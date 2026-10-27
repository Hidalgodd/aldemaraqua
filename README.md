# AldemarAqua · MVP Android (DSY1105)


App de **demostración académica** para ALDEMAR SpA: registro y trazabilidad de muestras de choritos. Se trabaja exclusivamente con datos ficticios. No es una aplicación productiva ni automatiza todavía el conteo con Machine Learning.

## Lo que está implementado

- Inicio de sesión **ficticio** con `OP-01`, `OP-02`, `SUP-01` y `AN-01` (clave `1234`). Los usuarios no tienen autenticación real de servidor.
- Operador: centro, concesión opcional, tren, línea, fecha y hora, tramo, **conteo manual** (−1/+1/+10), corrección opcional, calibre, observaciones y foto de cámara o galería.
- Borradores (pueden editarse/eliminarse); al enviar se exige un conteo positivo y una fotografía existente.
- Supervisor: observa (comentario de 5 caracteres mínimo) o valida. No se permite validar una muestra sin conteo o sin foto.
- Operador: corrige y reenvía las muestras observadas. El historial tiene filtros y accesos por rol.
- Analista: solo ve validadas, compara por **centro + tren + línea + fecha**, exporta CSV y simula sincronización de validadas con un endpoint REST público de demostración.
- Room mantiene las muestras y fotografías en el dispositivo. Migración de base desde la versión 4 a 5 sin borrar datos previos.
- Resumen: cantidad de muestras por estado, total de individuos, densidad media en individuos por metro.

## Demo de evidencia fotográfica

La primera ejecución con base vacía crea 4 muestras ficticias. Tres incluyen una **ilustración sintética rotulada** que sirve para demostrar la asociación imagen-registro; no es una fotografía real del cultivo. El borrador se crea sin imagen. En instalaciones que ya tenían una base de versiones anteriores, los ejemplos existentes se conservan (pueden no tener foto). Para repetir la demo desde cero, borrar datos de la app en el emulador; esto también elimina todas las muestras y fotos personales de prueba guardadas.

## Compilar y probar

Requisitos: Android Studio con **SDK 36** y **JDK 17**. Desde la raíz del proyecto:

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

En Windows usa `gradlew.bat` en lugar de `./gradlew`. El APK debug se encontrará en `app/build/outputs/apk/debug/`. Para un APK firmado release, usar *Build → Generate Signed Bundle / APK* y una clave administrada por el equipo. No se incluye ninguna llave privada.

## Flujo para la defensa

1. En `OP-01`, crear una muestra con el teléfono o galería (si es galería, foto **ficticia**), guardar como borrador, editarla y enviarla.
2. En `SUP-01`, buscar la muestra, observarla con comentario.
3. En `OP-01`, corregirla y reenviarla (estado **Corregido**).
4. En `SUP-01`, validarla, verificando foto y conteo.
5. En `AN-01`, mostrar el histórico por ubicación, exportar CSV y simular sincronización con internet disponible.
6. Desactivar internet para mostrar creación/consulta local y el mensaje de error si se intenta sincronizar sin red.

## Arquitectura y crecimiento

`ui/screen` (vistas Compose), `viewmodel` (estado y operaciones), `data` (Room/DAO/repositorio/Retrofit), `util` (reglas y validaciones testeables). Para ML futuro se debe agregar un módulo que procese imágenes y devuelva una sugerencia separada del conteo manual, con confirmación humana y trazabilidad de la fuente.

Consulta también `REVISION_Y_PRUEBAS.md` para la matriz de requisitos y verificación antes de la defensa.

## Interfaz AquaField integrada (25-10-2026)

El proyecto incluye el diseño **AquaField aplicado a las pantallas reales de Jetpack Compose**, con pantalla de detalle independiente y barra de navegación según rol. Mantiene Room, ViewModels, cámara, CSV y sincronización de prueba. Ver [`docs/aquafield/INTEGRACION_REALIZADA.md`](docs/aquafield/INTEGRACION_REALIZADA.md) para alcance, pantallas, instalación y pruebas.
