package cat.kmruiz.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cat.kmruiz.mongodb.listDatabases

@Composable
fun DatabaseList() {
    val databases by listDatabases().collectAsState()
    val scrollState = rememberScrollState()

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.background(Color.Green)) {
            Text("Database Name", modifier = Modifier.border(1.dp, Color.Black).weight(0.8f).padding(16.dp))
            Text("Database Size", modifier = Modifier.border(1.dp, Color.Black).weight(0.2f).padding(16.dp))
        }

        Column(Modifier.fillMaxWidth().verticalScroll(scrollState)) {
            for (database in databases) {
                Row {
                    Text(database.name, modifier = Modifier.border(1.dp, Color.Black).weight(0.8f).padding(8.dp))
                    Row(Modifier.border(1.dp, Color.Black).weight(0.2f).padding(8.dp)) {
                        DatabaseSizeTag(database.size)
                    }
                }
            }
        }
    }
}

@Composable
fun DatabaseSizeTag(size: Long) {
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

    Text("${normSize}${normUnit}", modifier = Modifier
        .background(tagColor)
        .border(width = 1.dp, color = tagColor, shape = RoundedCornerShape(32.dp))
        .padding(horizontal = 32.dp)
    )
}