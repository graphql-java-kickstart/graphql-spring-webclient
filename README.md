# GraphQL Spring Webclient
[![Build Status](https://travis-ci.org/graphql-java-kickstart/graphql-spring-webclient.svg?branch=master)](https://travis-ci.org/graphql-java-kickstart/graphql-spring-webclient)
[![Maven Central](https://img.shields.io/maven-central/v/com.graphql-java-kickstart/graphql-webclient-spring-boot-starter.svg)](https://maven-badges.herokuapp.com/maven-central/com.graphql-java-kickstart/graphql-webclient-spring-boot-starter)

Reactive GraphQL client for consuming GraphQL APIs from a Spring Boot application.
Provides OAuth2 authorization through configuration.

## Getting started

Add the starter to your project.

When using Maven:
```xml
<dependency>
  <groupId>com.graphql-java-kickstart</groupId>
  <artifactId>graphql-webclient-spring-boot-starter</artifactId>
  <version>0.2.0</version>
</dependency>
```

When using gradle:
```groovy
implementation "com.graphql-java-kickstart:graphql-webclient-spring-boot-starter:0.2.0"
```

Configure at least the URL of the GraphQL API to consume:
```yaml
graphql:
  client:
    url: https://graphql.github.com/graphql
```

### Using latest Snapshots

You can use the latest Snapshots by configuring the Snapshot repository, see https://www.graphql-java-kickstart.com/servlet/#using-the-latest-development-build.


## Configuration

The following tables list the configurable properties of the GraphQL Spring Webclient and their default values.
These properties are configured with the prefix `graphql.client`, e.g. the property listed in the table as `url` 
should be defined as `graphql.client.url` in your Spring Boot configuration files.

| Property | Description |
|----------|-------------|
| `url` | Full URL of the GraphQL API to connect to, e.g. https://graphql.github.com/graphql |
| `oauth2.client-id` | OAuth2 client id |
| `oauth2.client-secret` | OAuth2 client secret |
| `oauth2.token-uri` | Token URI of the identity provider |
| `oauth2.authorization-grant-type` | By default the grant type `client_credentials` is used |
 
### Max in memory size

In case you need to work with large responses you might run into the following error:
```
Exceeded limit on max bytes to buffer : 262144
```
In that case starting with version 0.1.2 you can use the default Spring Boot configuration property to configure
the max in memory size to use:
```properties
spring.codec.max-in-memory-size=10MB
``` 
