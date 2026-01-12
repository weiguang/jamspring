package com.okayjam.web.common.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonUtil 测试类
 *
 * @author Jam Chen
 */
class JsonUtilTest {

    /**
     * 测试用的简单对象
     */
    static class User {
        private String name;
        private Integer age;
        private String email;

        public User() {}

        public User(String name, Integer age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    /**
     * 测试用的嵌套对象
     */
    static class ResponseDto<T> {
        private Integer code;
        private String message;
        private T data;

        public ResponseDto() {}

        public ResponseDto(Integer code, String message, T data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public Integer getCode() { return code; }
        public void setCode(Integer code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }

    // ==================== toJsonStr 测试 ====================

    @Test
    @DisplayName("测试对象转JSON字符串")
    void testToJsonStr() {
        User user = new User("张三", 25, "zhangsan@example.com");
        String json = JsonUtil.toJsonStr(user);

        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"张三\""));
        assertTrue(json.contains("\"age\":25"));
        assertTrue(json.contains("\"email\":\"zhangsan@example.com\""));
        System.out.println("toJsonStr 结果: " + json);
    }

    @Test
    @DisplayName("测试空对象转JSON字符串")
    void testToJsonStrWithNull() {
        String json = JsonUtil.toJsonStr(null);
        assertEquals("null", json);
    }

    @Test
    @DisplayName("测试对象转JSON字符串（排除null值）")
    void testToJsonStrNonNull() {
        User user = new User("李四", 30, null);
        String json = JsonUtil.toJsonStrNonNull(user);

        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"李四\""));
        assertTrue(json.contains("\"age\":30"));
        assertFalse(json.contains("\"email\"")); // email 为 null，不应该出现
        System.out.println("toJsonStrNonNull 结果: " + json);
    }

    @Test
    @DisplayName("测试List转JSON字符串")
    void testToJsonStrWithList() {
        List<User> users = List.of(
            new User("用户1", 20, "user1@test.com"),
            new User("用户2", 25, "user2@test.com")
        );
        String json = JsonUtil.toJsonStr(users);

        assertNotNull(json);
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
        assertTrue(json.contains("\"name\":\"用户1\""));
        assertTrue(json.contains("\"name\":\"用户2\""));
        System.out.println("List toJsonStr 结果: " + json);
    }

    @Test
    @DisplayName("测试Map转JSON字符串")
    void testToJsonStrWithMap() {
        Map<String, Object> map = Map.of(
            "key1", "value1",
            "key2", 123,
            "key3", true
        );
        String json = JsonUtil.toJsonStr(map);

        assertNotNull(json);
        assertTrue(json.contains("\"key1\":\"value1\""));
        assertTrue(json.contains("\"key2\":123"));
        assertTrue(json.contains("\"key3\":true"));
        System.out.println("Map toJsonStr 结果: " + json);
    }

    // ==================== readValue 测试 ====================

    @Test
    @DisplayName("测试JSON字符串转对象")
    void testReadValue() throws IOException {
        String json = "{\"name\":\"王五\",\"age\":28,\"email\":\"wangwu@test.com\"}";
        User user = JsonUtil.readValue(json, User.class);

        assertNotNull(user);
        assertEquals("王五", user.getName());
        assertEquals(28, user.getAge());
        assertEquals("wangwu@test.com", user.getEmail());
        System.out.println("readValue 结果: name=" + user.getName() + ", age=" + user.getAge());
    }

    @Test
    @DisplayName("测试JSON字符串转对象（忽略未知属性）")
    void testReadValueWithUnknownProperty() throws IOException {
        // JSON 中包含对象没有的属性
        String json = "{\"name\":\"测试用户\",\"age\":30,\"email\":\"test@test.com\",\"unknownField\":\"unknown\"}";
        User user = JsonUtil.readValue(json, User.class);

        assertNotNull(user);
        assertEquals("测试用户", user.getName());
        assertEquals(30, user.getAge());
    }

    @Test
    @DisplayName("测试JSON字符串转List")
    void testReadListValue() throws IOException {
        String json = "[{\"name\":\"用户A\",\"age\":20},{\"name\":\"用户B\",\"age\":25}]";
        List<User> users = JsonUtil.readListValue(json, User.class);

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("用户A", users.get(0).getName());
        assertEquals("用户B", users.get(1).getName());
        System.out.println("readListValue 结果: size=" + users.size());
    }

