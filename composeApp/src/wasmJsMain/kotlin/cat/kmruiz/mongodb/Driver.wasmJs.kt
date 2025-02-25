package cat.kmruiz.mongodb

import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response

var interval: Int? = null
var connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
var databases = MutableStateFlow(emptyList<MongoDbDatabase>())

external class ServerMongoDbDatabase : JsAny {
    val name: String
    val size: JsBigInt
}

@JsFun("console.log")
external fun consoleLog(v: JsAny?)

fun connectRequestConfig(url: JsString): RequestInit = js("({ method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ url }) })")
fun disconnectRequestConfig(): RequestInit = js("({ method: 'POST' })")

actual suspend fun connectTo(url: String) {
    if (interval != null) {
        window.clearInterval(interval!!)
        interval = null
    }
    connectionState.emit(ConnectionState.Connecting)

    val response = window.fetch("http://localhost:8085/connect", connectRequestConfig(url.toJsString()))
        .await<Response>()

    if (!response.ok) {
        connectionState.emit(ConnectionState.Failed(response.text().await<JsString>().toString()))
        return
    }

    connectionState.emit(ConnectionState.Connected)
    interval = window.setInterval({
        GlobalScope.launch {
            val response = window.fetch("http://localhost:8085/databases").await<Response>()
            if (!response.ok) {
                databases.emit(emptyList())
            } else {
                val serverDbs = response.json().await<JsArray<ServerMongoDbDatabase>>().toList()
                val formattedDbs = serverDbs.map {
                    MongoDbDatabase(it.name, it.size.toLong())
                }.sortedBy { it.name }
                if (formattedDbs != databases.value) {
                    databases.emit(formattedDbs)
                }
            }
        }
        null
    }, 250)
}

actual suspend fun disconnect() {
    window.fetch("http://localhost:8085/disconnect", disconnectRequestConfig()).await<Response>()
    databases.emit(emptyList())
    connectionState.emit(ConnectionState.Disconnected)
}

actual fun connectionStatus(): StateFlow<ConnectionState> {
    return connectionState
}

actual fun listDatabases(): StateFlow<List<MongoDbDatabase>> {
    return databases
}