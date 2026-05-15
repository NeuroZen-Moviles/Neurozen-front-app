# 🚀 QUICK REFERENCE - NEUROZEN API

## 📍 URLs Base
```
Base:     http://localhost:8080/api/v1
Swagger:  http://localhost:8080/swagger-ui.html
```

## 🔑 Autenticación
```http
POST /auth/login
{
  "email": "user@example.com",
  "password": "password123"
}

Response: { token, refreshToken, expiresIn, user }
```

Usar en headers:
```
Authorization: Bearer <TOKEN>
```

---

## 📊 RECURSOS Y ENDPOINTS

### 👥 USUARIOS `/users`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/users` | Listar todos (paginado) |
| GET | `/users/{id}` | Obtener por ID |
| POST | `/users` | Crear nuevo |
| PUT | `/users/{id}` | Actualizar |
| DELETE | `/users/{id}` | Eliminar |

**Ejemplo GET:**
```http
GET /users?page=0&size=20
Authorization: Bearer TOKEN
```

**Ejemplo POST:**
```http
POST /users
Authorization: Bearer TOKEN
Content-Type: application/json

{
  "email": "new@example.com",
  "name": "Nuevo Usuario",
  "password": "securepass123"
}
```

---

### ❤️ MÉTRICAS DE SALUD `/health_metrics`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/health_metrics` | Crear métrica |
| GET | `/users/{userId}/health_metrics` | Obtener métricas del usuario |
| GET | `/health_metrics/{id}` | Obtener métrica por ID |
| PUT | `/health_metrics/{id}` | Actualizar |
| DELETE | `/health_metrics/{id}` | Eliminar |

**Ejemplo POST:**
```http
POST /health_metrics
Authorization: Bearer TOKEN
Content-Type: application/json

{
  "userId": 1,
  "heartRate": 72,
  "bloodPressure": "120/80",
  "sleepHours": 8,
  "stressLevel": 3,
  "exerciseMinutes": 30,
  "notes": "Buena sesión"
}
```

---

### 🧘 SESIONES DE MEDITACIÓN `/meditation_sessions`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/meditation_sessions` | Crear sesión |
| GET | `/users/{userId}/meditation_sessions` | Obtener sesiones |
| GET | `/meditation_sessions/{id}` | Obtener por ID |
| PUT | `/meditation_sessions/{id}` | Actualizar |
| DELETE | `/meditation_sessions/{id}` | Eliminar |

**Ejemplo POST:**
```http
POST /meditation_sessions
Authorization: Bearer TOKEN
Content-Type: application/json

{
  "userId": 1,
  "title": "Meditación Matutina",
  "duration": 20,
  "type": "guided",
  "difficulty": "beginner",
  "description": "Sesión guiada"
}
```

---

## 📦 ESTRUCTURA DE RESPUESTAS

### ✅ Éxito (200, 201)
```json
{
  "data": { /* recurso */ },
  "message": "Operación exitosa",
  "timestamp": "2026-05-14T10:30:00Z"
}
```

### ❌ Error (400, 401, 404, etc)
```json
{
  "error": {
    "code": "ERROR_CODE",
    "message": "Descripción del error",
    "details": {}
  },
  "timestamp": "2026-05-14T10:30:00Z"
}
```

### 📄 Lista (200)
```json
{
  "data": [ /* array */ ],
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

---

## 🔄 CÓDIGOS HTTP

| Código | Significado | Acción |
|--------|------------|--------|
| 200 | OK | Operación exitosa |
| 201 | Created | Recurso creado |
| 204 | No Content | Eliminado (sin respuesta) |
| 400 | Bad Request | Datos inválidos |
| 401 | Unauthorized | Token falta o expiró |
| 403 | Forbidden | Sin permisos |
| 404 | Not Found | No existe |
| 409 | Conflict | Ya existe / violación |
| 500 | Server Error | Error interno |

---

## ⏱️ PAGINACIÓN

```http
GET /users?page=0&size=20&sort=createdAt,desc

