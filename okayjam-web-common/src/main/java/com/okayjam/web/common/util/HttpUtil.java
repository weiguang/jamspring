package com.okayjam.web.common.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.net.ssl.X509TrustManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody.Builder;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * HttpUtil
 *
 * @author Jam chen
 * @description HTTP一些工具实现
 * @create 2018/07/24  15:17
 **/

public class HttpUtil {

    public static final String HTTP_GET = "GET";
    public static final String HTTP_PUT = "PUT";
    public static final String HTTP_POST = "POST";
    public static final int HTTP_CODE_OK = 200;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static final MediaType FORM
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final String TRACE_ID = "TRACE_ID";
    public static final String REQUEST_ID = "X-Request-UUID";
    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);
    private static final int HTTP_REQUEST_TIMEOUT = 60 * 2;
    static OkHttpClient client;

    static {
        X509TrustManager manager = SSLSocketClientUtil.getX509TrustManager();
        client = new OkHttpClient.Builder()
                .readTimeout(HTTP_REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(HTTP_REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .followRedirects(true)
                // 忽略校验
                .sslSocketFactory(SSLSocketClientUtil.getSocketFactory(manager), manager)
                .hostnameVerifier(SSLSocketClientUtil.getHostnameVerifier())
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                .retryOnConnectionFailure(true)
                .build();
    }


    public static OkHttpClient getClient() {
        return client;
    }


    /**
     * 请求连接
     *
     * @param url 请求地址
     * @param requestMethod 请求方法
     * @param headerMap 请求头
     * @param params 请求参数
     * @param isJson 是否是json请求
     * @return Call
     */
    public static Call getConnection(String url, String requestMethod, Map<String, String> headerMap,
            Map<String, Object> params, Boolean isJson) {
        Request.Builder reqBuilder = new Request.Builder();
        if (headerMap == null) {
            headerMap = new HashMap<>();
        }
        // 设置request id
        if (!headerMap.containsKey(REQUEST_ID)) {
            if (MDC.get(TRACE_ID) == null) {
                MDC.put(TRACE_ID, UUID.randomUUID().toString().replaceAll("-", ""));
            }
            headerMap.put(REQUEST_ID, MDC.get(TRACE_ID));
        }

        reqBuilder.headers(Headers.of(headerMap));

        RequestBody body;
        if (params != null) {
            if (isJson) {
                body = RequestBody.Companion.create(JsonUtil.toJsonStr(params), JSON);
            } else {
                Builder builder = new Builder();
                params.keySet().forEach(k -> {
                    Object t = params.get(k);
                    if (t != null) {
                        builder.add(k, params.get(k).toString());
                    }
                });
                body = builder.build();
            }
        } else {
            body = RequestBody.Companion.create(new byte[0], null);
        }

        // set default method
        if (requestMethod == null || HTTP_GET.equalsIgnoreCase(requestMethod)) {
            requestMethod = HTTP_GET;
            body = null;
            if (params != null) {
                url += url.contains("?") ? "&" : "?" + genParamString(params);
            }
        }

        reqBuilder.method(requestMethod, body).url(url);

        return getClient().newCall(reqBuilder.build());
    }

    /**
     * 生成CONNECTION
     *
     * @param url 网址
     * @param headerMap 请求头
     * @param json json
     * @return 返回call
     */
    public static Call getConnection(String url, String requestMethod, Map<String, String> headerMap, String json) {
        Request.Builder reqBuilder = new Request.Builder();
        if (headerMap == null) {
            headerMap = new HashMap<>();
        }
        // 设置request id
        if (!headerMap.containsKey(REQUEST_ID)) {
            if (MDC.get(TRACE_ID) == null) {
                MDC.put(TRACE_ID, UUID.randomUUID().toString().replaceAll("-", ""));
            }
            headerMap.put(REQUEST_ID, MDC.get(TRACE_ID));
        }

        reqBuilder.headers(Headers.of(headerMap));

        RequestBody body;
        if (json != null) {
            if (headerMap != null && headerMap.get("Content-Type") != null) {
                body = RequestBody.Companion.create(json, MediaType.parse(headerMap.get("Content-Type")));
            } else {
                body = RequestBody.Companion.create(json, JSON);
            }
        } else {
            body = RequestBody.Companion.create(new byte[0], null);
        }

        reqBuilder.method(requestMethod, body).url(url);

        return getClient().newCall(reqBuilder.build());
    }


    /**
     * 基础请求
     *
     * @param url url
     * @param headerMap 请求头
     * @return 返回response
     * @throws IOException 异常
     */
    public static String request(String url, String method, Map<String, String> headerMap, String json)
            throws IOException {
        Call conn = getConnection(url, method, headerMap, json);
        log.info("http request:{}, body:{}", conn.request(), json);
        return getResponse(conn);
    }

    /**
     * 基础请求
     *
     * @param url url
     * @param method 请求方法
     * @param headerMap 请求头
     * @param params 参数
     * @param isJson 是否是json
     * @return 返回response
     * @throws IOException 异常
     */
    public static String request(String url, String method, Map<String, String> headerMap, Map<String, Object> params,
            Boolean isJson) throws IOException {
        Call conn = getConnection(url, method, headerMap, params, isJson);
        log.info("http request:{}, body:{}", conn.request(), params);
        return getResponse(conn);
    }

    @NotNull
    private static String getResponse(Call conn) throws IOException {
        Response response = conn.execute();
//        BufferedSource source = Objects.requireNonNull(response.body()).source();
//        source.request(Long.MAX_VALUE);
//        String body = source.getBuffer().clone().readString(StandardCharsets.UTF_8);
        String body = response.body().string();
//        Headers headers = response.headers();
        log.info("response: {}, body:{} ", response, body);
        if (response.code() != HTTP_CODE_OK) {
            throw new IOException("http request error, code:" + response.code() + ", body:" + body);
        }
        return body;
    }


    /**
     * uploadFile
     *
     * @param url 请求地址
     * @param headerMap 请求头
     * @param params 请求参数
     * @param filePath 文件路径
     * @return Response
     * @throws IOException 异常
     */
    public static String uploadFile(String url, Map<String, String> headerMap, Map<String, Object> params,
            String filePath) throws IOException {
        File file = new File(filePath);
        if (params == null) {
            params = new HashMap<>();
        }
        RequestBody requestBody = RequestBody.Companion.create(file, MediaType.get("application/octet-stream"));

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        for (String key : params.keySet()) {
            builder.addFormDataPart(key, (String) params.get(key));
        }
        if (params.get("fileName") != null) {
            builder.addFormDataPart("file", (String) params.get("fileName"), requestBody);
        } else {
            builder.addFormDataPart("file", file.getName(), requestBody);
        }
        MultipartBody multipartBody = builder.build();
        Request request = new Request.Builder()
                .headers(Headers.of(headerMap != null ? headerMap : new HashMap<>()))
                .url(url)
                .post(multipartBody)
                .build();
        Call conn = client.newCall(request);
        return getResponse(conn);
    }

    /**
     * get请求
     *
     * @param url 地址
     * @param headerMap 头函数
     * @return 返回Response
     * @throws IOException 异常
     */
    public static String get(String url, Map<String, String> headerMap) throws IOException {
        return request(url, HTTP_GET, headerMap, null, false);
    }

    /**
     * post
     *
     * @param url 地址
     * @param headerMap 请求头
     * @param params 参数
     * @return 返回
     * @throws IOException 异常
     */
    public static String post(String url, Map<String, String> headerMap, Map<String, Object> params)
            throws IOException {
        return request(url, HTTP_POST, headerMap, params, true);
    }

    /**
     * post
     *
     * @param url 地址
     * @param headerMap 请求头
     * @param json 参数
     * @return 返回
     * @throws IOException 异常
     */
    public static String post(String url, Map<String, String> headerMap, String json) throws IOException {
        return request(url, HTTP_POST, headerMap, json);
    }

    /**
     * postForm
     *
     * @param url 地址
     * @param headerMap 请求头
     * @param params 参数
     * @return 返回
     * @throws IOException 异常
     */

    public static String postForm(String url, Map<String, String> headerMap, Map<String, Object> params)
            throws IOException {
        return request(url, HTTP_POST, headerMap, params, false);
    }


    /**
     * requestAsync
     *
     * @param url 请求地址
     * @param method 请求方法
     * @param headerMap 请求头
     * @param params 请求参数
     * @param isJson 是否是json
     */
    public static void requestAsync(String url, String method, Map<String, String> headerMap,
            Map<String, Object> params, Boolean isJson) {
        Call conn = getConnection(url, method, headerMap, params, isJson);
        conn.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.info("get failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("11111111111111");
                log.info("Response:" + response.body().string());
            }
        });

    }

    /**
     * 获取字符串信息
     *
     * @param params 参数
     * @return 返回字符串
     */
    public static String genParamString(Map<String, Object> params) {
        if (params == null) {
            return null;
        }
        String encodeBank = "%20";
        String strParams = params.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"))
                // 替换参数的空格
                .replaceAll(" ", encodeBank);
        return strParams;
    }

    /**
     * urlParamToMap
     *
     * @param urlStr url
     * @return map
     * @throws MalformedURLException 异常
     */
    public static Map<String, String> urlParamToMap(String urlStr) throws MalformedURLException {
        URL url1 = new URL(urlStr);
        Map<String, String> mapRequest = new HashMap<>();
        String[] arrSplit;
        String query = url1.getQuery();
        arrSplit = query.split("&");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual;
            arrSplitEqual = strSplit.split("=");
            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (!Objects.equals(arrSplitEqual[0], "")) {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

}
