# JamSpring

> 一个基于 Spring Boot 4.x 的企业级 Web 应用脚手架项目

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![MyBatis](https://img.shields.io/badge/MyBatis-4.0.1-red.svg)](https://mybatis.org/mybatis-3/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## 📖 目录

- [项目简介](#项目简介)
- [技术栈](#技术栈)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [配置说明](#配置说明)
- [功能特性](#功能特性)
- [开发指南](#开发指南)
- [API 文档](#api-文档)
- [常见问题](#常见问题)
- [参考文档](#参考文档)

## 项目简介

**JamSpring** 是一个开箱即用的 Spring Boot Web 应用脚手架项目，旨在帮助开发者快速搭建企业级 Web 应用。项目采用多模块架构设计，集成了常用的开发组件和最佳实践。

### 核心特性

- ✅ **多模块架构**：清晰的模块划分，便于团队协作和代码复用
- ✅ **MyBatis 集成**：支持 MySQL 和 SQLite 双数据库，内置分页插件
- ✅ **分布式锁**：基于数据库实现的分布式锁组件
- ✅ **API 文档**：集成 SpringDoc OpenAPI，自动生成 Swagger UI
- ✅ **日志追踪**：支持 TraceId 全链路追踪，JSON 格式日志输出
- ✅ **热部署**：集成 Spring DevTools，提升开发效率
- ✅ **多环境配置**：支持 dev、test、prod 环境切换

### 适用场景

- 快速搭建 RESTful API 服务
- 企业级 Web 应用开发
- 微服务架构中的单体服务
- 学习 Spring Boot 最佳实践

## 技术栈

### 核心框架

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 4.0.1 | 核心框架 |
| Java | 21 | 开发语言 |
| Maven | 3.6+ | 项目管理工具 |

### 数据访问层

| 技术 | 版本 | 说明 |
|------|------|------|
| MyBatis | 4.0.1 | ORM 框架 |
| MyBatis Spring Boot Starter | 4.0.1 | MyBatis 自动配置 |
| PageHelper | 2.1.1 | 分页插件 |
| HikariCP | - | 数据库连接池（Spring Boot 默认） |

### 数据库支持

| 数据库 | 版本 | 说明 |
|--------|------|------|
| MySQL | 8.4.0 | 生产环境推荐 |
| SQLite | 3.x | 开发测试环境 |

### 其他组件

| 技术 | 版本 | 说明 |
|------|------|------|
| SpringDoc OpenAPI | 2.8.14 | API 文档生成 |
| Logback | - | 日志框架 |
| Logstash Logback Encoder | 9.0 | JSON 日志编码器 |
| OkHttp | 4.12.0 | HTTP 客户端 |
| Caffeine | - | 本地缓存 |
| Lombok | - | 代码简化工具 |
| Spring DevTools | - | 热部署工具 |

## 环境要求

在开始之前，请确保您的开发环境满足以下要求：

| 环境 | 版本要求 | 说明 |
|------|----------|------|
| **JDK** | 21 或更高版本 | 必须，Spring Boot 4.x 要求 |
| **Maven** | 3.6+ | 必须，用于项目构建 |
| **数据库** | MySQL 8.0+ 或 SQLite 3.x | 必须，至少配置一种 |
| **IDE** | IntelliJ IDEA / Eclipse | 推荐，支持 Spring Boot 开发 |

### 端口占用

- 默认服务端口：`8080`
- 请确保该端口未被占用，或在配置文件中修改端口号

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd jamspring
```

### 2. 配置数据库

编辑 `okayjam-web-core/src/main/resources/application.yml`，选择数据库配置：

**使用 SQLite（默认，无需额外安装）：**

```yaml
spring:
  datasource:
    driver-class-name: org.sqlite.JDBC
    url: jdbc:sqlite:D:\UGit\java\jamspring\jam.db
    username:
    password:
```

**使用 MySQL：**

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=UTF-8&useSSL=false
    username: your_username
    password: your_password
```

### 3. 构建项目

```bash
mvn clean install
```

### 4. 运行应用

**方式一：使用 Maven 命令**

```bash
cd okayjam-web-core
mvn spring-boot:run
```

**方式二：在 IDE 中运行**

直接运行 `com.okayjam.web.ServiceApplication` 类的 `main` 方法

**方式三：运行 JAR 包**

```bash
cd okayjam-web-core/target
java -jar okayjam-web-core-1.0.0-SNAPSHOT.jar
```

**方式四：使用 service.sh 脚本（推荐生产环境）**

项目提供了 `service.sh` 启动脚本，支持 start、stop、restart 操作：

```bash
# 启动服务
bash service.sh start

# 停止服务
bash service.sh stop

# 重启服务
bash service.sh restart
```

脚本特性：
- ✅ 后台运行（使用 nohup）
- ✅ 进程检测（避免重复启动）
- ✅ 优雅停止（最多等待 120 秒）
- ✅ JVM 参数配置（Sentinel、FastJson、HeapDump）

### 5. 验证启动

启动成功后，访问以下地址：

- **应用首页**：http://localhost:8080
- **Swagger UI**：http://localhost:8080/swagger-ui.html
- **API 文档**：http://localhost:8080/v3/api-docs

看到 Swagger UI 页面即表示启动成功！

## 项目结构

```
jamspring/
├── okayjam-web-core/              # 核心业务模块（程序入口）
│   ├── src/main/java/
│   │   └── com/okayjam/web/core/
│   │       ├── ServiceApplication.java    # 启动类
│   │       ├── configuration/             # 配置类
│   │       ├── controller/                # 控制器层
│   │       ├── service/                   # 业务逻辑层
│   │       ├── dao/                       # 数据访问层
│   │       └── entity/                    # 实体类
│   ├── src/main/resources/
│   │   ├── application.yml                # 主配置文件
│   │   ├── logback-spring.xml             # 日志配置
│   │   └── mapper/                        # MyBatis Mapper XML
│   └── pom.xml
│
├── okayjam-web-common/            # 公共工具模块
│   ├── src/main/java/
│   │   └── com/okayjam/web/common/
│   │       ├── utils/                     # 工具类
│   │       ├── constants/                 # 常量定义
│   │       ├── exception/                 # 异常处理
│   │       └── response/                  # 统一响应封装
│   └── pom.xml
│
├── distributed-lock/              # 分布式锁模块
│   ├── src/main/java/
│   │   └── com/okayjam/web/lock/
│   │       ├── DistributedLock.java       # 分布式锁接口
│   │       └── impl/                      # 实现类
│   └── pom.xml
│
├── pom.xml                        # 父 POM 文件
└── README.md                      # 项目说明文档
```

### 模块说明

#### okayjam-web-core（核心模块）

- **程序入口**：`ServiceApplication.java`
- **职责**：包含应用的核心业务逻辑、控制器、服务层和数据访问层
- **依赖**：依赖 `okayjam-web-common` 和 `distributed-lock` 模块

#### okayjam-web-common（公共模块）

- **职责**：提供通用工具类、常量定义、异常处理、统一响应封装等
- **特点**：可被其他模块复用，不包含业务逻辑

#### distributed-lock（分布式锁模块）

- **职责**：提供基于数据库的分布式锁实现
- **特点**：独立模块，可单独使用或集成到其他项目

### 代码分层

```
controller/     # 控制器层：处理 HTTP 请求，参数校验
    ↓
service/        # 业务逻辑层：核心业务处理
    ↓
dao/            # 数据访问层：数据库操作（MyBatis Mapper）
    ↓
entity/         # 实体类：数据库表映射对象
```

## 配置说明

### 主配置文件

配置文件位置：`okayjam-web-core/src/main/resources/application.yml`

### 数据库配置

**MySQL 配置示例：**

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=UTF-8&useSSL=false
    username: your_username
    password: your_password
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

**SQLite 配置示例：**

```yaml
spring:
  datasource:
    driver-class-name: org.sqlite.JDBC
    url: jdbc:sqlite:./jam.db
    username:
    password:
```

### 多环境配置

项目支持 `dev`、`test`、`prod` 三种环境配置，通过 Maven Profile 切换：

**开发环境（默认）：**

```bash
mvn clean install -Pdev
```

**测试环境：**

```bash
mvn clean install -Ptest
```

**生产环境：**

```bash
mvn clean install -Pprod
```

### 修改服务端口

在 `application.yml` 中修改：

```yaml
server:
  port: 8080  # 修改为您需要的端口号
```

### 日志配置

日志配置文件：`okayjam-web-core/src/main/resources/logback-spring.xml`

- 支持控制台和文件输出
- 支持 JSON 格式日志（使用 Logstash Encoder）
- 支持 TraceId 全链路追踪

## 功能特性

### 1. MyBatis 集成

- **ORM 框架**：使用 MyBatis 进行数据库操作
- **分页支持**：集成 PageHelper 分页插件
- **代码生成**：支持 MyBatis Generator 自动生成代码

**分页查询示例：**

```java
@GetMapping("/list")
public Page<TbTest> list(@RequestParam(defaultValue = "1") int pageNum,
                         @RequestParam(defaultValue = "10") int pageSize) {
    PageHelper.startPage(pageNum, pageSize);
    return testService.selectAll();
}
```

### 2. 分布式锁

基于数据库实现的分布式锁，支持多实例部署场景。

**使用示例：**

```java
@Autowired
private DistributedLock distributedLock;

public void doSomething() {
    String lockKey = "business:lock:key";
    if (distributedLock.tryLock(lockKey, 30, TimeUnit.SECONDS)) {
        try {
            // 执行业务逻辑
        } finally {
            distributedLock.unlock(lockKey);
        }
    }
}
```

### 3. API 文档（SpringDoc OpenAPI）

- **自动生成**：基于注解自动生成 API 文档
- **在线测试**：通过 Swagger UI 在线测试接口
- **访问地址**：http://localhost:8080/swagger-ui.html

**Controller 注解示例：**

```java
@RestController
@RequestMapping("/api/test")
@Tag(name = "测试接口", description = "测试相关的 API")
public class TestController {
    
    @Operation(summary = "查询列表", description = "分页查询测试数据")
    @GetMapping("/list")
    public Result<Page<TbTest>> list(@Parameter(description = "页码") @RequestParam int pageNum) {
        // ...
    }
}
```

### 4. 日志追踪（TraceId）

- **全链路追踪**：每个请求自动生成唯一 TraceId
- **日志关联**：通过 TraceId 关联同一请求的所有日志
- **JSON 格式**：支持 JSON 格式日志输出，便于日志收集和分析

**日志输出示例：**

```json
{
  "timestamp": "2026-01-08T10:00:00.000+08:00",
  "level": "INFO",
  "traceId": "a1b2c3d4e5f6",
  "thread": "http-nio-8080-exec-1",
  "logger": "com.okayjam.web.controller.TestController",
  "message": "查询测试数据",
  "context": "default"
}
```

### 5. 虚拟线程支持（Virtual Threads）

Spring Boot 4.0.1 支持 Java 21 的虚拟线程特性，可显著提升高并发场景下的性能表现。

**启用配置：**

```yaml
spring:
  threads:
    virtual:
      enabled: true  # 开启虚拟线程
```

#### 性能测试对比

使用 ApacheBench (ab) 工具对 `/api/test/slow` 接口（模拟 500ms 延迟）进行压测，对比平台线程与虚拟线程的性能差异：

**测试数据汇总表：**

| 并发数 | 线程模式 | 总请求 | 完成请求 | 失败率 | 总耗时 | 吞吐量(req/s) | 平均响应(ms) | 中位数(ms) | 状态 |
|-------|---------|-------|---------|-------|--------|--------------|-------------|-----------|------|
| **1000** | 平台线程 | 10000 | 10000 | - | 26.42s | 378 | 2642 | 2555 | ⚠️ 慢 |
| **1000** | 虚拟线程 | 10000 | 10000 | 0% | 6.63s | **1507** | 663 | 531 | ✅ |
| **2000** | 平台线程 | 16000 | 16000 | **55%** | 41.95s | 381 | 5244 | 5118 | ⚠️ 大量失败 |
| **2000** | 虚拟线程 | 10000 | 10000 | ~4% | 4.98s | **2010** | 995 | 709 | ✅ |
| **3000** | 平台线程 | 20000 | **201** | **99%** | - | - | - | - | ❌ 崩溃 |
| **3000** | 虚拟线程 | 20000 | 20000 | 0% | 5.61s | **3568** | 841 | 602 | ✅ |

**性能提升倍数：**

| 并发数 | 吞吐量提升 | 响应时间降低 | 失败率改善 |
|-------|-----------|-------------|-----------|
| **1000** | **4.0x** | **75%** | - |
| **2000** | **5.3x** | **81%** | 55% → ~0% |
| **3000** | **∞** | **∞** | 崩溃 → 稳定 |

**关键发现：**

| 发现 | 说明 |
|------|------|
| **吞吐量天花板** | 平台线程在 ~380 req/s 封顶，虚拟线程随并发线性增长 |
| **响应时间** | 虚拟线程始终保持在 1 秒内，平台线程随并发急剧恶化 |
| **并发极限** | 平台线程 2000 并发开始大量失败，3000 直接崩溃 |
| **资源利用** | 虚拟线程能充分利用 CPU，平台线程受线程池限制 |

**测试结论：**

```
┌────────────────────────────────────────────────────────────────┐
│                     性能提升总览                                │
├────────────────────────────────────────────────────────────────┤
│  指标              平台线程          虚拟线程         提升      │
├────────────────────────────────────────────────────────────────┤
│  最大吞吐量        ~380 req/s       3568 req/s      9.4x       │
│  最大稳定并发      < 2000           3000+           50%+       │
│  2000并发响应      5244ms           995ms           ↓81%       │
│  高并发失败率      55%~崩溃         0%              100%改善   │
└────────────────────────────────────────────────────────────────┘
```

> 💡 **生产环境强烈推荐开启虚拟线程**，特别是在 I/O 密集型应用场景（如数据库查询、HTTP 调用、文件操作等）。

### 6. 热部署（Spring DevTools）

- **自动重启**：代码修改后自动重启应用
- **LiveReload**：支持浏览器自动刷新
- **配置优化**：已优化重启速度和资源占用

**IDE 配置（IntelliJ IDEA）：**

1. 打开 `Settings` → `Build, Execution, Deployment` → `Compiler`
2. 勾选 `Build project automatically`
3. 按 `Ctrl+Shift+A`，搜索 `Registry`
4. 勾选 `compiler.automake.allow.when.app.running`

### 7. CORS 跨域配置

项目已配置 CORS 跨域支持，可在 `WebMvcConfig` 中自定义配置。

### 8. 统一异常处理

全局异常处理器，统一返回格式，便于前端处理。

## 开发指南

### MyBatis Generator 代码生成

1. 配置 `generatorConfig.xml`（如果存在）
2. 运行 Maven 命令：

```bash
mvn mybatis-generator:generate
```

### RESTful API 开发规范

- **GET**：查询资源
- **POST**：创建资源
- **PUT**：更新资源（全量）
- **PATCH**：更新资源（部分）
- **DELETE**：删除资源

**统一响应格式：**

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 单元测试

运行单元测试：

```bash
mvn test
```

### 打包部署

#### 传统部署方式

**1. 打包命令：**

```bash
mvn clean package -Pprod
```

**2. 部署说明：**

将 `okayjam-web-core/target/okayjam-web-core-1.0.0-SNAPSHOT.jar` 和 `service.sh` 上传到服务器

**3. 使用 service.sh 启动（推荐）：**

```bash
# 赋予执行权限
chmod +x service.sh

# 启动服务
bash service.sh start

# 查看日志
tail -f nohup.out

# 停止服务
bash service.sh stop

# 重启服务
bash service.sh restart
```

**4. 直接运行（不推荐）：**

```bash
java -jar okayjam-web-core-1.0.0-SNAPSHOT.jar
```

#### Docker 容器化部署

项目提供了完整的 Docker 支持，可快速构建和部署容器化应用。

**1. 构建 Docker 镜像**

```bash
# 构建镜像（使用默认标签）
docker build -t jamspring:latest .

# 构建镜像（指定版本标签）
docker build -t jamspring:1.0.0 .

# 构建镜像（指定多个标签）
docker build -t jamspring:1.0.0 -t jamspring:latest .

# 删除旧容器（可选）
docker rm -f jamspring

```

**2. 运行 Docker 容器**

```bash
# 基础运行
docker run -d -p 8080:8080 --name jamspring jamspring:latest

# 挂载配置文件（推荐）
docker run -d \
  -p 8080:8080 \
  --name jamspring \
  -v /path/to/config:/app/service/config \
  jamspring:latest

# 挂载日志目录
docker run -d \
  -p 8080:8080 \
  --name jamspring \
  -v /path/to/config:/app/service/config \
  -v /path/to/logs:/app/service/logs \
  jamspring:latest

# 设置环境变量
docker run -d \
  -p 8080:8080 \
  --name jamspring \
  -e JAVA_OPTS="-Xmx512m -Xms256m" \
  jamspring:latest
```

**3. 容器管理命令**

```bash
# 查看容器日志
docker logs -f jamspring

# 查看容器状态
docker ps | grep jamspring

# 停止容器
docker stop jamspring

# 启动容器
docker start jamspring

# 重启容器
docker restart jamspring

# 删除容器
docker rm -f jamspring

# 进入容器
docker exec -it jamspring /bin/sh
```

**4. Docker Compose 部署（推荐）**

创建 `docker-compose.yml` 文件：

```yaml
version: '3.8'

services:
  jamspring:
    image: jamspring:latest
    container_name: jamspring
    ports:
      - "8080:8080"
    volumes:
      - ./config:/app/service/config
      - ./logs:/app/service/logs
    environment:
      - JAVA_OPTS=-Xmx512m -Xms256m
    restart: unless-stopped
    networks:
      - jamspring-network

networks:
  jamspring-network:
    driver: bridge
```

使用 Docker Compose 管理：

```bash
# 启动服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down

# 重启服务
docker-compose restart
```


## API 文档

### Swagger UI

- **访问地址**：http://localhost:8080/swagger-ui.html
- **功能**：
  - 查看所有 API 接口
  - 查看接口参数和返回值
  - 在线测试接口

### OpenAPI JSON

- **访问地址**：http://localhost:8080/v3/api-docs
- **用途**：可导入到 Postman、Apifox 等工具

## Docker 部署

### 快速开始

**1. 构建镜像**

```bash
docker build -t jamspring:latest .
```

**2. 运行容器**

```bash
docker run -d -p 8080:8080 --name jamspring jamspring:latest
```

**3. 访问应用**

- 应用地址：http://localhost:8080
- Swagger UI：http://localhost:8080/swagger-ui.html

### 生产环境部署建议

```bash
docker run -d \
  --name jamspring \
  -p 8080:8080 \
  -v /data/jamspring/config:/app/service/config \
  -v /data/jamspring/logs:/app/service/logs \
  -e JAVA_OPTS="-Xmx1g -Xms512m" \
  --restart=unless-stopped \
  jamspring:latest
```

### Docker Compose 部署

参考上面的 [Docker Compose 部署](#docker-容器化部署) 章节。

## 常见问题

### 1. 端口被占用

**问题**：启动时提示 `Port 8080 was already in use`

**解决方案**：

- 方案一：修改 `application.yml` 中的 `server.port`
- 方案二：关闭占用 8080 端口的进程

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <进程ID> /F

# Linux/Mac
lsof -i :8080
kill -9 <进程ID>
```

### 2. 数据库连接失败

**问题**：启动时提示数据库连接失败

**检查清单**：

- ✅ 数据库服务是否启动
- ✅ 数据库地址、端口是否正确
- ✅ 用户名、密码是否正确
- ✅ 数据库是否存在
- ✅ 防火墙是否允许连接

### 3. Maven 依赖下载失败

**问题**：构建时依赖下载缓慢或失败

**解决方案**：配置 Maven 镜像（阿里云）

编辑 `~/.m2/settings.xml`：

```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Aliyun Maven</name>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

### 4. 热部署不生效

**问题**：修改代码后应用未自动重启

**解决方案**：

- IntelliJ IDEA：确保开启 `Build project automatically`
- Eclipse：确保开启 `Project` → `Build Automatically`
- 检查 `spring-boot-devtools` 依赖是否存在

### 5. Swagger UI 无法访问

**问题**：访问 Swagger UI 返回 404

**解决方案**：

- 检查 `springdoc.swagger-ui.enabled` 是否为 `true`
- 确认访问地址：http://localhost:8080/swagger-ui.html
- 检查是否有安全配置拦截了该路径

## 参考文档

### 官方文档

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Spring Framework 文档](https://spring.io/projects/spring-framework)
- [MyBatis 官方文档](https://mybatis.org/mybatis-3/)
- [MyBatis Spring Boot Starter](https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)
- [PageHelper 文档](https://github.com/pagehelper/Mybatis-PageHelper)
- [SpringDoc OpenAPI 文档](https://springdoc.org/)
- [Logback 文档](https://logback.qos.ch/documentation.html)
- [HikariCP 文档](https://github.com/brettwooldridge/HikariCP)

### 相关资源

- [Spring Boot 最佳实践](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.best-practices)
- [RESTful API 设计指南](https://restfulapi.net/)
- [Java 21 新特性](https://www.oracle.com/java/technologies/javase/21-relnote-issues.html)

## 许可证

本项目采用 [Apache License 2.0](https://opensource.org/licenses/Apache-2.0) 开源许可证。

## 版本信息

- **当前版本**：1.0.0-SNAPSHOT
- **Spring Boot 版本**：4.0.1
- **Java 版本**：21

## 贡献指南

欢迎提交 Issue 和 Pull Request！

如有问题或建议，请联系项目维护者。

---

**Happy Coding! 🚀**
