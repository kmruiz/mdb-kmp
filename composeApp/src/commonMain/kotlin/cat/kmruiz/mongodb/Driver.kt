package cat.kmruiz.mongodb

import kotlinx.coroutines.flow.StateFlow

sealed interface ConnectionState {
    data object Disconnected: ConnectionState
    data object Connecting: ConnectionState
    data object Connected: ConnectionState
    data class Failed(val message: String): ConnectionState
}

data class MongoDbDatabase(val name: String, val size: Long)

expect suspend fun connectTo(url: String)
expect suspend fun disconnect()

expect fun connectionStatus(): StateFlow<ConnectionState>
expect fun listDatabases(): StateFlow<List<MongoDbDatabase>>