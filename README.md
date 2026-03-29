# ctoip

`ctoip` 是一个基于 Spring Boot 的后端服务，聚焦安全查询与网络工具能力。项目包含认证与验证码登录、IP 情报查询、域名信息查询，以及基于 `nmap` 的简单网络工具模块，依赖 MySQL、Redis 和外部威胁情报 API。

## 技术栈

- Java 8
- Spring Boot 2.6.3
- Spring Security
- MyBatis-Plus
- Redis
- MySQL
- Maven Wrapper
- Docker

## 功能特性

- 基于验证码的 Spring Security 登录流程
- 基于 JWT 的请求鉴权，请求头使用 `Authorization`
- 通过 ThreatBook 进行 IP 信誉与归属地查询
- 通过 VirusTotal 进行域名信息查询
- 使用 Redis 存储查询历史与缓存类数据
- 提供 `nmap` 执行接口，用于基础网络探测

## 项目结构

- `src/main/java/com/aurora/ctoip/CtoipApplication.java`：应用启动入口
- `src/main/java/com/aurora/ctoip/config`：Spring、Redis、MyBatis、CORS、验证码与安全配置
- `src/main/java/com/aurora/ctoip/controller`：认证、用户、IP 追踪、域名查询、网络工具等接口
- `src/main/java/com/aurora/ctoip/security`：自定义认证、CSRF、异常处理等安全逻辑
- `src/main/java/com/aurora/ctoip/service`：业务服务层
- `src/main/java/com/aurora/ctoip/mapper`：MyBatis-Plus Mapper 接口
- `src/main/resources/application.yml`：本地/默认运行配置
- `src/main/resources/application-prod.yml`：容器运行时生产配置
- `Dockerfile`：多阶段构建镜像并内置 `nmap`

## 主要接口

- `GET /captcha`：生成验证码图片和 token
- `POST /login`：基于 Spring Security 表单登录
- `POST /logout`：退出登录
- `GET /IpTrace/getIpInfo`：查询 IP 情报
- `GET /IpTrace/getIpInfoList`：获取已存储的 IP 查询结果
- `GET /DomainQuery/DomainInfo`：查询域名情报
- `GET /DomainQuery/getDomainInfoList`：获取已存储的域名查询结果
- `GET /netTools/nmap`：执行 `nmap` 命令参数查询

## 配置说明

应用默认监听端口为 `8081`。

关键配置文件：

- `src/main/resources/application.yml`：用于本地开发
- `src/main/resources/application-prod.yml`：Docker 镜像运行时使用

生产环境支持通过以下环境变量覆盖：

- `MYSQLIP`
- `MYSQL_ROOT_PASSWORD`
- `REDISIP`
- `REDIS_PASSWORD`
- `JWT_SECRET`
- `THREATBOOK_API_KEY`
- `VT_API_KEY`

## 本地运行

启动应用：

```bash
./mvnw spring-boot:run
```

构建 jar 包：

```bash
./mvnw clean package -DskipTests
```

运行测试：

```bash
./mvnw test
```

## Docker 使用

Docker 镜像采用多阶段构建：

1. 在 JDK 8 环境中使用 Maven Wrapper 编译项目
2. 将产出的 jar 复制到 JRE 8 运行镜像
3. 安装 `nmap`
4. 使用 `application-prod.yml` 启动应用

构建镜像：

```bash
docker build -t ctoip .
```

运行容器：

```bash
docker run --rm -p 8081:8081 \
  -e MYSQLIP=db \
  -e REDISIP=redis \
  -e MYSQL_ROOT_PASSWORD=root \
  -e REDIS_PASSWORD=root \
  -e JWT_SECRET=change-me \
  -e THREATBOOK_API_KEY=your-threatbook-key \
  -e VT_API_KEY=your-virustotal-key \
  ctoip
```

## 说明

- 前端通常通过 `/api` 代理访问本后端。
- 登录流程依赖 Redis 存储的验证码状态，认证前需确保 Redis 可用。
- `nmap` 接口依赖运行环境中存在 `nmap` 二进制，因此 Docker 镜像内已预装。
