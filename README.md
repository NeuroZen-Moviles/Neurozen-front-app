# Neurozen Front App

Frontend Android en Jetpack Compose para **Neurozen**, una app enfocada en aliviar el estrés con una interfaz suave, verde y natural.

## Que incluye

- Pantalla de bienvenida con estilo calmante
- Login conectado al endpoint `POST /auth/login`
- Dashboard con:
  - metricas de bienestar
  - momentos rapidos
  - sesiones recomendadas
  - respiracion guiada
  - perfil de usuario
- Sincronizacion con backend:
  - `GET /users/{id}`
  - `GET /users/{id}/health_metrics`
  - `GET /users/{id}/meditation_sessions`
- Tema personalizado en tonos verdes claros
- Navegación simple entre onboarding, login y dashboard
- Placeholders de imagen en onboarding, login, sesiones y perfil para que los reemplaces despues

## Requisitos

- Android Studio instalado
- JDK disponible en el sistema
- `JAVA_HOME` configurado, o Gradle no podrá compilar

## Configuracion de API

Por defecto la app usa:

- `http://10.0.2.2:8080/api/v1/` (emulador Android)

Si usas dispositivo fisico, cambia la URL en `app/src/main/java/com/example/neurozen_front/neurozen/data/network/ApiConfig.kt`.

## Como ejecutar

```powershell
cd "D:\joao\universidad UPC\SEXTO CICLO\APLICACIONES MOVILES\Neurozen-movil\Neurozen-front-app"
.\gradlew.bat :app:assembleDebug
```

## Nota

En mi verificación, el proyecto quedó sin errores de Kotlin en los archivos que modifiqué. La compilación completa no pudo ejecutarse porque el entorno actual no tiene `JAVA_HOME` configurado.

