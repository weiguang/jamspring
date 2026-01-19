# 节假日查询服务使用说明

## 📋 概述

本模块提供了基于 `HttpClientFactory` 的节假日查询服务，使用 Spring RestClient 进行 HTTP 请求，调用 timor.tech 提供的免费节假日
API。

### ✨ 核心特性

- ✅ **延迟加载**：使用 `@Lazy` 注解，只在首次调用时初始化，加快应用启动速度
- ✅ **连接池管理**：自动管理 HTTP 连接池，提高性能
- ✅ **TraceId 追踪**：自动传递 `X-Request-UUID` 请求头，支持全链路日志追踪
- ✅ **多种查询方式**：支持年度查询、批量查询、未来N天查询、下一个节假日查询等
- ✅ **Swagger 集成**：自动生成 API 文档，方便测试

## 🏗️ 架构设计

```
external/
├── HolidayService.java              # 节假日服务接口
├── impl/
│   └── HolidayServiceImpl.java      # 节假日服务实现类（@Lazy 延迟加载）
└── dto/
    ├── HolidayResponse.java          # 年度节假日响应 DTO
    ├── HolidayBatchResponse.java     # 批量查询响应 DTO
    └── NextHolidayResponse.java      # 下一个节假日响应 DTO
```

## 🔧 技术栈

- **HTTP 客户端**: Spring RestClient + Apache HttpClient 5
- **连接池管理**: HttpClientFactory（来自 common 模块）
- **日志拦截**: HttpLoggingInterceptor
- **JSON 序列化**: Jackson

## ⚙️ 配置说明

在 `application.yml` 中配置节假日 API：

```yaml
holiday:
  api:
    base-url: http://timor.tech/api/holiday  # 节假日 API 基础地址
```

## 📡 API 接口

### 1. 获取未来N天内的节假日 ⭐ 推荐

获取从今天开始未来N天内的所有节假日信息。

**请求示例：**

```bash
# 默认查询未来20天
curl -X GET "http://localhost:8080/api/holiday/upcoming"

# 自定义天数，比如查询未来30天
curl -X GET "http://localhost:8080/api/holiday/upcoming?days=30"

# 带 TraceId 的请求
curl -X GET "http://localhost:8080/api/holiday/upcoming?days=20" \
  -H "X-Request-UUID: 12345678-1234-1234-1234-123456789abc"
```

**响应示例：**

```json
{
  "code": 0,
  "holiday": {
    "2026-01-01": {
      "holiday": true,
      "name": "元旦",
      "wage": 3
    },
    "2026-01-02": {
      "holiday": true,
      "name": "元旦",
      "wage": 2
    }
  },
  "type": {
    "2026-01-01": {
      "type": 2,
      "name": "元旦",
      "week": 4
    }
  }
}
```

### 2. 获取下一个节假日

获取距离当前时间最近的下一个节假日，如果节假日前有调休，也会一起返回。

**请求示例：**

```bash
# 不包含周末
curl -X GET "http://localhost:8080/api/holiday/next"

# 包含周末
curl -X GET "http://localhost:8080/api/holiday/next?includeWeekend=true"

# 指定日期查询
curl -X GET "http://localhost:8080/api/holiday/next?date=2026-01-01"
```

**响应示例：**

```json
{
  "code": 0,
  "holiday": {
    "holiday": true,
    "name": "春节",
    "wage": 3,
    "date": "2026-02-17",
    "rest": 40
  },
  "workday": {
    "holiday": false,
    "name": "春节前补班",
    "wage": 1,
    "after": false,
    "target": "春节",
    "date": "2026-02-14",
    "rest": 37
  }
}
```

### 3. 批量查询指定日期

一次性查询多个日期的节假日信息（最多50个）。

**请求示例：**

```bash
curl -X POST "http://localhost:8080/api/holiday/batch?includeType=true" \
  -H "Content-Type: application/json" \
  -H "X-Request-UUID: 12345678-1234-1234-1234-123456789abc" \
  -d '["2026-01-01", "2026-02-16", "2026-02-17"]'
```

**响应示例：**

```json
{
  "code": 0,
  "holiday": {
    "2026-01-01": {
      "holiday": true,
      "name": "元旦",
      "wage": 3
    },
    "2026-02-16": {
      "holiday": true,
      "name": "除夕",
      "wage": 3
    },
    "2026-02-17": null
  },
  "type": {
    "2026-01-01": {
      "type": 2,
      "name": "元旦",
      "week": 4
    }
  }
}
```

### 4. 查询当前年份节假日

**请求示例：**

```bash
curl -X GET "http://localhost:8080/api/holiday/year"
```

### 5. 查询指定年份节假日

