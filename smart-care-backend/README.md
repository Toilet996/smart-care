# SmartCare Backend

智慧养老大数据分析决策系统的后端服务，基于 Spring Boot + RuoYi 3.9.1 二次开发。

> 项目完整介绍、功能清单、架构图和演示账号见根目录 [README](../README.md)。

## 技术栈

- Java 8
- Spring Boot 2.5.15
- Spring Security + JWT
- MyBatis + PageHelper
- MySQL 8.x
- Redis（Windows 可用 Memurai）
- Quartz
- Druid
- Swagger

## 模块说明

| 模块 | 说明 |
| --- | --- |
| `ruoyi-admin` | 启动模块、SmartCare Controller、系统管理接口 |
| `ruoyi-system` | SmartCare Service / Mapper / Domain、业务 Mapper XML |
| `ruoyi-framework` | 安全、数据源、Web、拦截器等基础配置 |
| `ruoyi-quartz` | 定时任务，包含 `AlertAnalysisTask` 预警分析 |
| `ruoyi-generator` | 若依代码生成器 |
| `ruoyi-common` | 公共工具与基础类 |
| `docs` | 技术功能文档、演示账号、PPT 提纲 |
| `sql` | 数据库脚本与交付 SQL |

## 本地启动

### 1. 初始化数据库

```bash
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS \`smart-care\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
mysql -uroot -p smart-care < sql/smart-care.sql
```

### 2. 配置环境变量

```powershell
$env:MYSQL_HOST="localhost"
$env:MYSQL_PORT="3306"
$env:MYSQL_USERNAME="root"
$env:MYSQL_PASSWORD="your-mysql-password"
$env:TOKEN_SECRET="change-me-to-a-long-random-string"
$env:QWEN_API_KEY="sk-your-qwen-key"
$env:QIANFAN_API_KEY="your-qianfan-key"
```

不启用 AI 时设置：

```powershell
$env:SMARTCARE_AI_ENABLED="false"
```

### 3. 启动

```bash
mvn clean package -DskipTests
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

默认端口：`8888`  
Swagger UI：`http://localhost:8888/swagger-ui/index.html`

## 关键代码位置

- 养老业务 Controller：`ruoyi-admin/src/main/java/com/ruoyi/smartcare/controller`
- 养老业务 Service：`ruoyi-system/src/main/java/com/ruoyi/smartcare/service`
- 养老业务 Mapper XML：`ruoyi-system/src/main/resources/mapper/care`
- IoT Mapper XML：`ruoyi-system/src/main/resources/mapper/iot`
- 预警分析定时任务：`ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/AlertAnalysisTask.java`
- AI 健康评估：`ruoyi-system/src/main/java/com/ruoyi/smartcare/service/impl/AiCareServiceImpl.java`

## 文档

- [技术功能文档](docs/技术功能文档.md)
- [演示账号说明](docs/演示账号说明文档.md)
- [PPT 提纲](docs/PPT%20提纲文档.md)

## 安全说明

- 不要提交真实 API Key、数据库密码、JWT 密钥。
- 演示账号仅用于本地环境，公网部署前请修改密码。
- 详细安全策略见根目录 [SECURITY.md](../SECURITY.md)。
