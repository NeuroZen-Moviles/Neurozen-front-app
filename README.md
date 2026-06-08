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


