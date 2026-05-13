package pe.edu.upc.easyvet.home.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pe.edu.upc.easyvet.R
import pe.edu.upc.easyvet.home.presentation.theme.AppTheme

@Composable
fun OnBoarding() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null
        )

        Text(
            text = "From Bowl to Soul \nWe've Got It All!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Buy the best pet food packed with health and nutrition for below pet",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center

        )
        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = {

        }, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)) {
            Text("Create account")
        }

        OutlinedButton(onClick = {

        }, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)) {
            Text("Sign in")
        }
        Spacer(modifier = Modifier.weight(1f))

    }
}

@Preview(showBackground = true)
@Composable
fun OnBoardingPreview() {
    AppTheme {
        OnBoarding()
    }
}