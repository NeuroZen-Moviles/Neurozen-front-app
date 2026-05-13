package pe.edu.upc.easyvet.home.presentation.navigation

import pe.edu.upc.easyvet.R


enum class MainTab(
    val label: String,
    val icon: Int,
    val iconFilled: Int
) {
    Home(
        label = "Home",
        icon = R.drawable.home,
        iconFilled = R.drawable.home_filled

    ),
    Favorites(
        label = "Favorites",
        icon = R.drawable.favorite,
        iconFilled = R.drawable.favorite_filled
    ),
    Cart(
        label = "Cart",
        icon = R.drawable.shopping_cart,
        iconFilled = R.drawable.shopping_cart_filled
    ),
    Profile(
        label = "Profile",
        icon = R.drawable.person,
        iconFilled = R.drawable.person_filled
    )

}