Parámetros:
- page: número página (0-indexed)
- size: elementos por página
- sort: campo,dirección (asc/desc)
```

Respuesta:
```json
{
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

## 🕐 FECHAS

**Formato:** ISO 8601 UTC
```
2026-05-14T10:30:00Z
2026-05-14T10:30:45.123Z
```

**Campos automáticos (NO enviar):**
- `createdAt` - Fecha creación (nunca cambia)
- `updatedAt` - Fecha última modificación (automática)
- `id` - Generado automáticamente

---

## 🎯 FLUJOS COMUNES

### 1. Login y Guardar Token
```javascript
// 1. POST a /auth/login con credenciales
const res = await fetch('/api/v1/auth/login', {
  method: 'POST',
  body: JSON.stringify({ email, password })
});

// 2. Guardar token
const { token } = await res.json();
localStorage.setItem('token', token);

// 3. Usar en requests
fetch('/api/v1/users', {
  headers: { 'Authorization': `Bearer ${token}` }
});
```

### 2. Listar Recursos con Paginación
```javascript
const page = 0;
const size = 20;

const res = await fetch(`/api/v1/users?page=${page}&size=${size}`, {
  headers: { 'Authorization': `Bearer ${token}` }
});

const { data, pagination } = await res.json();
// data = array de usuarios
// pagination = { page, size, totalElements, totalPages, ... }
```

### 3. Crear Recurso
```javascript
const res = await fetch('/api/v1/users', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    name: 'John Doe',
    email: 'john@example.com'
  })
});

const { data } = await res.json(); // Recurso creado con ID y timestamps
```

### 4. Actualizar Recurso
```javascript
const res = await fetch(`/api/v1/users/1`, {
  method: 'PUT',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    name: 'Jane Doe'
    // NO incluir createdAt, updatedAt, ni id
  })
});

const { data } = await res.json();
```

### 5. Eliminar Recurso
```javascript
const res = await fetch(`/api/v1/users/1`, {
  method: 'DELETE',
  headers: { 'Authorization': `Bearer ${token}` }
});

// Status 204 = eliminado (sin body)
// Status 200 = eliminado (con respuesta)
```

### 6. Manejo de Errores
```javascript
const res = await fetch('/api/v1/users', {
  headers: { 'Authorization': `Bearer ${token}` }
});

if (!res.ok) {
  const error = await res.json();
  console.error(error.error.message); // Mensaje de error
  console.error(error.error.details); // Detalles específicos
}
```

---

## 📋 PROPIEDADES AUTOMÁTICAS (Todas las Entidades)

```json
{
  "id": 1,                                // Auto-generado
  "createdAt": "2026-05-14T10:30:00Z",   // Auto-establecido (NO editable)
  "updatedAt": "2026-05-14T10:31:45Z"    // Auto-actualizado
}
```

---

## 🔗 HEADERS REQUERIDOS

```http
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

---

## ✅ CHECKLIST ANTES DE DESARROLLAR

- [ ] Backend corriendo en http://localhost:8080
- [ ] BD MySQL creada en localhost:3306
- [ ] Swagger UI accesible en http://localhost:8080/swagger-ui.html
- [ ] Instalé axios o uso Fetch API
- [ ] Configuré URL base de API
- [ ] Implementé almacenamiento de token
- [ ] Agregué header Authorization en requests
- [ ] Manejo de errores 401 (token expirado)
- [ ] Probé login y creación de usuario
- [ ] Probé endpoints en Swagger UI primero

---

## 🐛 DEBUGGING

### Ver token en Swagger UI
```
1. Haz login en algún endpoint de /auth
2. Copia el token de la respuesta
3. Click "Authorize" (arriba a la derecha)
4. Pega: Bearer <token>
5. Prueba endpoints
```

### Verificar token JWT (sin validar)
```javascript
const token = 'eyJhbGciOiJIUzI1NiIs...';
const part2 = token.split('.')[1];
const decoded = JSON.parse(atob(part2));
console.log(decoded); // { sub, iat, exp, ... }
```

### Resets útiles
```javascript
// Borrar token
localStorage.removeItem('token');

// Borrar todo
localStorage.clear();

// Ver BD (MySQL CLI)
mysql -u root -p
use neurozen_mobile_db;
SHOW TABLES;
SELECT * FROM users;
```

---

## 📞 CONTACTO

Si encuentras problemas:
1. Revisa el Swagger UI para probar endpoints
2. Verifica los logs del backend (consola donde corre Spring)
3. Comprueba la respuesta exacta con Network tab del navegador
4. Revisa que el token no esté expirado
5. Verifica que enviaste todos los campos requeridos

---

**Última actualización:** 14 de Mayo de 2026
**Versión:** 1.0.0 BETA
