# 🧠 NEUROZEN MOBILE APP - ESPECIFICACIÓN TÉCNICA PARA FRONTEND

## 📋 TABLA DE CONTENIDOS
1. [Información General del Backend](#información-general)
2. [Configuración Base de Conexión](#configuración-base)
3. [Autenticación y Seguridad](#autenticación-y-seguridad)
4. [Estructura de Respuestas](#estructura-de-respuestas)
5. [Especificación de Entidades](#especificación-de-entidades)
6. [Endpoints de Ejemplo](#endpoints-de-ejemplo)
7. [Manejo de Errores](#manejo-de-errores)
8. [Convenciones de Nombres](#convenciones-de-nombres)

---

## ℹ️ INFORMACIÓN GENERAL

### Stack Tecnológico del Backend
- **Framework:** Spring Boot 4.0.6
- **Java:** Versión 25
- **Base de Datos:** MySQL (localhost:3306)
- **ORM:** Spring Data JPA + Hibernate
- **Autenticación:** JWT Bearer Token
- **API Documentation:** OpenAPI 3.0 (Swagger UI)
- **Arquitectura:** Hexagonal + Domain-Driven Design (DDD)

### Datos de Conexión
```
Base URL: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui.html
API Docs: http://localhost:8080/v3/api-docs
Base de Datos: neurozen_mobile_db
```

---

## 🔌 CONFIGURACIÓN BASE

### Headers Requeridos
Todos los endpoints (excepto login/registro) requieren autenticación:

```http
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

### Estructura de Rutas
Las rutas siguen el patrón REST estándar:
```
/api/v1/{recurso}              → GET (listar)
/api/v1/{recurso}/{id}         → GET (detalle)
/api/v1/{recurso}              → POST (crear)
/api/v1/{recurso}/{id}         → PUT (actualizar)
/api/v1/{recurso}/{id}         → DELETE (eliminar)
```

### Convención de Nombres en Rutas
- Los nombres de entidades en rutas están en **plural y snake_case**
- La base de datos usa **snake_case pluralizado**
- Las clases Java usan **PascalCase**

Ejemplo:
| Java Class | Ruta API | Tabla BD |
|-----------|----------|---------|
| `User` | `/api/v1/users` | `users` |
| `HealthMetric` | `/api/v1/health_metrics` | `health_metrics` |
| `MeditationSession` | `/api/v1/meditation_sessions` | `meditation_sessions` |

---

## 🔐 AUTENTICACIÓN Y SEGURIDAD

### Sistema de Autenticación
El backend utiliza **JWT (JSON Web Tokens)** con Bearer Token.

### Flujo de Autenticación
```
1. Usuario proporciona credenciales (email + password)
2. Backend valida y retorna JWT token
3. Cliente almacena el token (localStorage/sessionStorage)
4. Cliente envía token en cada request en header Authorization
5. Backend valida token antes de procesar request
```

### Endpoints de Autenticación (A implementar)
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "password123"
}

Response 200 OK:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "email": "usuario@example.com",
    "name": "Nombre Usuario"
  }
}
```

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "nuevo@example.com",
  "password": "password123",
  "name": "Nuevo Usuario"
}

Response 201 Created:
{
  "id": 2,
  "email": "nuevo@example.com",
  "name": "Nuevo Usuario",
  "createdAt": "2026-05-14T10:30:00Z",
  "updatedAt": "2026-05-14T10:30:00Z"
}
```

---

## 📦 ESTRUCTURA DE RESPUESTAS

### Respuesta Exitosa (200, 201)
```json
{
  "data": {
    "id": 1,
    "name": "Ejemplo",
    "createdAt": "2026-05-14T10:30:00Z",
    "updatedAt": "2026-05-14T10:30:00Z"
  },
  "message": "Operación realizada exitosamente",
  "timestamp": "2026-05-14T10:30:00Z"
}
```

### Respuesta de Lista (200)
```json
{
  "data": [
    {
      "id": 1,
      "name": "Elemento 1",
      "createdAt": "2026-05-14T10:30:00Z",
      "updatedAt": "2026-05-14T10:30:00Z"
    },
    {
      "id": 2,
      "name": "Elemento 2",
      "createdAt": "2026-05-14T10:31:00Z",
      "updatedAt": "2026-05-14T10:31:00Z"
    }
  ],
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

### Respuesta de Error
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Los datos proporcionados no son válidos",
    "details": {
      "email": "Email debe ser válido",
      "password": "Password mínimo 8 caracteres"
    }
  },
  "timestamp": "2026-05-14T10:30:00Z"
}
```

### Códigos HTTP Esperados
| Código | Significado | Uso |
|--------|------------|-----|
| 200 | OK | GET exitoso, PUT/DELETE exitoso |
| 201 | Created | POST exitoso (recurso creado) |
| 204 | No Content | DELETE exitoso (sin body) |
| 400 | Bad Request | Datos inválidos en request |
| 401 | Unauthorized | Token no válido o expirado |
| 403 | Forbidden | Usuario sin permisos |
| 404 | Not Found | Recurso no existe |
| 409 | Conflict | Violación de restricción única |
| 500 | Server Error | Error interno del servidor |

---

## 🗂️ ESPECIFICACIÓN DE ENTIDADES

### Propiedades Automáticas (Todas las Entidades)
Toda entidad en la base de datos tendrá estas propiedades automáticas:

```json
{
  "id": 1,                                    // Generado automáticamente
  "createdAt": "2026-05-14T10:30:00Z",       // Fecha creación (automática, no editable)
  "updatedAt": "2026-05-14T10:31:45Z"        // Fecha última actualización (automática)
}
```

**Nota:** `createdAt` y `updatedAt` son administradas completamente por el backend. El frontend NO debe enviarlas en requests.

### Convención de Auditoría
- `createdAt`: Se establece cuando se crea el registro, **nunca cambia**
- `updatedAt`: Se actualiza automáticamente cada vez que se modifica cualquier campo
- Formato: ISO 8601 UTC (ej: `2026-05-14T10:30:00Z`)

---

## 🔌 ENDPOINTS DE EJEMPLO

### Ejemplo: Gestión de Usuarios

#### Listar Todos los Usuarios
```http
GET /api/v1/users
Authorization: Bearer <TOKEN>

Response 200 OK:
{
  "data": [
    {
      "id": 1,
      "email": "usuario1@example.com",
      "name": "Usuario 1",
      "status": "active",
      "createdAt": "2026-05-10T08:00:00Z",
      "updatedAt": "2026-05-14T10:30:00Z"
    },
    {
      "id": 2,
      "email": "usuario2@example.com",
      "name": "Usuario 2",
      "status": "active",
      "createdAt": "2026-05-12T09:15:00Z",
      "updatedAt": "2026-05-14T10:31:00Z"
    }
  ],
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

#### Obtener Usuario por ID
```http
GET /api/v1/users/1
Authorization: Bearer <TOKEN>

Response 200 OK:
{
  "data": {
    "id": 1,
    "email": "usuario1@example.com",
    "name": "Usuario 1",
    "status": "active",
    "createdAt": "2026-05-10T08:00:00Z",
    "updatedAt": "2026-05-14T10:30:00Z"
  }
}
```

#### Crear Usuario
```http
POST /api/v1/users
Authorization: Bearer <TOKEN>
Content-Type: application/json

{
  "email": "nuevo@example.com",
  "name": "Nuevo Usuario",
  "password": "securepass123"
}

Response 201 Created:
{
  "data": {
    "id": 3,
    "email": "nuevo@example.com",
    "name": "Nuevo Usuario",
    "status": "active",
    "createdAt": "2026-05-14T10:32:00Z",
    "updatedAt": "2026-05-14T10:32:00Z"
  }
}
```

#### Actualizar Usuario
```http
PUT /api/v1/users/1
Authorization: Bearer <TOKEN>
Content-Type: application/json

{
  "email": "actualizado@example.com",
  "name": "Nombre Actualizado"
}

Response 200 OK:
{
  "data": {
    "id": 1,
    "email": "actualizado@example.com",
    "name": "Nombre Actualizado",
    "status": "active",
    "createdAt": "2026-05-10T08:00:00Z",
    "updatedAt": "2026-05-14T10:35:00Z"
  }
}
```

#### Eliminar Usuario
```http
DELETE /api/v1/users/1
Authorization: Bearer <TOKEN>

Response 204 No Content
```

---

### Ejemplo: Métricas de Salud

#### Crear Métrica de Salud
```http
POST /api/v1/health_metrics
Authorization: Bearer <TOKEN>
Content-Type: application/json

{
  "userId": 1,
  "heartRate": 72,
  "bloodPressure": "120/80",
  "sleepHours": 8,
  "stressLevel": 3,
  "exerciseMinutes": 30,
  "notes": "Sesión de meditación completada"
}

Response 201 Created:
{
  "data": {
    "id": 10,
    "userId": 1,
    "heartRate": 72,
    "bloodPressure": "120/80",
    "sleepHours": 8,
    "stressLevel": 3,
    "exerciseMinutes": 30,
    "notes": "Sesión de meditación completada",
    "createdAt": "2026-05-14T10:30:00Z",
    "updatedAt": "2026-05-14T10:30:00Z"
  }
}
```

#### Obtener Métricas del Usuario
```http
GET /api/v1/users/1/health_metrics
Authorization: Bearer <TOKEN>

Response 200 OK:
{
  "data": [
    {
      "id": 10,
      "userId": 1,
      "heartRate": 72,
      "bloodPressure": "120/80",
      "sleepHours": 8,
      "stressLevel": 3,
      "exerciseMinutes": 30,
      "createdAt": "2026-05-14T10:30:00Z",
      "updatedAt": "2026-05-14T10:30:00Z"
    }
  ],
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

### Ejemplo: Sesiones de Meditación

#### Crear Sesión de Meditación
```http
POST /api/v1/meditation_sessions
Authorization: Bearer <TOKEN>
Content-Type: application/json

{
  "userId": 1,
  "title": "Meditación Matutina",
  "duration": 20,
  "type": "guided",
  "difficulty": "beginner",
  "description": "Sesión guiada para relajación matutina"
}

Response 201 Created:
{
  "data": {
    "id": 5,
    "userId": 1,
    "title": "Meditación Matutina",
    "duration": 20,
    "type": "guided",
    "difficulty": "beginner",
    "description": "Sesión guiada para relajación matutina",
    "status": "completed",
    "startedAt": "2026-05-14T07:00:00Z",
    "endedAt": "2026-05-14T07:20:00Z",
    "createdAt": "2026-05-14T07:00:00Z",
    "updatedAt": "2026-05-14T07:20:00Z"
  }
}
```

#### Listar Sesiones del Usuario
```http
GET /api/v1/users/1/meditation_sessions
Authorization: Bearer <TOKEN>

Response 200 OK:
{
  "data": [
    {
      "id": 5,
      "userId": 1,
      "title": "Meditación Matutina",
      "duration": 20,
      "type": "guided",
      "difficulty": "beginner",
      "status": "completed",
      "startedAt": "2026-05-14T07:00:00Z",
      "endedAt": "2026-05-14T07:20:00Z",
      "createdAt": "2026-05-14T07:00:00Z",
      "updatedAt": "2026-05-14T07:20:00Z"
    }
  ]
}
```

---

## ❌ MANEJO DE ERRORES

### Error: No Autenticado (401)
```json
{
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Token no válido o expirado",
    "details": {
      "reason": "JWT token expired"
    }
  },
  "timestamp": "2026-05-14T10:30:00Z"
}
```

### Error: Validación Fallida (400)
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Los datos enviados no son válidos",
    "details": {
      "email": "Formato de email inválido",
      "password": "La contraseña debe tener al menos 8 caracteres"
    }
  },
  "timestamp": "2026-05-14T10:30:00Z"
}
```

### Error: Recurso No Encontrado (404)
```json
{
  "error": {
    "code": "NOT_FOUND",
    "message": "El recurso solicitado no existe",
    "details": {
      "resourceType": "User",
      "resourceId": 999
    }
  },
  "timestamp": "2026-05-14T10:30:00Z"
}
```

### Error: Conflicto (409)
```json
{
  "error": {
    "code": "CONFLICT",
    "message": "El recurso ya existe",
    "details": {
      "field": "email",
      "value": "usuario@example.com"
    }
  },
  "timestamp": "2026-05-14T10:30:00Z"
}
```

### Error: Interno del Servidor (500)
```json
{
  "error": {
    "code": "INTERNAL_SERVER_ERROR",
    "message": "Ocurrió un error interno del servidor",
    "details": {
      "requestId": "abc-123-def-456"
    }
  },
  "timestamp": "2026-05-14T10:30:00Z"
}
```

---

## 📏 CONVENCIONES DE NOMBRES

### En Rutas API
- **Plural:** `/api/v1/users` NO `/api/v1/user`
- **Snake_case:** `/api/v1/health_metrics` NO `/api/v1/healthMetrics`
- **Verbos HTTP para acciones:**
  - GET para obtener
  - POST para crear
  - PUT para actualizar completo
  - PATCH para actualizar parcial
  - DELETE para eliminar

### En Respuestas JSON
- **camelCase:** `userId`, `firstName`, `healthMetrics` NO `user_id`, `first_name`
- **ISO 8601 para fechas:** `2026-05-14T10:30:00Z`
- **Booleanos:** `isActive`, `isDeleted`, `isVerified`
- **Arrays:** Nombre en plural: `users`, `items`, `metrics`

### En Base de Datos
- **snake_case:** `user_id`, `first_name`, `health_metrics`
- **Tablas pluralizadas:** `users`, `health_metrics`, `meditation_sessions`
- **Índices:** `idx_users_email`, `idx_health_metrics_user_id`
- **Foreign Keys:** `fk_health_metrics_user_id`

---

## 🔄 PAGINACIÓN

### Parámetros de Paginación
```http
GET /api/v1/users?page=0&size=20&sort=createdAt,desc

Parámetros:
- page: Número de página (0-indexed)
- size: Cantidad de elementos por página (default: 20)
- sort: Campo y dirección (asc/desc) - separados por coma
```

### Respuesta Paginada
```json
{
  "data": [...],
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8,
    "isFirst": true,
    "isLast": false,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

---

## 🎯 PRÓXIMOS PASOS PARA EL EQUIPO DE BACKEND

1. **Implementar Controladores REST** (`@RestController`)
   - Crear `UserController` para gestión de usuarios
   - Crear `HealthMetricsController` para métricas
   - Crear `MeditationSessionController` para sesiones
   - Crear `AuthController` para autenticación

2. **Implementar Servicios** (`@Service`)
   - `UserService` con lógica de negocio de usuarios
   - `HealthMetricsService` con lógica de métricas
   - `MeditationSessionService` con lógica de sesiones
   - `AuthService` con lógica de autenticación

3. **Crear Entidades JPA** (`@Entity`)
   - Extender de `AuditableAbstractAggregateRoot`
   - Definir relaciones (OneToMany, ManyToOne, etc.)
   - Agregar validaciones

4. **Implementar Repositorios** (`JpaRepository`)
   - `UserRepository`
   - `HealthMetricsRepository`
   - `MeditationSessionRepository`

5. **Configurar Seguridad**
   - Implementar JWT TokenProvider
   - Configurar SecurityConfig
   - Implementar autenticación

6. **DTOs de Request/Response**
   - Crear DTOs para cada entidad
   - Mapper de Entidad a DTO y viceversa

---

## 📝 NOTAS IMPORTANTES

- **Todas las fechas:** Vienen en ISO 8601 UTC desde el backend
- **Tokens JWT:** Incluir siempre en header `Authorization: Bearer <token>`
- **CORS:** El backend está configurado para aceptar requests desde localhost:3000 (típico para React/Angular)
- **Documentación Swagger:** Disponible en http://localhost:8080/swagger-ui.html
- **Base de Datos:** Se crea automáticamente si no existe (`createDatabaseIfNotExist=true`)
- **Auditoría:** El backend registra automáticamente quién y cuándo creó/modificó cada registro
- **Soft Deletes:** Considerar implementar soft deletes (marcar como deleted en lugar de eliminar)

---

**Última actualización:** 14 de Mayo de 2026
**Versión Backend:** 1.0.0
**Estado:** En desarrollo - Especificación 1.0
