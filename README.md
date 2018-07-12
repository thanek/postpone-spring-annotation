# postpone-spring-annotation

Postpone and store method executions in repository with single spring annotation.

## How to use

Just simply annotate your service's method with `@Postponable` and it will be marked to postponed execution. This means
that the method will not be executed, despite its invocation. Instead, the repository (pointed as annotation parameter)
will store the invocation info. The `PostponedMethodInvoker` service gets postponed method invocations from repository and
invokes them asynchronously. Use the `@EnablePostpones` in your @Configuration beans to enable proxying methods marked as
`@Postponable`.

## Running the example

```
./gradlew bootRun
```

To schedule method invocation:

```
curl localhost:8080/hello/world
```

The `HelloController` tries to execute the `HelloService::hello` method which is marked as `@Postponable` so it won't be
invoked immediately, instead the `MongoInvocationRepository` will store the method execution request in the MongoDB
database.

To actually invoke the `hello` method:

```
curl localhost:8080/run
```

This will run the `PostponedMethodInvoker`'s invokeQueued method, so the stored method execution requests will actually be
invoked. After the method finish, the entry in MongoDB is deleted (see `MongoInvocationRepository` implementation for
details).

## Requirements

You need a running and accessible mongodb instance on localhost to run the example.

## Running tests

```
./gradlew test
```