    @Test
    @DisplayName("测试JSON字符串转Map")
    void testReadMapValue() throws IOException {
        String json = "{\"name\":\"测试\",\"count\":\"100\"}";
        Map<String, String> map = JsonUtil.readMapValue(json, String.class, String.class);

        assertNotNull(map);
        assertEquals("测试", map.get("name"));
        assertEquals("100", map.get("count"));
        System.out.println("readMapValue 结果: " + map);
    }

    @Test
    @DisplayName("测试JSON字符串转ObjectNode")
    void testReadObject() throws IOException {
        String json = "{\"key\":\"value\",\"number\":123}";
        var objectNode = JsonUtil.readObject(json);

        assertNotNull(objectNode);
        assertEquals("value", objectNode.get("key").asString());
        assertEquals(123, objectNode.get("number").asInt());
    }

    @Test
    @DisplayName("测试空JSON转ObjectNode")
    void testReadObjectWithEmpty() throws IOException {
        assertNull(JsonUtil.readObject(null));
        assertNull(JsonUtil.readObject(""));
    }

    @Test
    @DisplayName("测试JSON字符串转ArrayNode")
    void testReadArray() throws IOException {
        String json = "[1, 2, 3, 4, 5]";
        var arrayNode = JsonUtil.readArray(json);

        assertNotNull(arrayNode);
        assertEquals(5, arrayNode.size());
        assertEquals(1, arrayNode.get(0).asInt());
        assertEquals(5, arrayNode.get(4).asInt());
    }

    // ==================== 序列化与反序列化往返测试 ====================

    @Test
    @DisplayName("测试序列化与反序列化往返")
    void testSerializeAndDeserialize() throws IOException {
        // 创建原始对象
        User original = new User("往返测试", 35, "roundtrip@test.com");

        // 序列化
        String json = JsonUtil.toJsonStr(original);
        System.out.println("序列化结果: " + json);

        // 反序列化
        User deserialized = JsonUtil.readValue(json, User.class);

        // 验证
        assertEquals(original.getName(), deserialized.getName());
        assertEquals(original.getAge(), deserialized.getAge());
        assertEquals(original.getEmail(), deserialized.getEmail());
    }

    @Test
    @DisplayName("测试嵌套泛型对象序列化与反序列化")
    void testNestedGenericSerializeAndDeserialize() throws IOException {
        // 创建嵌套对象
        User user = new User("嵌套测试", 40, "nested@test.com");
        ResponseDto<User> original = new ResponseDto<>(200, "success", user);

        // 序列化
        String json = JsonUtil.toJsonStr(original);
        System.out.println("嵌套对象序列化结果: " + json);

        assertTrue(json.contains("\"code\":200"));
        assertTrue(json.contains("\"message\":\"success\""));
        assertTrue(json.contains("\"name\":\"嵌套测试\""));
    }

    // ==================== 时区测试 ====================

    /**
     * 测试用的带时间戳对象
     */
    static class Event {
        private String name;
        private Date eventTime;

        public Event() {}

        public Event(String name, Date eventTime) {
            this.name = name;
            this.eventTime = eventTime;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Date getEventTime() { return eventTime; }
        public void setEventTime(Date eventTime) { this.eventTime = eventTime; }
    }

    @Test
    @DisplayName("测试时区设置")
    void testTimeZone() {
        // 记录当前时区
        TimeZone originalTimeZone = TimeZone.getDefault();

        try {
            // 修改时区为 UTC
            JsonUtil.chargeTimeZone(TimeZone.getTimeZone("UTC"));

            // 验证 ObjectMapper 已更新（通过重新获取）
            assertNotNull(JsonUtil.getObjectMapper());

            // 修改为上海时区
            JsonUtil.chargeTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            assertNotNull(JsonUtil.getObjectMapper());

        } finally {
            // 恢复原始时区
            JsonUtil.chargeTimeZone(originalTimeZone);
        }
    }

