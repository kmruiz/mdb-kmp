# mdb-kmp example

An application that runs on Java Swing (Desktop) and WASM that connects to a MongoDB
cluster and lists in real time created databases in the cluster.

## commonMain

All the UI that is shared across applications.

## desktopMain

Implementation, backed by the Java Driver, of the MongoDB connection.

### How to start

Just run the following command to start the Swing application.

```sh
./gradlew :composeApp:run
```

## wasmJsMAin

Implementation, backed by an Express.js HTTP proxy, of the MongoDB connection.

### How to start:

1. Start the server in composeApp/src/httpMongoDbProxy

```sh
cd composeApp/src/httpMongoDbProxy
npm i
node index.js
```

2. In another terminal, run the following command:

```shell
./gradlew :composeApp:wasmJsBrowserRun
```
