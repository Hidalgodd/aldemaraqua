# AquaField integrado en AldemarAqua (25-10-2026)

Esta es la **aplicación Android Kotlin / Jetpack Compose**, no solo el prototipo HTML. Las pantallas se adaptaron tomando como referencia el kit visual AquaField y sin modificar el modelo Room ni los ViewModels de registro y revisión.

## Cambios en la interfaz

| Pantalla Android | Actualización |
|---|---|
| `LoginScreen.kt` | Cabecera AquaField, formulario blanco, botones de acceso para los 3 perfiles |
| `HomeScreen.kt` | Inicio con indicadores reales (según perfil), tarjetas de tareas, barra inferior |
| `NuevaMuestraScreen.kt` | Formulario en pasos visuales: localización, conteo, fotografía, guardar/enviar |
| `MuestrasScreen.kt` | Buscador, filtros y tarjetas de historial con estados y métricas |
| `DetalleMuestraScreen.kt` (nuevo) | Vista independiente de fotografía, datos, densidad, observaciones y acciones por rol |
| `RevisionScreen.kt` | Datos evidenciables y revisión del supervisor en secciones |
| `ResumenScreen.kt` | Indicadores y análisis, exportación CSV y sincronización de prueba |

## Componentes y recursos

- `ui/theme/AquaDesignTokens.kt`: colores AquaField.
- `ui/theme/AquaFieldTheme.kt`: esquema Material 3, tipografía y esquinas.
- `ui/theme/AquaComponents.kt`: componentes originales del kit.
- `ui/theme/AquaUi.kt`: hero, tarjetas de secciones, barra inferior, indicadores y etiquetas.
- `ui/theme/Theme.kt`: conserva el nombre `AldemarAquaTheme` y usa AquaField.
- `res/drawable/ic_launcher_*.xml`: icono adaptable con gota y ola marina.
- `MainActivity.kt`: navegación a detalle y callbacks de barra inferior; indicadores a partir de Room.

## Cómo abrir y comprobar

1. Abrir **la carpeta raíz `aldemaraqua-main`** en Android Studio.
2. Esperar la sincronización de Gradle y solucionar cualquier solicitud de instalación del SDK 36 / JDK 17.
3. Ejecutar `app` en emulador Android o teléfono, con permisos de cámara si los solicita.
4. Probar con perfiles ficticios `OP-01`, `SUP-01`, `AN-01`; clave `1234`.
5. Comprobar el flujo: crear muestra (operador) → enviar → ver detalle → observar (supervisor) → corregir (operador) → validar (supervisor) → ver resumen/exportar (analista).
6. Comprobar galería, cámara, persistencia tras cerrar, modo avión, filtros y sincronización sin conexión.

## Alcance y limitaciones

- **No se implementó Machine Learning.** El conteo es manual y el modelo de datos conserva los campos futuros de ML.
- El CSV exporta la referencia local de la foto, no el archivo binario.
- La sincronización sigue siendo una **API de prueba**, no el servidor de ALDEMAR.
- La seguridad y credenciales son ficticias, adecuadas solo para demostración académica.
- Se verificó la sintaxis Kotlin de forma aislada (sin errores del analizador), las referencias locales, las rutas y los recursos XML. **No fue posible compilar un APK en este entorno** por la ausencia de Android SDK y Gradle instalado; deben ejecutar Gradle, pruebas y demo en Android Studio para confirmar el funcionamiento final.
- El ZIP original tampoco contenía `gradle/wrapper/gradle-wrapper.jar`. Android Studio puede importar y sincronizar el proyecto mediante su Gradle; para ejecutar `./gradlew` por terminal se necesita regenerar el wrapper o incorporar el JAR legítimo del wrapper de Gradle.

La carpeta `docs/aquafield` incluye también el prototipo HTML y las imágenes de referencia para comparar con la implementación real. El HTML **no forma parte del APK**.
