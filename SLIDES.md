# Kotlin Multiplatform

```
~~~graph-easy --as=boxart
[ Kotlin ] - for -> [ Desktop ], [ Mobile ], [ Web ]
~~~
```

---
# Kotlin?

Kotlin is an open-source, modern multi-paradigm programming language developed by JetBrains
> The folks that build IntelliJ.

---
# Kotlin?

Kotlin is an open-source, modern multi-paradigm programming language developed by JetBrains
> The folks that build IntelliJ.

## It uses a lightweight syntax, familiar to Swift or JavaScript developers.

```kt
fun helloWorld() {
    println("Hello World!")
}
```
---
# Kotlin?

Kotlin is an open-source, modern multi-paradigm programming language developed by JetBrains
> The folks that build IntelliJ.

## It uses a lightweight syntax, familiar to Swift or JavaScript developers.

```kt
fun helloWorld() {
    println("Hello World!")
}
```
* Official language by Google for Android developers.
* Official DSL for Gradle
* Official languages for Spring (one of the most important Java frameworks)
---
# Kotlin?

Kotlin is an open-source, modern multi-paradigm programming language developed by JetBrains
> The folks that build IntelliJ.

## It uses a lightweight syntax, familiar to Swift or JavaScript developers.

```kt
fun helloWorld() {
    println("Hello World!")
}
```
* Official language by Google for Android developers.
* Official DSL for Gradle
* Official languages for Spring (one of the most important Java frameworks)

### It started strongly tied to Android Development

* Now it's used for server applications (Ktor/Vert.x)
> ↗️  Vert.x is the only JVM-based framework the top 7 most performant framework.
> According to TechEmpower. 
* It's used for desktop applications (Compose/Desktop)
> It's GPU rendered by default, like a web browser or a video game. 
* And also for web applications (via Kotlin/JS)
> It can use the DOM directly, several JS libraries, even React and Redux.
---

# How does Multiplatform work?

## Essentially, it's a compiler with multiple backends

```
~~~graph-easy --as=boxart
[ Kotlin ] - to -> [ IR ] - to -> [ Your backend ]
~~~
```
> **IR** stands for _Intermediate Representation_ and is usually a simplified version of the source code.
> It's used for dead code elimination _(tree shaking)_, optimisation and actual output generation. 

## This IR approach is common in any compiler that targets multiple platforms

* clang
* gcc
* rustc

### They contain their own IR where the apply optimisations and transform your code

---

# How does it work?

## It links dependencies that are related to the target platform

```
~~~graph-easy --as=boxart
[ Kotlin ] - is transformed to -> [ IR ]
[ IR ] - used by -> [ Kotlin/WASM ] - bundled for -> [ Browser ]
[ IR ] - used by -> [ Kotlin/Java ] - bundled for -> [ Desktop ]
[ IR ] - used by -> [ Kotlin/Native ] - uses -> [ LLVM ]
[ LLVM ] - compiles to -> [ macOS ] 
[ LLVM ] - compiles to -> [ iOS / tvOS / watchOS ] 
[ LLVM ] - compiles to -> [ Linux ] 
[ LLVM ] - compiles to -> [ Windows ] 
[ LLVM ] - compiles to -> [ Android NDK ] 
~~~
```
> Fun fact: LLVM is the backend for Rust and Swift

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

# How do we use platform specific code then?

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

# How do we use platform specific code then?

```
~~~graph-easy --as=boxart
[ commonMain ] -> (expect)
[ commonMain ] - actual -> [ desktopMain ]
[ commonMain ] - actual -> [ wasmJsMain ]
~~~
```

* commonMain contains the `expect`
* desktopMain contains the `actual` _java_ version
* wasmJsMain contains the `actual` _wasm_ version

## You can use any platform library in the implementation code!

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

# Wait, did you just say WebGL?

Compose WASM uses WebGL because it's faster than working with the DOM
for applications with enough complexity.

## Compose Multiplatform Relative Benchmark Times _(lower is better)_
```
~~~python3
import plotext as plt

benchmarks = ["AnimatedVisibility", "LazyGrid", "VisualEffects"]
jvm = [100, 100, 100]
wasm = [150, 180, 155]
js = [320, 398, 310]

plt.simple_multiple_bar(benchmarks, [jvm, wasm, js], labels = ["JVM", "WASM", "JS"])
plt.show()
~~~
```
[Reference: Kotlin/Wasm performance](https://kotlinlang.org/docs/wasm-overview.html#kotlin-wasm-performance)

---

# Wait, did you just say WebGL?

Compose WASM uses WebGL because it's faster than working with the DOM
for applications with enough complexity.

## Compose Multiplatform Relative Benchmark Times _(lower is better)_
```
~~~python3
import plotext as plt

benchmarks = ["AnimatedVisibility", "LazyGrid", "VisualEffects"]
jvm = [100, 100, 100]
wasm = [150, 180, 155]
js = [320, 398, 310]

plt.simple_multiple_bar(benchmarks, [jvm, wasm, js], labels = ["JVM", "WASM", "JS"])
plt.show()
~~~
```
[Reference: Kotlin/Wasm performance](https://kotlinlang.org/docs/wasm-overview.html#kotlin-wasm-performance)

## You can use Kotlin/JS with React but it's not "Compose"

* So you can't reuse components across platforms.
* But the final package is smaller

### The DOM model is fairly similar to Compose though

* So maybe someone at some point builds a Compose/DOM version that doesn't depend on WebGL.

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

# Q&A