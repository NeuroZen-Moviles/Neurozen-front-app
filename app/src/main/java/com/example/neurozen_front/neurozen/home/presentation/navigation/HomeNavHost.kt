package pe.edu.upc.easyvet.home.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import pe.edu.upc.easyvet.home.domain.model.Product
import pe.edu.upc.easyvet.home.presentation.home.Home
import pe.edu.upc.easyvet.home.presentation.home.HomeViewModel


@Composable
fun HomeNavHost() {

    val navController = rememberNavController()
    val viewModel: HomeViewModel = viewModel()

    val selectedProduct = remember {
        mutableStateOf<Product?>(null)
    }

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {

        composable("home") {
            Home(viewModel) { product ->
                selectedProduct.value = product
                navController.navigate("product_detail")
            }
        }


        composable("product_detail") {
            selectedProduct.value?.let { product ->

                Column {
                    AsyncImage(model = product.image, contentDescription = product.name)
                    Text(product.name)

                }

            }
        }
    }


}