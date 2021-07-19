# Ktor Sample

## TL;DR

A simple sample of Rest API by [Ktor](https://ktor.io).

This is another implementation version of [Spring Boot Sample](https://github.com/retheviper/springbootsample).

In Construction!

## Includes

- [Gradle](https://gradle.org) (Kotlin DSL)
- [Exposed](https://github.com/JetBrains/Exposed)
- [Kotlin serialization](https://github.com/Kotlin/kotlinx.serialization)

## Architecture

- `common`: common utils, constants
- `domain`: dtos
- `infrastructure`: repositories, tables
- `plugins`: plugins and settings for db, routing, security, serialization
- `route`: router/handler functions, data models (request/response)

## Test APIs

1. Run server.

```shell
./gradlew run
```

2. Import [Postman](https://www.postman.com) data from below.

```shell
/miscs/SpringBootSample.postman_collection.json
```

3. Test with Postman.

Note: Some APIs need JWT on HTTP Request Header(`X-AUTH-TOKEN`) and it will be found at HTTP Response Header when successfully logged in.
