package com.example.neurozen_front.neurozen.data.network

object ApiConfig {
    // IMPORTANTE PARA CELULAR REAL:
    // 1. Cambia "10.0.2.2" por la IP de tu PC (ejemplo: "192.168.1.15")
    // 2. Asegúrate de que tu celular y PC estén en el mismo Wi-Fi.
    // 3. El puerto 8080 debe estar abierto en tu PC.
    
    private const val MY_PC_IP = "10.0.2.2" // <-- CAMBIA ESTO POR TU IP (ej: "192.168.1.15")
    
    const val BASE_URL = "http://$MY_PC_IP:8080/api/v1/"
}
