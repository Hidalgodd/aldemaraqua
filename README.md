# AldemarAqua

Aplicación Android para registrar y revisar muestras de choritos en centros de cultivo. El proyecto está en desarrollo.

## Funciones

- Inicio de sesión de demostración con los perfiles operador, supervisor y analista.
- Registro de muestras de centros, concesiones (opcional), trenes y líneas ficticios.
- Guardado de borradores y envío a revisión, con conteo estimado táctil, conteo confirmado/corregido, calibre opcional y fotografía desde cámara o galería.
- Historial local con búsqueda y filtros por estado; cada perfil ve los registros permitidos.
- Revisión de muestras pendientes por un supervisor, con validación u observación comentada.
- Edición de borradores y corrección/reenvío de muestras observadas por su operador.
- Resumen con total de muestras, individuos, densidad promedio y comparación cronológica por centro/línea.
- Exportación CSV de muestras validadas desde el perfil analista; incluye datos de concesión y protege texto que podría ejecutarse como fórmula en planillas.
- Persistencia local mediante Room. La sincronización de validadas usa Retrofit y requiere conexión.

## Tecnologías

- Kotlin y Jetpack Compose
- Room y Kotlin Coroutines
- Navigation Compose

## Ejecutar

Abre el proyecto en Android Studio con Android SDK 36 y JDK 17 configurados. También puedes compilar la aplicación desde PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

Ejecuta las pruebas unitarias con:

```powershell
.\gradlew.bat testDebugUnitTest
```

## Cuentas de demostración

| Usuario | Clave | Perfil |
| --- | --- | --- |
| `OP-01` o `OP-02` | `1234` | Registro y corrección de sus muestras |
| `SUP-01` | `1234` | Historial y revisión |
| `AN-01` | `1234` | Reportes y sincronización |

También se aceptan los alias `operador`, `supervisor` y `analista` para las cuentas correspondientes. Son credenciales ficticias; la aplicación no usa autenticación de servidor.

La sincronización envía únicamente muestras validadas a JSONPlaceholder (`/posts`), un servicio REST de prueba. No se debe usar con información real; el resto de las funciones trabaja sin conexión y guarda los datos en el dispositivo.