package com.discogs.mobilechallenge.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.discogs.mobilechallenge.ui.theme.MobileChallengeTheme

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
        TextButton(onClick = onRetry) { Text("Retry") }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorMessagePreview() {
    MobileChallengeTheme {
        ErrorMessage(
            message = "Something went wrong. Check your connection.",
            onRetry = {},
        )
    }
}
