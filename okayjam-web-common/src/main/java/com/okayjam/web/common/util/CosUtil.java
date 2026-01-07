package com.okayjam.web.common.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.endpoint.EndpointBuilder;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.internal.BucketNameUtils;
import com.qcloud.cos.internal.UrlComponentUtils;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.UploadResult;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import com.qcloud.cos.transfer.Upload;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.util.StringUtils;

/**
 * rms-scheduler
 *
 * @author JamChen
 * @date 2023/06/12 17:46
 **/
public class CosUtil implements AutoCloseable {

    private COSClient cosClient;

    public CosUtil(String secretId, String secretKey, String cosRegion) {
        this.cosClient = cosClient(secretId, secretKey, cosRegion);
    }

    public CosUtil(String secretId, String secretKey, String cosRegion, String endPoint) {
        this.cosClient = cosClient(secretId, secretKey, cosRegion, endPoint);
    }

    /**
     * 检查文件路径
     *
     * @param localFilePath 本地路径
     * @return 是否合法
     */
    private static boolean checkFilePath(String localFilePath) {
        // 1. 校验文件名，防止路径穿越
        if (localFilePath == null || localFilePath.isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        // 只允许字母、数字、下划线短横线、点号，禁止 "/", "\", ":"
        if (localFilePath.contains("..")) {
            throw new IllegalArgumentException("文件名包含非法字符");
        }
        return true;
    }

    /**
     * 创建 COSClient 实例
     *
     * @param secretId id
     * @param secretKey key
     * @param cosRegion region
     * @return COSClient 实例
     */
    public COSClient cosClient(String secretId, String secretKey, String cosRegion) {
        return cosClient(secretId, secretKey, cosRegion, null);
    }

    /**
     * 创建 COSClient 实例
     *
     * @param secretId id
     * @param secretKey key
     * @param cosRegion region
     * @param endpoint endpoint
     * @return COSClient 实例
     */
    public COSClient cosClient(String secretId, String secretKey, String cosRegion, String endpoint) {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(cosRegion));
        // 这里建议设置使用 https 协议
        // 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);

        if (StringUtils.hasText(endpoint)) {
            // 使用内网域名
//            String endpoint = String.format("cos-internal.%s.tencentcos.cn", cosRegion);
            // 使用公网域名
            // String endPoint = String.format("cos.%s.tencentcos.cn", region);
            clientConfig.setEndpointBuilder(new CosEndpointBuilder(endpoint));
        }

        // 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }

    /**
     * 创建 TransferManager 实例，这个实例用来后续调用高级接口
     *
     * @return
     */
    public TransferManager createTransferManager() {
        // 创建一个 COSClient 实例，这是访问 COS 服务的基础实例。
        // 详细代码参见本页: 简单操作 -> 创建 COSClient
//        COSClient cosClient = createCOSClient();

        // 自定义线程池大小，建议在客户端与 COS 网络充足（例如使用腾讯云的 CVM，同地域上传 COS）的情况下，设置成16或32即可，可较充分的利用网络资源
        // 对于使用公网传输且网络带宽质量不高的情况，建议减小该值，避免因网速过慢，造成请求超时。
        ExecutorService threadPool = Executors.newFixedThreadPool(32);

        // 传入一个 threadpool, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池。
        TransferManager transferManager = new TransferManager(cosClient, threadPool);

        // 设置高级接口的配置项
        // 分块上传阈值和分块大小分别为 5MB 和 1MB
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(5 * 1024 * 1024);
        transferManagerConfiguration.setMinimumUploadPartSize(1 * 1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);

        return transferManager;
    }

    public void close() {
        cosClient.shutdown();
    }

    /**
     * 上传文件
     *
     * @param bucketName 桶名称
     * @param key key
     * @param localFilePath 本地路径
     * @return 上传后的ETag
     */
    public String upload(String bucketName, String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, new File(localFilePath));
        TransferManager transferManager = createTransferManager();
        Upload upload = transferManager.upload(putObjectRequest);
        try {
            UploadResult uploadResult = upload.waitForUploadResult();
            transferManager.shutdownNow(false);
            return uploadResult.getETag();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

//        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
//        System.out.println(putObjectResult.getETag());
//        return putObjectResult.getETag();
    }

    /**
     * 上传文件
     *
     * @param bucketName 桶名称
     * @param key key
     * @param inputStream 输入流
     * @param size 文件大小
     * @return 上传后的ETag
     */
    public String upload(String bucketName, String key, InputStream inputStream, long size) {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 上传的流如果能够获取准确的流长度，则推荐一定填写 content-length
        // 如果确实没办法获取到，则下面这行可以省略，但同时高级接口也没办法使用分块上传了
        if (size > 0) {
            objectMetadata.setContentLength(size);
        }
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);

        TransferManager transferManager = createTransferManager();
        Upload upload = transferManager.upload(putObjectRequest);
        try {
            UploadResult uploadResult = upload.waitForUploadResult();
            transferManager.shutdownNow(false);
            return uploadResult.getETag();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 下载文件
     *
     * @param bucketName 桶名称
     * @param key key
     * @param localFilePath 本地目录
     * @return 文件本地路径
     */
    public String download(String bucketName, String key, String localFilePath) {
        checkFilePath(localFilePath);
        checkFilePath(key);
        // 方法2 下载文件到本地的路径，例如 D 盘的某个目录
        Path path = Paths.get(localFilePath, new File(key).getName());
        File downFile = path.toFile();
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        ObjectMetadata downObjectMeta = cosClient.getObject(getObjectRequest, downFile);
        return path.toString();

    }

    /**
     * 生成预授权URL
     *
     * @param bucketName 桶名称
     * @param key key
     * @return URL
     */
    public String genPresignedURL(String bucketName, String key) {
        return genPresignedURL(bucketName, key, null, new HashMap<>(), new HashMap<>());
    }

    /**
     * 生成预授权URL
     *
     * @param bucketName 桶名称
     * @param key key
     * @param expirationDate 过期时间
     * @param headers 请求头
     * @param params 参数
     * @return URL
     */
    public String genPresignedURL(String bucketName, String key, Date expirationDate, Map<String, String> headers,
            Map<String, String> params) {
        // 请求的 HTTP 方法，上传请求用 PUT，下载请求用 GET，删除请求用 DELETE
        HttpMethodName method = HttpMethodName.GET;
        //过期时间设置30天后
//        if (expirationDate == null) {
//            long expiration = 30 * 24 * 3600;
//            expirationDate = new Date(System.currentTimeMillis() + expiration * 1000);
//        }
        URL url = cosClient.generatePresignedUrl(bucketName, key, expirationDate, method, headers, params);
        return url.toString();
    }

    /**
     * 内部类
     */
    public class CosEndpointBuilder implements EndpointBuilder {

        private String cosEndpoint;
        private String cosGetServiceEndpoint = "service.cos.tencentcos.cn";

        public CosEndpointBuilder(String endpoint) {
            super();
            if (endpoint == null) {
                throw new IllegalArgumentException("endpoint must not be null");
            }
            while (endpoint.startsWith(".")) {
                endpoint = endpoint.substring(1);
            }
            UrlComponentUtils.validateEndPointSuffix(endpoint);
            this.cosEndpoint = endpoint.trim();
        }

        @Override
        public String buildGeneralApiEndpoint(String bucketName) {
            BucketNameUtils.validateBucketName(bucketName);
            return String.format("%s.%s", bucketName, this.cosEndpoint);
        }

        @Override
        public String buildGetServiceApiEndpoint() {
            return cosGetServiceEndpoint;
        }
    }


}
