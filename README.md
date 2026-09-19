# EntrenaIPN

<p align="center"><em>App Android para registrar, monitorear y dar seguimiento a sesiones de entrenamiento físico — carrera, caminata, ciclismo, natación, yoga y fuerza.</em></p>

<p align="center">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.2.0-7F52FF?logo=kotlin&logoColor=white">
  <img alt="Android" src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white">
  <img alt="minSdk" src="https://img.shields.io/badge/minSdk-27-blue">
</p>

## Tabla de contenido
- [Descripción](#descripción)
- [Flujo de uso](#flujo-de-uso)
- [Características](#características)
- [Capturas de pantalla](#capturas-de-pantalla)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Arquitectura y estructura del proyecto](#arquitectura-y-estructura-del-proyecto)
- [Modelo de datos](#modelo-de-datos)
- [Requisitos previos](#requisitos-previos)
- [Instalación y configuración](#instalación-y-configuración)
- [Notas de seguridad](#notas-de-seguridad)
- [Permisos utilizados](#permisos-utilizados)
- [Equipo](#equipo)
- [Licencia](#licencia)

## Descripción
**EntrenaIPN** es una aplicación Android nativa escrita en Kotlin que permite registrar, monitorear y llevar un historial de sesiones de entrenamiento físico. Incluye registro con boleta institucional, pensada para la comunidad de ESCOM-IPN.

La app soporta seis tipos de actividad —carrera, caminata, ciclismo, natación, yoga y fuerza—, cada una con su propia pantalla de seguimiento, cronómetro y almacenamiento de detalles. Las actividades al aire libre (carrera, caminata, ciclismo) incorporan seguimiento por GPS con Google Maps, dibujando la ruta recorrida en tiempo real.

Toda la información (usuarios, entrenamientos, configuraciones) se persiste **localmente en el dispositivo** mediante Room (SQLite); la app no depende de un backend remoto.

## Flujo de uso
1. El usuario se registra (correo, boleta, contraseña) o inicia sesión.
2. Llega al **panel principal**, con su resumen mensual, meta semanal y logros.
3. Elige **"Nuevo entrenamiento"**, selecciona actividad, lugar y duración estimada.
4. Inicia el cronómetro; puede pausar, reanudar y finalizar la sesión (con mapa y voz si es carrera/caminata/ciclismo).
5. Al finalizar, la sesión queda guardada y visible en el **historial**.
6. Desde **configuración** puede editar su perfil y activar recordatorios de entrenamiento.

## Características

**Cuenta y perfil**
- Registro e inicio de sesión local (correo + contraseña + boleta).
- Edición de perfil con foto (cámara o galería, vía `FileProvider`).
- Pantallas de privacidad, términos y contacto.

**Panel principal**
- Resumen mensual: total del mes, número de actividades, calorías estimadas.
- Progreso hacia una meta semanal configurable.
- Gráfico de actividad por día de la semana y saludo dinámico según la hora.
- Insignias ("chips") por logros alcanzados.

**Registro de entrenamientos** (6 modalidades)

| Actividad | Pantalla | Seguimiento GPS |
|---|---|:---:|
| Carrera | `CarreraActivity` | ✅ |
| Caminata | `CaminataActivity` | ✅ |
| Ciclismo | `BicicletaActivity` | ✅ |
| Natación | `NatacionActivity` | — |
| Yoga | `YogaActivity` | — |
| Fuerza | `FuerzaActivity` | — |

- Cronómetro con iniciar / pausar / reanudar / finalizar.
- Mapa en vivo con ruta dibujada (`GoogleMap` + `Polyline`) en las actividades con GPS.
- Retroalimentación por voz (Text-to-Speech) y vibración durante el entrenamiento.
- Acceso directo a apps de música (Spotify, YouTube Music, Apple Music, Amazon Music, Deezer): abre la app instalada o, si no está, su versión web.

**Historial**
- Vista unificada de entrenamientos pasados de todas las modalidades, con tarjetas expandibles (`HistorialActivity`).

**Recordatorios**
- Notificaciones programadas (WorkManager + `BroadcastReceiver`) para recordar al usuario entrenar.

**Configuración**
- Edición de datos personales, confirmación por contraseña para acciones sensibles, gestión del recordatorio.

## Tecnologías utilizadas
- **Lenguaje:** Kotlin 2.2.0
- **UI:** Vistas XML tradicionales (Android Views) + Material Components — no usa Jetpack Compose
- **Arquitectura:** MVVM ligero — `ViewModel` → `Repository` → `DAO` (Room), replicado por cada tipo de actividad
- **Persistencia:** Room 2.7.0 (SQLite local), base de datos `activa_database`
- **Mapas y ubicación:** Google Maps SDK 18.2.0, Fused Location Provider (`play-services-location` 21.2.0)
- **Tareas en segundo plano:** WorkManager 2.9.0 (recordatorios)
- **Otros:** Lifecycle ViewModel KTX, Android TextToSpeech API, `FileProvider` para fotos de perfil
- **Build:** Gradle 8.11.1 (Kotlin DSL) + Android Gradle Plugin 8.9.1, `kapt` para el compilador de Room

## Arquitectura y estructura del proyecto
```
app/src/main/java/com/example/activaescom/
├── MainActivity.kt              # Panel principal (dashboard)
├── LoginActivity.kt             # Pantalla inicial / autenticación
├── RegistroActivity.kt          # Alta de nuevos usuarios
├── PerfilActivity.kt / EditarPerfilActivity.kt
├── ConfigActivity.kt            # Ajustes y recordatorios
├── HistorialActivity.kt         # Historial unificado
├── NuevoEntrenamientoActivity.kt
├── CarreraActivity.kt / CaminataActivity.kt / BicicletaActivity.kt   # con GPS
├── NatacionActivity.kt / YogaActivity.kt / FuerzaActivity.kt
├── ContactoActivity.kt / PrivacidadActivity.kt / TerminosActivity.kt
├── NavegacionHelper.kt          # Menú lateral / barra inferior compartidos
├── UserPreferences.kt           # Sesión vía SharedPreferences
├── RecordatorioWorker.kt / NotificationReceiver.kt
│
├── adapter/                     # Adapters de RecyclerView (historial)
├── database/
│   ├── AppDatabase.kt           # Room, versión 11
│   ├── dao/                     # 11 interfaces DAO
│   ├── entities/                # 16 entidades
│   └── relations/                # Consultas @Relation (Configuración + Detalle)
├── model/                       # Modelos de UI (no persistidos)
├── repository/                  # 10 repositorios
└── viewmodel/                   # 10 ViewModels
```

Cada actividad física sigue el mismo patrón de capas: una entidad de **Configuración** (metas/preferencias antes de iniciar), una entidad de **Detalle** (datos registrados de la sesión) y, cuando aplica, una relación `@Relation` que las combina.

## Modelo de datos
La base de datos (`AppDatabase`, versión 11) contiene 16 entidades, entre ellas:

| Entidad | Propósito |
|---|---|
| `UsuarioEntity` | Cuenta del usuario (correo, boleta, contraseña, datos de perfil) |
| `ActividadEntity` / `EntrenamientoEntity` | Registro genérico de actividad/entrenamiento |
| `UbicacionEntity` | Puntos de ubicación de la ruta GPS |
| `Carrera` / `Caminata` / `Bicicleta` `ConfiguracionEntity` y `DetalleEntity` | Configuración y detalle registrado de cada actividad con GPS |
| `Natacion` / `Yoga` / `Fuerza` `ConfiguracionEntity` y `DetalleEntity` | Configuración y detalle registrado de cada actividad sin GPS |

## Requisitos previos
- [Android Studio](https://developer.android.com/studio) (versión reciente, compatible con AGP 8.9.1 y `compileSdk` 36)
- JDK 21 (el que incluye Android Studio es suficiente; el bytecode de la app se compila con compatibilidad Java 11)
- Android SDK con la Platform 36 y las Build-Tools correspondientes instaladas
- Un dispositivo o emulador con Android 8.1 (API 27) o superior
- Una API key propia de **Google Maps SDK for Android** (ver siguiente sección)

## Instalación y configuración
1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/CortesVizcainoLuisRamon/Proyecto_Moviles.git
   cd Proyecto_Moviles
   ```
2. **Abrir en Android Studio** y esperar la sincronización de Gradle (`File > Sync Project with Gradle Files`).
3. **Configurar tu propia API key de Google Maps.** Actualmente está escrita directamente en `app/src/main/AndroidManifest.xml`; se recomienda moverla fuera del control de versiones:
   - Agrega en `local.properties` (no se versiona): `MAPS_API_KEY=tu_api_key_aqui`
   - Expón la variable en `app/build.gradle.kts` mediante `manifestPlaceholders["MAPS_API_KEY"] = ...`
   - Referéncialo en el manifest como `android:value="${MAPS_API_KEY}"`
   - Genera/gestiona tu key en [Google Cloud Console](https://console.cloud.google.com/) y restríngela por SHA-1 + nombre de paquete (`com.example.activaescom`).
4. **Ejecutar** la app (`Run ▶`) en un emulador o dispositivo físico con API 27+, o compilar desde línea de comandos:
   ```bash
   ./gradlew assembleDebug
   ```

## Notas de seguridad
Proyecto con fines académicos. Antes de usarlo en producción o de hacer público el repositorio, ten en cuenta:

- ⚠️ **API key de Google Maps expuesta.** Está en texto plano en `AndroidManifest.xml` y quedará en el historial de git. Se recomienda rotarla y restringirla en Google Cloud Console, y moverla fuera del control de versiones (ver sección anterior).
- ⚠️ **Contraseñas en texto plano.** `UsuarioDao` compara `password` directamente contra la base de datos local, sin hash. Es aceptable para un prototipo local, pero no debería usarse así si la app llegara a manejar credenciales reales — se recomienda aplicar hashing (p. ej. BCrypt) antes de cualquier despliegue real.
- El login/registro es completamente local (Room); no existe un backend que valide credenciales.

## Permisos utilizados
| Permiso | Uso |
|---|---|
| `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` | Mapa y seguimiento GPS de rutas |
| `POST_NOTIFICATIONS` | Notificaciones de recordatorio de entrenamiento |
| `VIBRATE` | Retroalimentación háptica durante el entrenamiento |

## Equipo
- **Ramón Cortés** — [@CortesVizcainoLuisRamon](https://github.com/CortesVizcainoLuisRamon)
- **David Gil** — [@DavidGilJ](https://github.com/DavidGilJ)

Proyecto académico de la comunidad ESCOM (Escuela Superior de Cómputo), IPN.

## Licencia
Este repositorio no incluye actualmente un archivo de licencia. Si planeas compartirlo públicamente, considera añadir una —por ejemplo [MIT](https://choosealicense.com/licenses/mit/) o [Apache 2.0](https://choosealicense.com/licenses/apache-2.0/)— para dejar claro cómo puede usarse el código.
