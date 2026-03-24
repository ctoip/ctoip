# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project summary

- Service: `ctoip` backend
- Stack: Spring Boot 2.6.3 + MyBatis-Plus + Spring Security + Redis
- Java: 1.8 (`pom.xml`)
- Default app port: `8081`

## Commands

### Development

```bash
./mvnw spring-boot:run
```

### Build

```bash
./mvnw clean package -DskipTests
```

### Test

Run all tests:

```bash
./mvnw test
```

Run a single test class:

```bash
./mvnw -Dtest=ClassName test
```

Run a single test method:

```bash
./mvnw -Dtest=ClassName#methodName test
```

### Lint

No dedicated lint command is defined in `pom.xml`.

## Architecture (big picture)

- App entry: `src/main/java/com/aurora/ctoip/CtoipApplication.java`
- Security chain: `config/SecurityConfig.java` plus filters
- Data access: MyBatis-Plus mapper model (`mapper` + XML mappers)
- Core domain features are exposed through controllers (auth, user, IP trace, domain query, network tools)

## Configuration model

There are two important runtime config files with different purposes:

- `src/main/resources/application.yml`: local development defaults
- `application.yml` (repo root): image runtime config copied by Dockerfile

Docker image starts with:

```bash
java -jar /usr/local/ctoip.jar --spring.config.location=/usr/local/application.yml
```

So container behavior is controlled by root `application.yml` + environment variable overrides.

## Integration contracts

- Frontend (`ctoip_vue`) calls backend through `/api` proxy.
- Login flow depends on Redis-backed captcha (`/captcha` then `/login`).
- JWT header key is `Authorization`.
- Deployment target is `ctoip_docker` `spring` service.
