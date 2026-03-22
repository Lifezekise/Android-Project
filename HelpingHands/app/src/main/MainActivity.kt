package com.athisintiya.helpinghands

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athisintiya.helpinghands.ui.theme.HelpingHandsTheme

/**
 * Main activity for the Helping Hands app. This activity hosts the primary UI for the My Account screen.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelpingHandsTheme {
                MyAccountScreen()
            }
        }
    }
}


@Composable
fun MyAccountScreen() {
    // The main container for the screen, providing a light gray background and padding.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // A white card-like container for the account information.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(24.dp)
        ) {
            // Screen title
            Text(
                text = "My Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Full Name section
            Text(
                text = "Full Name",
                fontSize = 16.sp,
                color = Color(0xFF374151)
            )
            Spacer(modifier = Modifier.height(4.dp))
            AccountDetailBox(text = "Athi Sintiya")

            Spacer(modifier = Modifier.height(16.dp))

            // Username section
            Text(
                text = "Username",
                fontSize = 16.sp,
                color = Color(0xFF374151)
            )
            Spacer(modifier = Modifier.height(4.dp))
            AccountDetailBox(text = "athisintiya@...")
        }
    }
}


@Composable
fun AccountDetailBox(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            
            .border(
                width = 1.dp,
                color = Color(0xFF374151),
                shape = RoundedCornerShape(8.dp)
            )
            .background(Color(0xFFF9FAFB))
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            color = Color(0xFF1F2937)
        )
    }
}

/**
 * Provides a preview of the My Account screen in Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun MyAccountScreenPreview() {
    HelpingHandsTheme {
        MyAccountScreen()
    }
}
