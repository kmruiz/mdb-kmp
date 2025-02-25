package cat.kmruiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cat.kmruiz.components.DatabaseConnectionHandler
import cat.kmruiz.components.DatabaseList
import cat.kmruiz.mongodb.ConnectionState
import cat.kmruiz.mongodb.connectionStatus
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val connectionState by connectionStatus().collectAsState()
    val showContent by derivedStateOf { connectionState == ConnectionState.Connected }

    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            DatabaseConnectionHandler()

            AnimatedVisibility(showContent) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    DatabaseList()
                }
            }
        }
    }
}