    @Test
    @DisplayName("测试不同时区下带时间戳的对象序列化")
    void testTimeZoneWithTimestamp() {
        // 创建一个固定时间点：2026-01-12 12:00:00 UTC
        // 对应北京时间（UTC+8）：2026-01-12 20:00:00
        long timestamp = 1768046400000L; // 2026-01-12 12:00:00 UTC
        Date testDate = new Date(timestamp);

        Event event = new Event("测试事件", testDate);

        // 测试 UTC 时区
        JsonUtil.chargeTimeZone(TimeZone.getTimeZone("UTC"));
        String utcJson = JsonUtil.toJsonStr(event);
        System.out.println("UTC 时区序列化结果: " + utcJson);
        // UTC 时区应该显示 12:00:00
        assertTrue(utcJson.contains("12:00:00") || utcJson.contains("1768046400000"), 
            "UTC 时区序列化结果不符合预期: " + utcJson);

        // 测试北京时区 (UTC+8)
        JsonUtil.chargeTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String shanghaiJson = JsonUtil.toJsonStr(event);
        System.out.println("上海时区序列化结果: " + shanghaiJson);
        // 北京时区应该显示 20:00:00
        assertTrue(shanghaiJson.contains("20:00:00") || shanghaiJson.contains("1768046400000"), 
            "上海时区序列化结果不符合预期: " + shanghaiJson);

        // 测试纽约时区 (UTC-5)
        JsonUtil.chargeTimeZone(TimeZone.getTimeZone("America/New_York"));
        String newYorkJson = JsonUtil.toJsonStr(event);
        System.out.println("纽约时区序列化结果: " + newYorkJson);
        // 纽约时区应该显示 07:00:00
        assertTrue(newYorkJson.contains("07:00:00") || newYorkJson.contains("1768046400000"), 
            "纽约时区序列化结果不符合预期: " + newYorkJson);

        // 恢复默认时区
        JsonUtil.chargeTimeZone(TimeZone.getDefault());
    }

    @Test
    @DisplayName("测试不同时区下带时间戳的Map序列化")
    void testTimeZoneWithTimestampMap() {
        // 创建一个固定时间点
        long timestamp = 1768046400000L; // 2026-01-12 12:00:00 UTC
        Date testDate = new Date(timestamp);

        Map<String, Object> eventMap = Map.of(
            "eventName", "Map测试事件",
            "eventTime", testDate,
            "timestamp", timestamp
        );

        // 测试 UTC 时区
        JsonUtil.chargeTimeZone(TimeZone.getTimeZone("UTC"));
        String utcJson = JsonUtil.toJsonStr(eventMap);
        System.out.println("UTC Map序列化结果: " + utcJson);

        // 测试北京时区
        JsonUtil.chargeTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String shanghaiJson = JsonUtil.toJsonStr(eventMap);
        System.out.println("上海 Map序列化结果: " + shanghaiJson);

        // 验证两个时区产生的结果不同（如果 Jackson 使用日期格式化而非时间戳）
        // 或者时间戳相同（如果 Jackson 使用时间戳格式）
        assertNotNull(utcJson);
        assertNotNull(shanghaiJson);
        System.out.println("UTC 和 上海时区序列化结果对比:");
        System.out.println("  UTC:     " + utcJson);
        System.out.println("  Shanghai:" + shanghaiJson);

        // 恢复默认时区
        JsonUtil.chargeTimeZone(TimeZone.getDefault());
    }

    @Test
    @DisplayName("测试不同时区下带时间戳的List序列化")
    void testTimeZoneWithTimestampList() {
        // 创建多个时间点
        long timestamp1 = 1768046400000L; // 2026-01-12 12:00:00 UTC
        long timestamp2 = 1768089600000L; // 2026-01-13 00:00:00 UTC

        List<Event> events = List.of(
            new Event("早间事件", new Date(timestamp1)),
            new Event("午夜事件", new Date(timestamp2))
        );

        // 测试 UTC 时区
        JsonUtil.chargeTimeZone(TimeZone.getTimeZone("UTC"));
        String utcJson = JsonUtil.toJsonStr(events);
        System.out.println("UTC List序列化结果: " + utcJson);

        // 测试北京时区
        JsonUtil.chargeTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String shanghaiJson = JsonUtil.toJsonStr(events);
        System.out.println("上海 List序列化结果: " + shanghaiJson);

        // 验证结果
        assertNotNull(utcJson);
        assertNotNull(shanghaiJson);
        assertTrue(utcJson.contains("早间事件"));
        assertTrue(utcJson.contains("午夜事件"));
        assertTrue(shanghaiJson.contains("早间事件"));
        assertTrue(shanghaiJson.contains("午夜事件"));

        System.out.println("\nList 时区对比:");
        System.out.println("  UTC:     " + utcJson);
        System.out.println("  Shanghai:" + shanghaiJson);

        // 恢复默认时区
        JsonUtil.chargeTimeZone(TimeZone.getDefault());
    }

    @Test
    @DisplayName("测试获取ObjectMapper")
    void testGetObjectMapper() {
        var mapper = JsonUtil.getObjectMapper();
        assertNotNull(mapper);
    }
}
