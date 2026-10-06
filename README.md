# AldemarAqua

Aplicación Android para registrar y revisar muestras de choritos en centros de cultivo. El proyecto está en desarrollo.

## Funciones disponibles

- Inicio de sesión de demostración con perfiles de operador y supervisor.
- Registro de muestras con centro, tren, línea, fecha, hora, tramo, operador, observaciones y fotografía.
- Ingreso manual de conteo estimado y confirmado. El conteo automático todavía no está conectado.
- Historial local de muestras con búsqueda y filtros por estado.
- Revisión de muestras por supervisor, con estados observado, corregido o validado y un campo de comentario.
- Persistencia local mediante Room.

## Tecnologías

- Kotlin y Jetpack Compose
- Room y Kotlin Coroutines
- Navigation Compose

## Ejecutar

Abre el proyecto en Android Studio con Android SDK 36 y JDK 17 configurados. También puedes compilar la aplicación desde PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

## Acceso de demostración

| Usuario | Clave | Perfil |
| --- | --- | --- |
| `operador` | `1234` | Registro e historial |
| `supervisor` | `1234` | Registro, historial y revisión |

Estas credenciales están definidas para pruebas locales; la aplicación aún no usa autenticación de servidor.