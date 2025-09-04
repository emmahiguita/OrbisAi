package com.example.orbisai.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.orbisai.ui.theme.OrbisAITheme

@Composable
fun EASectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionIcon: ImageVector = Icons.Default.MoreVert,
    onActionClick: (() -> Unit)? = null,
    showAction: Boolean = onActionClick != null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (showAction) {
            IconButton(
                onClick = { onActionClick?.invoke() }
            ) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = "Más opciones",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Previews para Android Studio
@Preview(showBackground = true, name = "Section Header Preview")
@Composable
fun EASectionHeaderPreview() {
    OrbisAITheme {
        EASectionHeader(
            title = "Sección de Ejemplo",
            subtitle = "Subtítulo opcional"
        )
    }
}

@Preview(showBackground = true, name = "Section Header with Action Preview")
@Composable
fun EASectionHeaderWithActionPreview() {
    OrbisAITheme {
        EASectionHeader(
            title = "Sección con Acción",
            subtitle = "Con botón de más opciones",
            showAction = true,
            onActionClick = { /* Acción */ }
        )
    }
}
