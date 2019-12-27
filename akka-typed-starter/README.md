# akka-persistence-examples [![Build Status](https://travis-ci.org/daggerok/akka-persistence-examples.svg?branch=master)](https://travis-ci.org/daggerok/akka-persistence-examples)

## sbt-docker-compose

```bash
./sbtw dockerComposeUp
./sbtw dockerComposeRestart
./sbtw dockerComposeStop
```

## app

```bash
./sbtw clean package
./sbtw "runMain com.github.daggerok.akka.Main"
./sbtw "runMain com.github.daggerok.akka.AskPattern"
./sbtw "runMain com.github.daggerok.akka.AkkaActorTyped"
```

## all

```bash
./sbtw dockerComposeRestart "runMain com.github.daggerok.akka.Main" dockerComposeStop
```
