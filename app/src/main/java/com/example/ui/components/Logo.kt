package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.WhiteText

@Composable
fun NoxKaavLogo(
    logoUri: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    fontSize: Int = (size.value * 0.6f).toInt()
) {
    if (logoUri != null) {
        AsyncImage(
            model = logoUri,
            contentDescription = "Logo NoxKaav",
            modifier = modifier.size(size).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "N",
                color = WhiteText,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
