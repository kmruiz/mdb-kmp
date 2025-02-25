package cat.kmruiz.mongodb

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.bson.Document
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

var client: MongoClient? = null
var dbListenerJob: Job? = null
var connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
var databases = MutableStateFlow(emptyList<MongoDbDatabase>())

actual suspend fun connectTo(url: String) {
    disconnect()
    connectionState.emit(ConnectionState.Connecting)
    val clientConnection = runCatching {
        val client = MongoClients.create(MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(url))
            .timeout(5, TimeUnit.SECONDS)
            .build()
        )
        client.getDatabase("admin").runCommand(Document("hello", 1))
        client
    }

    client = clientConnection.getOrNull()
    clientConnection.onFailure {
        connectionState.emit(ConnectionState.Failed(it.message ?: "Could not connect. Unknown error."))
    }

    clientConnection.onSuccess {
        connectionState.emit(ConnectionState.Connected)
    }

    dbListenerJob = GlobalScope.launch(Dispatchers.IO) {
        databases.emit(emptyList())

        while (true) {
            val dbsFromServer = client?.listDatabases()?.into(mutableListOf<Document>()) ?: emptyList()

            val dbs = dbsFromServer.map {
                MongoDbDatabase(it["name"].toString(), it["sizeOnDisk"].toString().toLong())
            }.sortedBy { it.name }

            if (dbs != databases.value) {
                databases.emit(dbs)
            }

            delay(50.milliseconds)
        }
    }
}

actual suspend fun disconnect() {
    client?.close()
    dbListenerJob?.cancelAndJoin()
    connectionState.emit(ConnectionState.Disconnected)
}

actual fun connectionStatus(): StateFlow<ConnectionState> {
    return connectionState
}

actual fun listDatabases(): StateFlow<List<MongoDbDatabase>> {
    return databases
}