#!/bin/bash

# 普通 JVM 模式打包（推荐，适用于大多数场景）
mvn clean package -DskipTests

# ========== 以下是 GraalVM Native Image 构建（可选） ==========
# 注意：需要安装 GraalVM 并设置 JAVA_HOME 或 GRAALVM_HOME
## 测试可以使用sdk man安装  sdk install java 25.0.1-graalce && sdk use java 25.0.1-graalce
# # 1. AOT 预处理
# mvn spring-boot:process-aot -DskipTests
# # 2. 构建原生镜像,-pl 只编译指定的模块, -am：同时构建依赖的模块
# mvn -Pnative native:compile  -DskipTests  -pl okayjam-web-core -am