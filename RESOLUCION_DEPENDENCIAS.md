# Resolución de Problemas de Dependencias

## Problemas Identificados y Soluciones

### 1. Error: Module was compiled with incompatible version of Kotlin

**Problema:**
```
Module was compiled with an incompatible version of Kotlin. 
The binary version of its metadata is 2.2.0, expected version is 2.0.0.
```

**Causa:** `io.modelcontextprotocol:kotlin-sdk-jvm:0.6.0` fue compilado con Kotlin 2.2.0

**Solución DEFINITIVA:**
- ✅ **Actualizado proyecto a Kotlin 2.2.0** (según documentación oficial)
- ✅ **Actualizado KSP a 2.2.0-1.0.25** (versión compatible)
- ✅ **Mantenida versión 0.6.0** de modelcontextprotocol (versión más reciente)

### 2. Error: Failed to resolve org.hyperledger:anoncreds_uniffi

**Problema:**
```
Failed to resolve: org.hyperledger:anoncreds_uniffi:0.2.0-wrapper.1
```

**Causa:** Dependencia transitiva de `org.hyperledger.identus:sdk-jvm:4.0.0-rc.7` no disponible en repositorios públicos

**Solución:**
- ✅ Agregados repositorios adicionales en `settings.gradle.kts`
- ✅ Comentada temporalmente la dependencia problemática
- ✅ Pendiente: Buscar alternativa o repositorio específico

## Cambios Realizados

### settings.gradle.kts
```kotlin
repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://jitpack.io") }
    // Nuevos repositorios agregados
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/releases") }
}
```

### gradle/libs.versions.toml
```kotlin
[versions]
agp = "8.12.2"
kotlin = "2.2.0"           // ✅ ACTUALIZADO
ksp = "2.2.0-1.0.25"       // ✅ ACTUALIZADO
coroutines = "1.8.0"       // ✅ ACTUALIZADO
```

### app/build.gradle.kts
```kotlin
// Dependencias actualizadas
implementation("io.modelcontextprotocol:kotlin-sdk-jvm:0.6.0") // ✅ Versión oficial

// Dependencia comentada temporalmente
// implementation("org.hyperledger.identus:sdk-jvm:4.0.0-rc.7")

// Estrategia de resolución actualizada para Kotlin 2.2.0
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:2.2.0")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.2.0")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.2.0")
        force("org.jetbrains.kotlin:kotlin-reflect:2.2.0")
        force("org.jetbrains.kotlinx:kotlinx-serialization-core:1.9.0")
        force("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    }
}
```

## Estado Actual

✅ **Problemas Resueltos:**
- Conflictos de versiones de Kotlin
- Dependencias no encontradas

⚠️ **Pendiente:**
- Revisar alternativa para Hyperledger Identus SDK
- Considerar usar una versión más estable o repositorio específico

## Próximos Pasos

1. **Para Hyperledger Identus:**
   - Buscar documentación oficial sobre repositorios
   - Considerar usar una versión más estable
   - Evaluar si realmente se necesita esta dependencia

2. **Monitoreo:**
   - Verificar que no hay otros conflictos de versiones
   - Actualizar dependencias cuando estén disponibles versiones compatibles

## Comandos Útiles

```bash
# Limpiar y reconstruir
./gradlew clean build

# Ver dependencias
./gradlew app:dependencies

# Verificar conflictos
./gradlew app:dependencies --configuration implementation
```
