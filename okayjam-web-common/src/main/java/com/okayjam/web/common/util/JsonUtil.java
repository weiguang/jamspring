package com.okayjam.web.common.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * json工具类
 *
 * @author Jam Chen
 * @date 2021/09/13 20:27
 **/
public class JsonUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper mapper;

    private static final ObjectMapper mapperNonNull;

    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 设置时区为系统默认时区
//        mapper.setTimeZone(TimeZone.getDefault());
        //mapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);
        mapperNonNull = new ObjectMapper();
        mapperNonNull.setSerializationInclusion(Include.NON_NULL);
        // 设置时区为系统默认时区
//        mapperNonNull.setTimeZone(TimeZone.getDefault());
    }

    public static void chargeTimeZone(TimeZone timeZone) {
        mapper.setTimeZone(timeZone);
        mapperNonNull.setTimeZone(timeZone);
        LOG.info("JsonUtil new time zone: {}", timeZone.getID());
    }


    /**
     * 获取 ObjectMapper
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return mapper;
    }

    /**
     * 将对象转成json字符串
     *
     * @param object 对象
     * @return String
     */
    public static String toJsonStr(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            LOG.error("parse object to json string error with object=" + object, e);
        }
        return "";
    }

    /**
     * 将对象转成json字符串
     *
     * @param object 对象
     * @return String
     */
    public static String toJsonStrNonNull(Object object) {
        try {
            return mapperNonNull.writeValueAsString(object);
        } catch (Exception e) {
            LOG.error("parse object to json string error with object=" + object, e);
        }
        return "";
    }

    /**
     * 读取json转成对象
     *
     * @param json json
     * @param clazz clazz
     * @return T
     * @throws IOException IOException
     */
    public static <T> T readValue(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, clazz);
    }

    /**
     * 读取json转成对象, 具有多层嵌套的数据，如 {@code ResponseDto<List<Object>> }
     *
     * @param json json
     * @param clazz clazz
     * @return T
     * @throws IOException IOException
     */
    public static <T> T readValue(String json, Class<T> clazz, Class... clazz1) throws IOException {
        if (clazz1.length == 1) {
            return mapper.readValue(json, getObjectMapper().getTypeFactory().constructParametricType(clazz, clazz1[0]));
        }
        JavaType subType = JsonUtil.getObjectMapper().getTypeFactory()
                .constructParametricType(clazz1[clazz1.length - 2], clazz1[clazz1.length - 1]);
        for (int i = clazz1.length - 3; i > 0; i--) {
            subType = JsonUtil.getObjectMapper().getTypeFactory().constructParametricType(clazz1[i], subType);
        }
        JavaType reType = JsonUtil.getObjectMapper().getTypeFactory().constructParametricType(clazz, subType);
        return mapper.readValue(json, reType);
    }


    /**
     * 基于TypeReference转换
     *
     * @param json json
     * @param valueTypeRef valueTypeRef
     * @return T
     * @throws IOException IOException
     */
    public static <T> T readValue(String json, TypeReference<T> valueTypeRef) throws IOException {
        return mapper.readValue(json, valueTypeRef);
    }

    /**
     * 获取List集合
     *
     * @param json json
     * @param clazz clazz
     * @return T
     * @throws IOException IOException
     */
    public static <T> List<T> readListValue(String json, Class<T> clazz) throws IOException {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
        return mapper.readValue(json, javaType);
    }

    /**
     * 获取 Map数据
     *
     * @param json json
     * @param keyClass keyClass
     * @param valueClass valueClass
     * @param <T> T
     * @param <K> K
     * @return Map
     * @throws IOException IOException
     */
    public static <T, K> Map<T, K> readMapValue(String json, Class<T> keyClass, Class<K> valueClass)
            throws IOException {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(HashMap.class, keyClass, valueClass);
        return mapper.readValue(json, javaType);
    }


    /**
     * 读取json转成ObjectNode
     *
     * @param json json
     * @return ObjectNode
     * @throws IOException IOException
     */
    public static ObjectNode readObject(String json) throws IOException {
        if (json == null || "".equals(json)) {
            return null;
        }

        return mapper.readValue(json, ObjectNode.class);
    }

    /**
     * 读取json转成ArrayNode
     *
     * @param json json
     * @return ArrayNode
     * @throws IOException IOException
     */
    public static ArrayNode readArray(String json) throws IOException {
        if (json == null || "".equals(json)) {
            return null;
        }

        return mapper.readValue(json, ArrayNode.class);
    }


}
