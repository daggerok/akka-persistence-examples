# akka-persistence-examples [![Build Status](https://travis-ci.org/daggerok/akka-persistence-examples.svg?branch=master)](https://travis-ci.org/daggerok/akka-persistence-examples)

## mem stateless

```bash
./sbtw clean h2Mem "runMain com.github.daggerok.akka.Main"
```

_or_

```bash
cp -Rf ./src/main/resources/application-mem.conf ./src/main/resources/application.conf
./sbtw clean "runMain com.github.daggerok.akka.Main"
```

## file stateless

```bash
./sbtw clean h2FileInit "runMain com.github.daggerok.akka.Main"
```

_or_

```bash
cp -Rf ./src/main/resources/application-file-init.conf ./src/main/resources/application.conf
./sbtw clean "runMain com.github.daggerok.akka.Main"
```

## file stateful

```bash
./sbtw clean h2FileInit "runMain com.github.daggerok.akka.Main"
./sbtw h2FileNext "runMain com.github.daggerok.akka.Main"
./sbtw "runMain com.github.daggerok.akka.Main"
./sbtw h2Mem
```

_or_

```bash
cp -Rf ./src/main/resources/application-file-init.conf ./src/main/resources/application.conf
./sbtw clean "runMain com.github.daggerok.akka.Main"

cp -Rf ./src/main/resources/application-file-next.conf ./src/main/resources/application.conf
./sbtw "runMain com.github.daggerok.akka.Main"
./sbtw "runMain com.github.daggerok.akka.Main"
# ...
```

## resources

* https://github.com/akka/akka-persistence-jdbc/issues/116#issuecomment-324546231
* https://github.com/lightbend/config#debugging-your-configuration
