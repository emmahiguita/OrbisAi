package com.example.orbisai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.orbisai.ui.theme.OrbisAITheme

/**
 * Tarjeta para interactuar con el Asistente de IA
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantCard(
    onAnalyzeTrends: () -> Unit,
    onGetRecommendations: () -> Unit,
    onAskQuestion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showQuestionDialog by remember { mutableStateOf(false) }
    var question by remember { mutableStateOf("") }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "AI Assistant",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "🤖 Asistente de IA Financiero",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Text(
                text = "Obtén análisis inteligente de tus datos financieros",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            
            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Analizar Tendencias
                OutlinedButton(
                    onClick = onAnalyzeTrends,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "📈 Tendencias",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                
                // Botón Recomendaciones
                OutlinedButton(
                    onClick = onGetRecommendations,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "💡 Ideas",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            
            // Botón Hacer Pregunta
            Button(
                onClick = { showQuestionDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "💬 Hacer una pregunta",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
    
    // Diálogo para hacer preguntas
    if (showQuestionDialog) {
        AlertDialog(
            onDismissRequest = { showQuestionDialog = false },
            title = {
                Text(
                    text = "💬 Pregunta al Asistente IA",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    Text(
                        text = "¿Qué te gustaría saber sobre tus finanzas?",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = question,
                        onValueChange = { question = it },
                        placeholder = { Text("Ej: ¿Cómo puedo reducir gastos?") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (question.isNotBlank()) {
                            onAskQuestion(question)
                            question = ""
                            showQuestionDialog = false
                        }
                    }
                ) {
                    Text("Preguntar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showQuestionDialog = false 
                        question = ""
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AIAssistantCardPreview() {
    OrbisAITheme {
        AIAssistantCard(
            onAnalyzeTrends = { },
            onGetRecommendations = { },
            onAskQuestion = { }
        )
    }
}
