package cat.kmruiz.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cat.kmruiz.mongodb.ConnectionState
import cat.kmruiz.mongodb.connectTo
import cat.kmruiz.mongodb.connectionStatus
import cat.kmruiz.mongodb.disconnect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DatabaseConnectionHandler() {
    val connectionState by connectionStatus().collectAsState()
    var currentUrl by remember { mutableStateOf("mongodb://localhost:27017") }
    val coroutineScope = rememberCoroutineScope()

    fun connectToCluster() {
        coroutineScope.launch(Dispatchers.Default) {
            connectTo(currentUrl)
        }
    }

    fun disconnectFromCluster() {
        coroutineScope.launch(Dispatchers.Default) {
            disconnect()
        }
    }

    Column(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalAlignment = Alignment.CenterHorizontally) {
        if (connectionState is ConnectionState.Failed) {
            val failedStatus = (connectionState as ConnectionState.Failed)

            Row {
                Text(failedStatus.message, modifier = Modifier.background(Color(0.9f, 0.3f, 0.3f)).fillMaxWidth())
            }
        }

        Row {
            val canEditUrl by derivedStateOf {
                connectionState == ConnectionState.Disconnected || connectionState is ConnectionState.Failed
            }

            TextField(currentUrl, enabled = canEditUrl, modifier = Modifier.weight(0.7f), onValueChange = {
                currentUrl = it
            })

            when (connectionState) {
                ConnectionState.Disconnected, is ConnectionState.Failed -> {
                    Button(modifier = Modifier.weight(0.3f).fillMaxHeight(), onClick = { connectToCluster() }) {
                        Text("Connect")
                    }
                }
                ConnectionState.Connecting -> {
                    Button(modifier = Modifier.weight(0.3f).fillMaxHeight(), enabled = false, onClick = { disconnectFromCluster() }) {
                        Text("Connecting...")
                    }
                }
                ConnectionState.Connected -> {
                    Button(modifier = Modifier.weight(0.3f).fillMaxHeight(), onClick = { disconnectFromCluster() }) {
                        Text("Disconnect")
                    }
                }
            }
        }
    }
}