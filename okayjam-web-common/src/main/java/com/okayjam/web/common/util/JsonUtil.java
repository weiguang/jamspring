package com.okayjam.web.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * json工具类
 *
 * @author Jam Chen
 * @date 2021/09/13 20:27
 **/
public class JsonUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);

    private static volatile ObjectMapper mapper;

    private static volatile ObjectMapper mapperNonNull;

    /**
     * 默认时区
     */
    private static volatile TimeZone defaultTimeZone = TimeZone.getDefault();

    /**
     * 默认日期格式
     */
    private static volatile String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";

    static {
        initMappers(defaultTimeZone, defaultDateFormat);
    }

    /**
     * 初始化 ObjectMapper
     *
     * @param timeZone   时区
     * @param dateFormat 日期格式
     */
    private static void initMappers(TimeZone timeZone, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setTimeZone(timeZone);

        // Jackson 3.x 使用 JsonMapper.builder() 构建
        mapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .defaultTimeZone(timeZone)
                .defaultDateFormat(sdf)
                .build();

        SimpleDateFormat sdfNonNull = new SimpleDateFormat(dateFormat);
        sdfNonNull.setTimeZone(timeZone);

        mapperNonNull = JsonMapper.builder()
                .changeDefaultPropertyInclusion(v -> v.withValueInclusion(JsonInclude.Include.NON_NULL))
                .defaultTimeZone(timeZone)
                .defaultDateFormat(sdfNonNull)
                .build();
    }

    /**
     * 修改默认时区（会重新构建 ObjectMapper）
     *
     * @param timeZone 时区
     */
    public static synchronized void chargeTimeZone(TimeZone timeZone) {
        LOG.info("JsonUtil change time zone: {}", timeZone.getID());
        defaultTimeZone = timeZone;
        // Jackson 3.x 中 ObjectMapper 是不可变的，需要重新构建
        initMappers(timeZone, defaultDateFormat);
    }

    /**
     * 修改默认日期格式（会重新构建 ObjectMapper）
     *
     * @param dateFormat 日期格式，如 "yyyy-MM-dd HH:mm:ss"
     */
    public static synchronized void changeDateFormat(String dateFormat) {
        LOG.info("JsonUtil change date format: {}", dateFormat);
        defaultDateFormat = dateFormat;
        initMappers(defaultTimeZone, dateFormat);
    }

    /**
     * 同时修改时区和日期格式（会重新构建 ObjectMapper）
     *
     * @param timeZone   时区
     * @param dateFormat 日期格式
     */
    public static synchronized void configure(TimeZone timeZone, String dateFormat) {
        LOG.info("JsonUtil configure: timeZone={}, dateFormat={}", timeZone.getID(), dateFormat);
        defaultTimeZone = timeZone;
        defaultDateFormat = dateFormat;
        initMappers(timeZone, dateFormat);
    }

    /**
     * 获取当前日期格式
     *
     * @return 日期格式字符串
     */
    public static String getDateFormat() {
        return defaultDateFormat;
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
     * @param json  json
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
     * @param json  json
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
     * @param json         json
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
     * @param json  json
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
     * @param json       json
     * @param keyClass   keyClass
     * @param valueClass valueClass
     * @param <T>        T
     * @param <K>        K
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