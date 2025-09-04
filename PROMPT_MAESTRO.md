# 🚀 PROMPT MAESTRO - Jetpack Compose Material 3

## 📋 **INSTRUCCIONES BASE**

Genera código en **Kotlin con Jetpack Compose (Material 3)**, listo para compilar en Android Studio sin errores.

## ⚠️ **REGLAS OBLIGATORIAS**

### 1. **Imports Correctos**
```kotlin
// ✅ CORRECTO - LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items

// ❌ INCORRECTO - No usar
import androidx.compose.foundation.lazy.LazyVerticalGrid
```

### 2. **TopAppBar Material 3**
```kotlin
// ✅ CORRECTO - Material 3
TopAppBar(
    title = { Text("Título") },
    actions = { /* acciones */ },
    colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
)

// ❌ INCORRECTO - No usar
SmallTopAppBar() // No existe en Material 3
```

### 3. **Íconos Material Icons**
```kotlin
// ✅ CORRECTO - Imports
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

// ✅ CORRECTO - Uso
Icon(
    imageVector = Icons.Default.Search,
    contentDescription = "Buscar"
)

// ❌ INCORRECTO - No usar íconos que no existen
Icons.Default.FilterAlt // Solo si está en material-icons-extended
```

### 4. **Funciones Composable**
```kotlin
// ✅ CORRECTO - Toda función que dibuje UI debe ser @Composable
@Composable
fun MiComponente() {
    Text("Hola")
}

// ❌ INCORRECTO - No llamar Composable desde función normal
fun funcionNormal() {
    Text("Hola") // ERROR: @Composable invocations can only happen from @Composable
}
```

### 5. **Dependencias Correctas**
```kotlin
// ✅ CORRECTO - build.gradle.kts
implementation(platform("androidx.compose:compose-bom:2025.01.00"))
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.material:material-icons-extended")
implementation("androidx.compose.foundation:foundation")

// ❌ INCORRECTO - No duplicar dependencias
implementation("androidx.compose.material3:material3:1.2.0") // No especificar versión
```

### 6. **Versiones Compatibles**
```toml
# ✅ CORRECTO - libs.versions.toml
[versions]
kotlin = "1.9.22"                    # Compatible con Compose Compiler 1.5.8
composeCompiler = "1.5.8"           # Requiere Kotlin 1.9.22
composeBom = "2025.01.00"           # BOM de Compose

# ❌ INCORRECTO - Versiones incompatibles
kotlin = "1.9.10"                   # No compatible con Compose Compiler 1.5.8
composeCompiler = "1.5.8"           # Requiere Kotlin 1.9.22
```

### 7. **Elevación en Material 3**
```kotlin
// ✅ CORRECTO - Sintaxis Material 3
CardDefaults.cardElevation(4.dp)
FilterChipDefaults.filterChipElevation(2.dp, 4.dp)
ButtonDefaults.buttonElevation(4.dp, 2.dp)

// ❌ INCORRECTO - Parámetros nombrados no válidos
CardDefaults.cardElevation(defaultElevation = 4.dp)
FilterChipDefaults.filterChipElevation(defaultElevation = 2.dp, selectedElevation = 4.dp)
ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 2.dp)
```

### 8. **Anotaciones @Composable**
```kotlin
// ✅ CORRECTO - Funciones de UI marcadas como @Composable
@Composable
private fun MyComponent() {
    Card { Text("Hola") }
}

// ✅ CORRECTO - Funciones de datos NO marcadas como @Composable
private fun getData(): List<Item> {
    return listOf(Item("1"), Item("2"))
}

// ❌ INCORRECTO - Función de datos marcada como @Composable
@Composable
private fun getData(): List<Item> { ... }

// ❌ INCORRECTO - Función de UI sin @Composable
private fun MyComponent() {
    Card { Text("Hola") } // ERROR
}
```

## 🎯 **ESTRUCTURA DE ARCHIVO TÍPICA**

```kotlin
package com.example.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiPantalla() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Pantalla") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Contenido",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}
```

## 🔧 **CHECKLIST ANTES DE ENTREGAR**

- [ ] **Imports correctos** - LazyVerticalGrid desde `foundation.lazy.grid`
- [ ] **TopAppBar Material 3** - No usar SmallTopAppBar
- [ ] **Íconos disponibles** - Solo usar íconos de `material.icons.filled.*`
- [ ] **Funciones @Composable** - Toda función que dibuje UI debe ser @Composable
- [ ] **Sin APIs experimentales** - No usar @ExperimentalFoundationApi innecesariamente
- [ ] **Dependencias sin duplicar** - Usar BOM de Compose
- [ ] **Colores consistentes** - Usar MaterialTheme.colorScheme
- [ ] **Versiones compatibles** - Kotlin 1.9.22 con Compose Compiler 1.5.8
- [ ] **Elevación correcta** - Sintaxis Material 3 sin parámetros nombrados
- [ ] **Anotaciones @Composable** - Funciones de UI marcadas correctamente
- [ ] **Sin errores de compilación** - Código 100% compilable

## 🚨 **ERRORES COMUNES A EVITAR**

1. **`Unresolved reference 'LazyVerticalGrid'`** → Usar import correcto
2. **`Unresolved reference 'SmallTopAppBar'`** → Usar TopAppBar de Material 3
3. **`Unresolved reference 'FilterAlt'`** → Verificar que el ícono existe
4. **`@Composable invocations can only happen from @Composable`** → Marcar función como @Composable
5. **`Duplicate class found`** → No duplicar dependencias
6. **`Compose Compiler requires Kotlin version X`** → Verificar compatibilidad de versiones
7. **`No parameter with name 'defaultElevation' found`** → Usar sintaxis Material 3 sin parámetros nombrados
8. **`@Composable invocations can only happen from @Composable`** → Marcar funciones de UI como @Composable
9. **`Expression 'weight' of type 'Float' cannot be invoked`** → Verificar anotaciones @Composable

## 📱 **EJEMPLO COMPLETO Y CORRECTO**

```kotlin
@Composable
fun EjemploCorrecto() {
    var showDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ejemplo") },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, "Agregar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(listOf("Item 1", "Item 2")) { item ->
                Card(
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Default.Star, contentDescription = null)
                        Text(item)
                    }
                }
            }
        }
    }
}
```

---

**✅ Con este prompt, tu código será 100% compilable y seguirá las mejores prácticas de Material 3**
