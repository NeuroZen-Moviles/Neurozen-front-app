# 🔗 GUÍA PRÁCTICA DE CONSUMO DE API - NEUROZEN FRONTEND

## 📋 TABLA DE CONTENIDOS
1. [Configuración Inicial](#configuración-inicial)
2. [Ejemplos con Fetch API](#ejemplos-con-fetch-api)
3. [Ejemplos con Axios](#ejemplos-con-axios)
4. [Manejo de Tokens JWT](#manejo-de-tokens)
5. [Interceptores y Middleware](#interceptores-y-middleware)
6. [Gestión de Errores](#gestión-de-errores)
7. [Estructura de Carpetas Recomendada](#estructura-de-carpetas)

---

## ⚙️ CONFIGURACIÓN INICIAL

### URL Base del Backend
```javascript
// Development
const API_BASE_URL = 'http://localhost:8080/api/v1';

// Production
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1';
```

### Variables de Entorno (.env)
```env
REACT_APP_API_URL=http://localhost:8080/api/v1
REACT_APP_API_TIMEOUT=10000
```

---

## 📡 EJEMPLOS CON FETCH API

### 1. Autenticación - Login

```javascript
// services/authService.js
export const login = async (email, password) => {
  try {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email,
        password
      })
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.error?.message || 'Login failed');
    }

    const data = await response.json();
    
    // Guardar token
    localStorage.setItem('authToken', data.data.token);
    localStorage.setItem('refreshToken', data.data.refreshToken);
    localStorage.setItem('user', JSON.stringify(data.data.user));
    
    return data.data;
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
};
```

### 2. Obtener Todos los Usuarios

```javascript
export const getUsers = async (page = 0, size = 20) => {
  try {
    const token = localStorage.getItem('authToken');
    
    const response = await fetch(
      `${API_BASE_URL}/users?page=${page}&size=${size}`,
      {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      }
    );

    if (!response.ok) {
      if (response.status === 401) {
        // Token expirado, hacer refresh
        await refreshToken();
        return getUsers(page, size); // Reintentar
      }
      throw new Error('Failed to fetch users');
    }

    return await response.json();
  } catch (error) {
    console.error('Error fetching users:', error);
    throw error;
  }
};
```

### 3. Obtener Usuario por ID

```javascript
export const getUserById = async (userId) => {
  const token = localStorage.getItem('authToken');
  
  const response = await fetch(`${API_BASE_URL}/users/${userId}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });

  if (!response.ok) {
    throw new Error('Failed to fetch user');
  }

  return await response.json();
};
```

### 4. Crear Usuario

```javascript
export const createUser = async (userData) => {
  const token = localStorage.getItem('authToken');
  
  const response = await fetch(`${API_BASE_URL}/users`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(userData)
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.error?.message || 'Failed to create user');
  }

  return await response.json();
};
```

### 5. Actualizar Usuario

```javascript
export const updateUser = async (userId, userData) => {
  const token = localStorage.getItem('authToken');
  
  const response = await fetch(`${API_BASE_URL}/users/${userId}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(userData)
  });

  if (!response.ok) {
    throw new Error('Failed to update user');
  }

  return await response.json();
};
```

### 6. Eliminar Usuario

```javascript
export const deleteUser = async (userId) => {
  const token = localStorage.getItem('authToken');
  
  const response = await fetch(`${API_BASE_URL}/users/${userId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  if (!response.ok) {
    throw new Error('Failed to delete user');
  }

  // 204 No Content no tiene body
  if (response.status === 204) {
    return { success: true };
  }

  return await response.json();
};
```

### 7. Crear Métrica de Salud

```javascript
export const createHealthMetric = async (metricData) => {
  const token = localStorage.getItem('authToken');
  
  const response = await fetch(`${API_BASE_URL}/health_metrics`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(metricData)
  });

  if (!response.ok) {
    throw new Error('Failed to create health metric');
  }

  return await response.json();
};
```

### 8. Obtener Métricas de un Usuario

```javascript
export const getUserHealthMetrics = async (userId, page = 0) => {
  const token = localStorage.getItem('authToken');
  
  const response = await fetch(
    `${API_BASE_URL}/users/${userId}/health_metrics?page=${page}`,
    {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    }
  );

  if (!response.ok) {
    throw new Error('Failed to fetch health metrics');
  }

  return await response.json();
};
```

---

## 📦 EJEMPLOS CON AXIOS

### Configuración Base

```javascript
// services/apiClient.js
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor para agregar token automáticamente
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor para manejar respuestas
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expirado - limpiar y redirigir a login
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

### 1. Login con Axios

```javascript
// services/authService.js
import apiClient from './apiClient';

export const loginWithAxios = async (email, password) => {
  try {
    const response = await apiClient.post('/auth/login', {
      email,
      password
    });

    const { token, refreshToken, user } = response.data.data;
    
    localStorage.setItem('authToken', token);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify(user));
    
    return { token, user };
  } catch (error) {
    console.error('Login error:', error.response?.data);
    throw error;
  }
};
```

### 2. Operaciones CRUD Simplificadas

```javascript
// services/userService.js
import apiClient from './apiClient';

export const userService = {
  // Listar usuarios
  list: (page = 0, size = 20) => 
    apiClient.get('/users', { params: { page, size } }),

  // Obtener por ID
  getById: (id) => 
    apiClient.get(`/users/${id}`),

  // Crear
  create: (data) => 
    apiClient.post('/users', data),

  // Actualizar
  update: (id, data) => 
    apiClient.put(`/users/${id}`, data),

  // Eliminar
  delete: (id) => 
    apiClient.delete(`/users/${id}`),

  // Métricas de salud
  getHealthMetrics: (userId, page = 0) =>
    apiClient.get(`/users/${userId}/health_metrics`, { params: { page } }),

  // Crear métrica
  createHealthMetric: (data) =>
    apiClient.post('/health_metrics', data)
};
```

### 3. Uso en un Componente React

```javascript
// components/UserList.jsx
import React, { useEffect, useState } from 'react';
import { userService } from '../services/userService';

function UserList() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        setLoading(true);
        const response = await userService.list(page);
        setUsers(response.data.data);
      } catch (err) {
        setError(err.message);
        console.error('Error fetching users:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, [page]);

  if (loading) return <div>Cargando...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      <h1>Usuarios</h1>
      <ul>
        {users.map((user) => (
          <li key={user.id}>{user.name} ({user.email})</li>
        ))}
      </ul>
    </div>
  );
}

export default UserList;
```

---

## 🔐 MANEJO DE TOKENS JWT

### Almacenamiento y Recuperación

```javascript
// services/tokenService.js

export const tokenService = {
  // Guardar tokens
  setTokens: (accessToken, refreshToken) => {
    localStorage.setItem('authToken', accessToken);
    if (refreshToken) {
      localStorage.setItem('refreshToken', refreshToken);
    }
  },

  // Obtener token de acceso
  getAccessToken: () => localStorage.getItem('authToken'),

  // Obtener refresh token
  getRefreshToken: () => localStorage.getItem('refreshToken'),

  // Limpiar tokens
  clearTokens: () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  },

  // Verificar si existe token
  hasToken: () => !!localStorage.getItem('authToken'),

  // Decodificar token (sin validar firma)
  decodeToken: (token) => {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64).split('').map((c) => {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join('')
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  },

  // Verificar si token está expirado
  isTokenExpired: (token) => {
    const decoded = tokenService.decodeToken(token);
    if (!decoded || !decoded.exp) return true;
    return decoded.exp * 1000 < Date.now();
  }
};
```

### Refresh de Token Automático

```javascript
// services/authService.js

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  
  isRefreshing = false;
  failedQueue = [];
};

export const refreshAccessToken = async () => {
  if (isRefreshing) {
    return new Promise((resolve, reject) => {
      failedQueue.push({ resolve, reject });
    });
  }

  isRefreshing = true;

  try {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) throw new Error('No refresh token available');

    const response = await axios.post(
      `${API_BASE_URL}/auth/refresh`,
      { refreshToken },
      { headers: { 'Content-Type': 'application/json' } }
    );

    const { token } = response.data.data;
    localStorage.setItem('authToken', token);
    
    processQueue(null, token);
    return token;
  } catch (error) {
    processQueue(error, null);
    localStorage.removeItem('authToken');
    localStorage.removeItem('refreshToken');
    window.location.href = '/login';
    throw error;
  }
};
```

---

## 🔗 INTERCEPTORES Y MIDDLEWARE

### Interceptor de Axios Completo

```javascript
// middleware/axiosInterceptor.js
import apiClient from '../services/apiClient';
import { refreshAccessToken } from '../services/authService';

export const setupInterceptors = () => {
  // Request Interceptor
  apiClient.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('authToken');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      // Logging en desarrollo
      if (process.env.NODE_ENV === 'development') {
        console.log(`API Request: ${config.method.toUpperCase()} ${config.url}`);
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  // Response Interceptor
  apiClient.interceptors.response.use(
    (response) => {
      if (process.env.NODE_ENV === 'development') {
        console.log(`API Response: ${response.status} ${response.statusText}`);
      }
      return response;
    },
    async (error) => {
      const originalRequest = error.config;

      // Si es 401 y no hemos intentado refresh
      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
          const newToken = await refreshAccessToken();
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          return apiClient(originalRequest);
        } catch (refreshError) {
          // Refresh falló, redirigir a login
          window.location.href = '/login';
          return Promise.reject(refreshError);
        }
      }

      // Logging de errores
      console.error('API Error:', {
        status: error.response?.status,
        message: error.response?.data?.error?.message,
        url: error.config?.url
      });

      return Promise.reject(error);
    }
  );
};
```

### Uso en App.js

```javascript
// App.js
import React, { useEffect } from 'react';
import { setupInterceptors } from './middleware/axiosInterceptor';

function App() {
  useEffect(() => {
    setupInterceptors();
  }, []);

  return (
    // Tu app aquí
  );
}

export default App;
```

---

## ⚠️ GESTIÓN DE ERRORES

### Hook Personalizado para Manejo de Errores

```javascript
// hooks/useApi.js
import { useState } from 'react';
import apiClient from '../services/apiClient';

export const useApi = (url, method = 'GET', config = {}) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const execute = async (body = null) => {
    try {
      setLoading(true);
      setError(null);

      let response;
      if (method === 'GET') {
        response = await apiClient.get(url, config);
      } else if (method === 'POST') {
        response = await apiClient.post(url, body, config);
      } else if (method === 'PUT') {
        response = await apiClient.put(url, body, config);
      } else if (method === 'DELETE') {
        response = await apiClient.delete(url, config);
      }

      setData(response.data.data);
      return response.data;
    } catch (err) {
      const errorMessage = err.response?.data?.error?.message || 
                          err.message || 
                          'An error occurred';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { data, loading, error, execute };
};
```

### Componente con Error Handling

```javascript
// components/UserForm.jsx
import React, { useState } from 'react';
import { useApi } from '../hooks/useApi';

function UserForm() {
  const [formData, setFormData] = useState({ name: '', email: '' });
  const { loading, error, execute } = useApi('/users', 'POST');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await execute(formData);
      alert('Usuario creado exitosamente');
      setFormData({ name: '', email: '' });
    } catch (err) {
      // El error ya está en el estado 'error'
      console.error('Error creating user:', err);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        value={formData.name}
        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
        placeholder="Nombre"
      />
      <input
        type="email"
        value={formData.email}
        onChange={(e) => setFormData({ ...formData, email: e.target.value })}
        placeholder="Email"
      />
      {error && <div className="error">{error}</div>}
      <button type="submit" disabled={loading}>
        {loading ? 'Creando...' : 'Crear'}
      </button>
    </form>
  );
}

export default UserForm;
```

---

## 📁 ESTRUCTURA DE CARPETAS RECOMENDADA

```
src/
├── services/
│   ├── apiClient.js           # Configuración de axios
│   ├── authService.js         # Servicios de autenticación
│   ├── userService.js         # Servicios de usuarios
│   ├── healthMetricsService.js
│   ├── meditationService.js
│   └── tokenService.js        # Gestión de tokens
│
├── hooks/
│   ├── useApi.js              # Hook para consumo de API
│   ├── useAuth.js             # Hook para autenticación
│   └── useUser.js             # Hook para usuarios
│
├── middleware/
│   └── axiosInterceptor.js    # Configuración de interceptores
│
├── context/
│   └── AuthContext.js         # Context de autenticación
│
├── components/
│   ├── auth/
│   │   ├── Login.jsx
│   │   └── Register.jsx
│   ├── users/
│   │   ├── UserList.jsx
│   │   ├── UserDetail.jsx
│   │   └── UserForm.jsx
│   └── health/
│       ├── HealthMetrics.jsx
│       └── HealthForm.jsx
│
├── pages/
│   ├── LoginPage.jsx
│   ├── DashboardPage.jsx
│   └── NotFoundPage.jsx
│
├── utils/
│   ├── constants.js           # URLs, constantes
│   ├── validators.js          # Validaciones
│   └── formatters.js          # Formateo de datos
│
└── App.jsx
```

---

## 🧪 TESTING CON FETCH Y AXIOS

### Mock de Fetch para Tests

```javascript
// __tests__/userService.test.js
import { getUsers } from '../services/userService';

global.fetch = jest.fn();

describe('userService', () => {
  afterEach(() => {
    fetch.mockClear();
  });

  it('should fetch users successfully', async () => {
    const mockResponse = {
      data: [{ id: 1, name: 'John' }],
      pagination: { page: 0, size: 20, totalElements: 1 }
    };

    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse
    });

    const result = await getUsers(0, 20);
    expect(result).toEqual(mockResponse);
    expect(fetch).toHaveBeenCalledWith(
      'http://localhost:8080/api/v1/users?page=0&size=20',
      expect.any(Object)
    );
  });
});
```

---

## 🚀 BUENAS PRÁCTICAS

1. **Siempre maneja errores** - Usa try/catch o `.catch()`
2. **Carga los datos en useEffect** - Evita cargas múltiples
3. **Usa cancelación de requests** - Con AbortController
4. **Valida datos antes de enviar** - Lado del cliente
5. **Cachea respuestas cuando sea posible** - Reduce llamadas
6. **Usa constantes para URLs** - No hardcodees URLs
7. **Implementa exponential backoff** - Para reintentos
8. **Loguea errores** - Para debugging en producción
9. **Usa TypeScript** - Si es posible, para type safety
10. **Documenta tu API** - Mantén actualizado este documento

---

**Última actualización:** 14 de Mayo de 2026
**Versión:** 1.0.0
