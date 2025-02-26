# Kotlin Multiplatform

```
~~~graph-easy --as=boxart
[ Kotlin ] - for -> [ Desktop ], [ Mobile ], [ Web ]
~~~
```

---

# How does it work?

## Essentially, it's a compiler with multiple backends

```
~~~graph-easy --as=boxart
[ Kotlin ] - to -> [ IR ] - to -> [ Your backend ]
~~~
```

## This IR approach is common in any compiler that targets multiple platforms

* clang
* gcc
* rustc

### Contain their own IR where the apply optimisations and transform your code

---

# How does it work?

## It links dependencies that are related to the target platform

```
~~~graph-easy --as=boxart
[ Kotlin ] - is transformed to -> [ IR ]
[ IR ] -> [ Kotlin for WASM ] -> [ Browser ]
[ IR ] -> [ Kotlin for Java ] -> [ Desktop ]
[ IR ] -> [ Kotlin Native ] -> [ Windows / Linux / OSX ]
~~~
```

## It does this job during compile-time
But you can also depend on runtime libraries.

---

# Kotlin/Common

It's a subset of Kotlin that only allows to use:

* The kotlin standard library
* Kotlinx libraries
* Other multiplatform libraries

## Kotlin/Java

* Allows using Java libraries
* Deploys as a JAR file
* Can be used for both the desktop and server

### Kotlin/WASM

* Allows using JavaScript libraries
* Allows using the browser API
* Bundles with **webpack** and exposes a JS friendly API.
* ⚠️ In Alpha.

> For a more stable version, you should use Kotlin/JS.

---

# How do we dynamically link this stuff?

Two words: **expect** and **actual**.

## Common
```kt
expect fun randomId(): String
```

## Kotlin/Java

```kt
actual fun randomId() = UUID.randomUUID().toString()
```

## Kotlin/WASM
```kt
actual fun randomId() = "" + (+Date())
```

---

# How do we dynamically link this stuff?

```
~~~graph-easy --as=boxart
[ commonMain ]
[ commonMain ] -> [ desktopMain ]
[ commonMain ] -> [ wasmJsMain ]
~~~
```

* commonMain contains the `expect`
* desktopMain contains the `actual` _java_ version
* wasmJsMain contains the `actual` _wasm_ version

## You can use any platform library in the implementations!

---

# Kotlin Compose

A toolkit to build cross-platform UI applications.

## Android

Based on Jetpack Compose

## Swift

Based on SwiftUI

## Java

OpenGL Based rendering with integration with Swing.

## Browser/WASM

WebGL based rendering.

## Native

Nothing official, but there are libraries for TUIs like mosaic.

---

# Kotlin Compose

Declarative UI based on a @Composable DSL. Familiar for developers
that work with React / Swift UI.

```kt
@Composable // a Component
fun DatabaseSizeTag(size: Long) { // Arguments
    val tagColor = if (size > 1024 * 1024 * 1024) {
        Color.Red
    } else if (size > 1024L * 1024 * 50) {
        Color.Yellow
    } else {
        Color.Green
    }

    var normSize = size
    var normUnit = "B"

    if (normSize > 1024) {
        normSize /= 1024
        normUnit = "KB"
    }

    if (normSize > 1024) {
        normSize /= 1024
        normUnit = "MB"
    }

    if (normSize > 1024) {
        normSize /= 1024
        normUnit = "GB"
    }

    Text("${normSize}${normUnit}", modifier = Modifier // Virtual DOM
        .background(tagColor)
        .border(width = 1.dp, color = tagColor, shape = RoundedCornerShape(32.dp))
        .padding(horizontal = 32.dp)
    )
}
```

---
# Kotlin Compose

Declarative UI based on a @Composable DSL. Familiar for developers
that work with React / Swift UI.

```kt
@Composable
fun App() {
    val connectionState by connectionStatus().collectAsState() // reactive state
    val showContent by derivedStateOf { connectionState == ConnectionState.Connected }

    MaterialTheme { // how it's going to be rendered
        Column(Modifier.fillMaxWidth()) {
            DatabaseConnectionHandler() // custom components

            AnimatedVisibility(showContent) { // animations
                Column(Modifier.fillMaxWidth()) {
                    DatabaseList()
                }
            }
        }
    }
}
```

---

# Demo

---

# Demo

## Desktop

```shell
./gradlew :composeApp:run
```

---

# Demo

## Desktop

```shell
./gradlew :composeApp:run
```

## Web

Start the proxy server:

```shell
cd composeApp/src/httpMongoDbProxy
npm i
node index.js
```

And then the application (it will open a browser):

```shell
./gradlew :composeApp:wasmJsBrowserRun
```

---

# Distributing

## Desktop

* Support for Msi/Deb/Dmg
* Minifies and obfuscates using Proguard

```shell
./gradlew composeApp:createReleaseDistributable
```

> 📦 123MB.

## WASM

* Minifies using webpack
* You can decide what ES version to target
* Requires WebGL to work

```shell
./gradlew composeApp:wasmJsBrowserProductionWebpack
```

> 📦 12MB w/o compression, 3.5MB gzipped.

---

# Wait, did you just say WebGL?

Compose WASM uses WebGL because it's faster than working with the DOM
for applications with enough complexity.

```
~~~python3
import plotext as plt

benchmarks = ["AnimatedVisibility", "LazyGrid", "VisualEffects"]
jvm = [100, 100, 100]
wasm = [150, 180, 155]
js = [320, 398, 310]

plt.simple_multiple_bar(benchmarks, [jvm, wasm, js], labels = ["JVM", "WASM", "JS"])
plt.title("Compose Multiplatform Relative Benchmark Times (lower is better)")
plt.show()
~~~
```

---

# Wait, did you just say WebGL?

Compose WASM uses WebGL because it's faster than working with the DOM
for applications with enough complexity.

```
~~~python3
import plotext as plt

benchmarks = ["AnimatedVisibility", "LazyGrid", "VisualEffects"]
jvm = [100, 100, 100]
wasm = [150, 180, 155]
js = [320, 398, 310]

plt.simple_multiple_bar(benchmarks, [jvm, wasm, js], labels = ["JVM", "WASM", "JS"])
plt.title("Compose Multiplatform Relative Benchmark Times (lower is better)")
plt.show()
~~~
```

## You can Kotlin/JS with React but is not "Compose"

So you can't reuse components across platforms.

> Ideally, you want to have a Compose module to DOM, but it's not there.

### The DOM model is compatible with Compose though

So maybe someone at some point builds a Compose/DOM version that doesn't depend on WebGL.

---

# Q&A