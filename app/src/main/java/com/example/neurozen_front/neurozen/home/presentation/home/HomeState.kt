package pe.edu.upc.easyvet.home.presentation.home

import pe.edu.upc.easyvet.home.domain.model.Product


data class HomeState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)