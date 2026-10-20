# SmartCare Frontend

智慧养老大数据分析决策系统的前端，基于 Vue 3 + Vite + Element Plus。

> 项目完整介绍、功能清单、架构图和演示账号见根目录 [README](../README.md)。

## 技术栈

- Vue 3.5
- Vite 6
- Element Plus 2.13
- Pinia
- Vue Router
- Axios
- ECharts 5.6

## 本地启动

```bash
npm install --registry=https://registry.npmmirror.com
npm run dev
```

默认地址：`http://localhost:8080`

开发环境通过 Vite 将 `/dev-api` 代理到 `http://localhost:8888`。  
如后端端口不同，修改 `vite.config.js` 中的 `baseUrl`。

## 构建

```bash
npm run build:prod
```

构建产物在 `dist/`。

## 页面结构

| 目录 | 说明 |
| --- | --- |
| `src/views/care/overview` | 社区总览 |
| `src/views/care/overviewScreen` | 社区大屏 |
| `src/views/care/elderly` | 老人档案 |
| `src/views/care/family` | 家属关联 |
| `src/views/care/alertRule` | 预警规则 |
| `src/views/care/alertRecord` | 异常预警 |
| `src/views/care/workOrder` | 处置工单 |
| `src/views/care/visitRecord` | 随访记录 |
| `src/views/care/healthReport` | 健康报告 |
| `src/views/care/medicationPlan` | 服药计划 |
| `src/views/care/medicationRecord` | 服药执行 |
| `src/views/care/familyPortal` | 家属门户 |
| `src/views/care/aiCare` | AI 健康评估 |
| `src/views/iot/device` | 设备台账 |
| `src/views/iot/sensorLog` | 传感器流水 |

API 目录：`src/api/care`、`src/api/iot`。

## 说明

- 前端只负责页面与接口调用，业务逻辑在后端 `smart-care-backend`。
- 演示账号与后端配置见根目录 README。
- 不要在前端代码中写入 API Key、数据库密码等敏感信息。
