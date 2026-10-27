# AldemarAqua · Prototipo visual AquaField

**Propuesta UX/UI académica · 25 de octubre de 2026**

Este kit visual se basa en las pantallas Kotlin existentes de `AldemarAqua_MVP_corregido.zip`, la guía del profesor y el anexo de ALDEMAR SpA. **No es un APK ni modifica todavía el proyecto Android**. Sirve para revisar el diseño, ensayar una demostración y aplicarlo progresivamente en Jetpack Compose.

## 1. Cómo abrir el prototipo

1. Extraer el ZIP `AldemarAqua_Kit_Diseno_Visual.zip`.
2. Abrir `AldemarAqua_Prototipo_Interactivo.html` con Chrome, Edge o un navegador actualizado, también sin internet.
3. Ingresar con `OP-01` y clave `1234` (o elegir los perfiles ficticios). Para revisión, usar `SUP-01`; para análisis, `AN-01`.
4. Navegar desde la barra inferior del teléfono simulado o desde el menú lateral en pantalla grande. Cambiar de perfil en el panel derecho.
5. Probar el ciclo: registrar → enviar → observar → corregir → validar → consultar/exportar.

El prototipo usa **solo datos ficticios**, fotografía sintética embebida y cambios de estado simulados en memoria. El botón de exportación descarga un CSV de prueba; el botón de sincronización **no se conecta a ningún servidor**. Los datos del HTML se pierden al recargar la página. La captura de foto no abre la cámara real, porque es un prototipo de interfaz y no la aplicación Android.

## 2. Mapa de pantallas y equivalencia real

| Prototipo | Pantalla del proyecto Android | Función principal |
|---|---|---|
| 01 Acceso | `LoginScreen.kt` | Identificación mediante credenciales ficticias y 3 roles |
| 02 Inicio | `HomeScreen.kt` | Saludo, tareas, indicadores y acción principal según rol |
| 03 Nueva muestra | `NuevaMuestraScreen.kt` | Centro, concesión, tren, línea, tramo, conteo manual, foto y notas; borrador o envío |
| 04 Historial | `MuestrasScreen.kt` | Búsqueda y filtro de estados con visibilidad por rol |
| 05 Detalle | Detalle de `MuestrasScreen.kt` | Datos, imagen, estado, correcciones, densidad y permisos |
| 06 Revisión | `RevisionScreen.kt` | Validar u observar; comentario obligatorio si se observa |
| 07 Resumen | `ResumenScreen.kt` | Totales, densidad, estados, comparación temporal, CSV y sincronización de prueba |

**Estados:** Borrador → Pendiente → Validado, o Pendiente → Observado → Corregido → Validado. El supervisor puede observar nuevamente una muestra corregida. El conteo asistido por Machine Learning **no está disponible ahora** y no se dibuja como una funcionalidad activa.

## 3. Sistema visual listo para trasladar a Compose

| Token | Hex | Uso |
|---|---|---|
| Azul océano | `#09283D` | Cabecera y barra de navegación alta |
| Azul profundo | `#0E4960` | Jerarquía secundaria |
| Turquesa | `#0AB6A5` | Acciones positivas y elementos activos |
| Turquesa oscuro | `#0A817B` | Botones con mejor contraste |
| Verde menta | `#DFF8F1` | Éxito y superficies positivas |
| Fondo hielo | `#F4F8F9` | Fondo general |
| Texto principal | `#183A4A` | Títulos y párrafos |
| Gris azulado | `#75909C` | Metadatos y texto secundario |
| Bordes | `#E2ECEF` | Campos y tarjetas |
| Ámbar | `#E8A64E` | Pendientes |
| Coral | `#D76E65` | Observaciones y advertencias |

**Medidas base:** unidad de separación 8 dp, margen horizontal 20–24 dp, tarjetas 16–20 dp de radio, campos 12 dp, botones de al menos 48 dp de alto y zonas táctiles no menores que 48 × 48 dp. Mantener scroll vertical en formularios largos.

## 4. Archivos Compose incluidos

La subcarpeta `compose_kit/` contiene los siguientes archivos **propuestos, no compilados en Android Studio**:

- `AquaDesignTokens.kt`: paleta de colores.
- `AquaFieldTheme.kt`: Material 3 con colores estables, tipografía y bordes redondeados.
- `AquaComponents.kt`: cabecera, indicadores, botón primario y chip de estado.

Para probarlos:

1. Copiar estos `.kt` al paquete actual `app/src/main/java/com/duoc/aldemaraqua/ui/theme/`.
2. En `MainActivity.kt`, importar `AquaFieldTheme` y sustituir la llamada a `AldemarAquaTheme { ... }` por `AquaFieldTheme { ... }`.
3. Abrir las pantallas existentes y migrar **progresivamente** a los componentes visuales. **Esto solo aplica el tema y componentes base; las siete pantallas no se reemplazan automáticamente por copiar tres archivos.**
4. En `HomeScreen.kt`, comenzar con `AquaHeader`, `AquaMetric` y `AquaPrimaryButton`, respetando sus callbacks existentes.
5. Extender el patrón a `NuevaMuestraScreen`, `MuestrasScreen`, `RevisionScreen`, `ResumenScreen` y `LoginScreen`. Conservar `ViewModel`, repositorios, navegación, validaciones y permisos sin cambios.
6. Compilar con Gradle y verificar accesibilidad, cámara, teclado, modo offline, giro y barras del sistema en emulador/dispositivo.

Ejemplo de uso:

```kotlin
AquaFieldTheme {
    AquaHeader(
        title = "Nueva muestra",
        description = "Identifica, cuenta y documenta el muestreo"
    )
}
```

## 5. Flujo de defensa con el prototipo

**Operador OP-01:** acceder → Nueva muestra → seleccionar ubicación → usar `+10` y `+1` → adjuntar imagen de ejemplo → Enviar. Demostrar que no acepta envío sin fotografía o conteo positivo.

**Supervisor SUP-01:** cambiar rol → Historial → abrir muestra pendiente → Revisión → Observar, ingresando un comentario de cinco o más caracteres.

**Operador OP-01:** volver → Historial → abrir muestra observada propia → corregir y reenviar.

**Supervisor SUP-01:** abrir muestra corregida → Validar.

**Analista AN-01:** Resumen → comparar registros validados por centro–tren–línea → exportar CSV ficticio → sincronización de prueba (dejar claro que no persiste datos en ALDEMAR).

## 6. Precauciones de implementación

- No afirmar que el prototipo HTML guarda datos en Room o se conecta a servicios de ALDEMAR.
- No introducir un botón de ML operativo: solo mantener preparada la estructura (`conteoManual` separado de `cantidadEstimadaML`).
- Las fotografías de demostración son sintéticas y están rotuladas como tales.
- La exportación CSV y la API de prueba no transportan los archivos de imágenes.
- En pantalla Android real, no confundir botones visuales con permisos ya implementados. Seguir las funciones de los ViewModel existentes.
- La comparación temporal debe respetar centro **y** tren **y** línea; no mezclar registros de diferentes ubicaciones.