**请求示例：**

```bash
curl -X GET "http://localhost:8080/api/holiday/year/2026"
```

### 6. 查询指定年月节假日

**请求示例：**

```bash
curl -X GET "http://localhost:8080/api/holiday/year/2026/02"
```

**响应示例：**

```json
{
  "code": 0,
  "holiday": {
    "01-01": {
      "holiday": true,
      "name": "元旦",
      "wage": 3,
      "date": "2026-01-01",
      "rest": 18
    },
    "01-02": {
      "holiday": true,
      "name": "元旦",
      "wage": 2,
      "date": "2026-01-02",
      "rest": 1
    },
    "02-16": {
      "holiday": true,
      "name": "除夕",
      "wage": 3,
      "date": "2026-02-16",
      "rest": 1
    },
    "02-17": {
      "holiday": true,
      "name": "初一",
      "wage": 3,
      "date": "2026-02-17",
      "rest": 1
    }
  }
}
```

## 💻 代码使用示例

### 在 Service 中注入使用

```java

@Service
public class YourService {

    @Autowired
    private HolidayService holidayService;

    public void checkHoliday() {
        // 查询当前年份节假日
        HolidayResponse holidays = holidayService.getCurrentYearHolidays();

        // 查询指定年份节假日
        HolidayResponse holidays2026 = holidayService.getYearHolidays(2026);

        // 查询指定年月节假日
        HolidayResponse feb2026 = holidayService.getMonthHolidays(2026, "02");

        // 判断某天是否为节假日
        HolidayResponse.HolidayInfo info = holidays.getHoliday().get("01-01");
        if (info != null && info.getHoliday()) {
            System.out.println("今天是节假日：" + info.getName());
            System.out.println("薪资倍数：" + info.getWage());
        }
    }
}
```

### 在 Controller 中使用

```java

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private HolidayService holidayService;

    @GetMapping("/is-holiday")
    public String checkTodayIsHoliday() {
        HolidayResponse holidays = holidayService.getCurrentYearHolidays();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd"));

        HolidayResponse.HolidayInfo info = holidays.getHoliday().get(today);
        if (info != null && info.getHoliday()) {
            return "今天是节假日：" + info.getName() + "，薪资倍数：" + info.getWage();
        } else if (info != null && info.getAfter() != null && info.getAfter()) {
            return "今天是补班日：" + info.getName();
        } else {
            return "今天是工作日";
        }
    }
}
```

## 📊 响应字段说明

### HolidayResponse 字段

| 字段      | 类型      | 说明                    |
|---------|---------|-----------------------|
| code    | Integer | 响应码，0表示成功             |
| holiday | Map     | 节假日数据，key为日期（MM-dd格式） |

### HolidayInfo 字段

| 字段      | 类型      | 说明                   |
|---------|---------|----------------------|
| holiday | Boolean | 是否为节假日               |
| name    | String  | 节假日名称                |
| wage    | Integer | 薪资倍数（1=正常，2=双倍，3=三倍） |
| date    | String  | 日期（yyyy-MM-dd格式）     |
| rest    | Integer | 距离下一个节假日的天数          |
| after   | Boolean | 是否为补班日               |
| target  | String  | 补班/调休的目标节假日          |

## 🔍 HttpClientFactory 特性

节假日服务使用了 `HttpClientFactory` 提供的以下特性：

1. **连接池管理**: 自动管理 HTTP 连接池，提高性能
2. **超时配置**: 支持连接超时和读取超时配置
3. **请求日志**: 自动记录请求和响应日志（包含 Trace ID）
4. **异常处理**: 统一的异常处理机制
5. **重试机制**: 自动重试失败的请求
6. **延迟加载**: 使用 `@Lazy` 注解，只在首次调用时初始化

## 🔗 TraceId 全链路追踪

### 工作原理

系统通过 `TraceIdFilter` 和 `HttpLoggingInterceptor` 实现全链路日志追踪：

1. **请求入口**：`TraceIdFilter` 拦截所有 HTTP 请求
    - 从请求头中读取 `X-Request-UUID`
    - 如果存在则使用，否则自动生成新的 UUID
    - 将 TraceId 存入 MDC（Mapped Diagnostic Context）

2. **日志记录**：所有日志自动包含 TraceId
   ```
   [1bfd4428cd1a4843beb2c5636011cc9d] 接收到查询当前年份节假日请求
   ```

3. **外部调用**：`HttpLoggingInterceptor` 自动传递 TraceId
    - 从 MDC 中获取 TraceId
    - 添加到外部 HTTP 请求的 `X-Request-UUID` 请求头
    - 实现跨服务的链路追踪

### 使用方式

#### 1. 客户端传入 TraceId

