# postpone-sprint-annotation

Postpone and store method executions in repository with single spring annotation

```
./gradlew bootRun
```

To schedule method invocation:

```
curl localhost:8080/hello/world
```

To actually invoke the "hello" method:

```
curl localhost:8080/run
```


## Requirements

You need a running and accessible mongodb instance on localhost to run the example