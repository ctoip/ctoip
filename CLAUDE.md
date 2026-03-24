# CLAUDE.md

## 项目定位
- 子项目：`ctoip`
- 类型：Spring Boot 2.6.3 + MyBatis-Plus + Spring Security + Redis 的后端服务
- 运行端口：`8081`
- JDK 版本：`1.8`（见 `pom.xml`）

## 关键入口
- 启动类：`src/main/java/com/aurora/ctoip/CtoipApplication.java`
- 安全配置：`src/main/java/com/aurora/ctoip/config/SecurityConfig.java`
- 配置文件：`src/main/resources/application.yml`

## 核心业务模块
- 登录与验证码：`AuthController` + `CaptchaFilter`
- 用户信息：`SysUserController`
- IP 溯源：`IpTraceController`
- 域名查询：`DomainQueryController`
- 网络工具箱（nmap）：`NetToolsController`

## 与其它子项目关系
- 前端 `ctoip_vue` 通过 `/api` 代理到本服务
- Docker 部署由 `ctoip_docker` 的 `spring` 服务承载本项目 jar

## 本地开发命令
```bash
./mvnw spring-boot:run
```

## 打包命令
```bash
./mvnw clean package -DskipTests
```

## 部署配置说明
- Actions 镜像构建会使用仓库根目录 `application.yml` 作为运行配置模板
- 生产环境通过容器环境变量覆盖配置（DB/Redis/JWT/API key）
- 容器以内：
  `java -jar /usr/local/ctoip.jar --spring.config.location=/usr/local/application.yml` 启动

## 当前已确认约定
- JWT Header 为 `Authorization`
- 默认依赖 MySQL(3306) 与 Redis(6379)
- 登录流程依赖验证码 token + code