```bash
# 使用自定义的 TraceId
curl -X GET "http://localhost:8080/api/holiday/year" \
  -H "X-Request-UUID: my-custom-trace-id-12345"
```

#### 2. 自动生成 TraceId

```bash
# 不传 X-Request-UUID，系统自动生成
curl -X GET "http://localhost:8080/api/holiday/year"
```

#### 3. 在代码中获取 TraceId

```java
import org.slf4j.MDC;
import com.okayjam.web.common.util.HttpUtil;

public class YourService {
    public void yourMethod() {
        // 获取当前请求的 TraceId
        String traceId = MDC.get(HttpUtil.TRACE_ID);
        log.info("当前 TraceId: {}", traceId);
    }
}
```

#### 4. 在外部调用中传递 TraceId

使用 `HttpUtil` 工具类时，会自动传递 TraceId：

```java
import com.okayjam.web.common.util.HttpUtil;

public class YourService {
    public void callExternalApi() throws IOException {
        Map<String, String> headers = new HashMap<>();
        // HttpUtil 会自动添加 X-Request-UUID 请求头
        String response = HttpUtil.get("https://api.example.com/data", headers);
    }
}
```

### TraceId 常量定义

在 `HttpUtil` 类中定义了相关常量：

```java
public static final String TRACE_ID = "TRACE_ID";        // MDC 中的 key
public static final String REQUEST_ID = "X-Request-UUID"; // HTTP 请求头的 key
```

## 📊 日志示例

```
[HolidayAPI] >>> Request: GET http://timor.tech/api/holiday/year/2026/ | Body: 
[HolidayAPI] <<< Response: 200 (Cost: 156ms) | Body: {"code":0,"holiday":{...}}
```

## 🎯 API 说明

### 重要提示

根据 timor.tech API 的要求：

- **查询整年数据时，必须在年份后面加斜杠 `/`**
- 示例：`/api/holiday/year/2026/`（注意末尾的斜杠）
- 查询指定月份时不需要斜杠：`/api/holiday/year/2026-02`

### API 地址格式

| 功能   | URL 格式                 | 示例                                           |
|------|------------------------|----------------------------------------------|
| 当前年份 | `/year`                | `http://timor.tech/api/holiday/year`         |
| 指定年份 | `/year/{year}/`        | `http://timor.tech/api/holiday/year/2026/`   |
| 指定年月 | `/year/{year}-{month}` | `http://timor.tech/api/holiday/year/2026-02` |

## 🚀 扩展建议

1. **添加缓存**: 使用 Spring Cache 缓存节假日数据，减少 API 调用
   ```java
   @Cacheable(value = "holidays", key = "#year")
   public HolidayResponse getYearHolidays(Integer year) {
       // ...
   }
   ```

2. **本地存储**: 将节假日数据存储到数据库，定期更新

3. **工具方法**: 添加便捷的工具方法
   ```java
   // 判断指定日期是否为节假日
   boolean isHoliday(LocalDate date);
   
   // 获取指定日期的薪资倍数
   int getWageMultiple(LocalDate date);
   
   // 计算工作日
   int countWorkdays(LocalDate start, LocalDate end);
   ```

4. **异常处理**: 添加更详细的异常处理和降级策略

5. **定时更新**: 使用定时任务定期更新节假日数据

6. **监控告警**: 添加 API 调用失败的监控和告警

## 📝 注意事项

1. **延迟加载**：`HolidayService` 使用 `@Lazy` 注解，只在首次调用时初始化，不会影响应用启动速度
2. **免费 API**：timor.tech 提供的是免费 API，请合理使用，避免频繁调用
3. **缓存建议**：建议添加缓存机制，节假日数据通常不会频繁变化
4. **调休信息**：API 返回的数据包含了调休和补班信息，注意区分
5. **薪资计算**：薪资倍数字段可用于计算加班工资（1=正常，2=双倍，3=三倍）
6. **TraceId 追踪**：所有外部 API 调用自动传递 `X-Request-UUID` 请求头，支持全链路追踪
7. **批量查询限制**：批量查询接口最多支持 50 个日期

## 🔗 相关文档

- [timor.tech 节假日 API](http://timor.tech/api/holiday)
- [HttpClientFactory 文档](../okayjam-web-common/src/main/java/com/okayjam/web/common/configuration/http/HttpClientFactory.java)
- [Spring RestClient 文档](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html)
- [Apache HttpClient 5 文档](https://hc.apache.org/httpcomponents-client-5.3.x/)

## 🎉 快速测试

启动应用后，访问 Swagger UI：

```
http://localhost:8080/swagger-ui.html
```

在"节假日查询"分组中可以直接测试所有接口。
