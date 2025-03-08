package com.front_pes.features.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF)) // Light gray background
            .padding(top = 150.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Settings", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        // White container with rounded edges
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f) // Adjust width
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp), // Rounded edges
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SettingItem("Content 1")
                SettingItem("Content 2")
                SettingItem("Content 3")
            }
        }
    }
}

@Composable
fun SettingItem(text: String) { //A modificar para poder meter un icono/imagen especifica segun el item
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val gradientBrush = Brush.linearGradient(
            colors = listOf(Color(0xFF05C7F2), Color(0xFF07F285))
        )

        Icon(
            painter = rememberVectorPainter(image = Icons.Default.Settings),
            contentDescription = "Settings Icon",
            tint = Color.White, // Ensures gradient applies correctly
            modifier = Modifier
                .size(24.dp)
                .background(brush = gradientBrush, shape = RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 18.sp)
    }
}