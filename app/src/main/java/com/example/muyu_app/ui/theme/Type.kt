package com.example.muyu_app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R

val UrbanistBold = FontFamily(
    Font(R.font.urbanist_bold, FontWeight.Bold)
)

val UrbanistSemiBold = FontFamily(
    Font(R.font.urbanist_semibold, FontWeight.SemiBold)
)

val UrbanistRegular = FontFamily(
    Font(R.font.urbanist_regular, FontWeight.Normal)
)

val UrbanistLight = FontFamily(
    Font(R.font.urbanist_light, FontWeight.Light)
)

val UrbanistExtraBold = FontFamily(
    Font(R.font.urbanist_extrabold, FontWeight.ExtraBold)
)

val UrbanistBlack = FontFamily(
    Font(R.font.urbanist_extrabold, FontWeight.Black)
)

val UrbanistMedium = FontFamily(
    Font(R.font.urbanist_semibold, FontWeight.Medium)
)

val FredokaSemiBold = FontFamily(
    Font(R.font.fredoka_semibold, FontWeight.SemiBold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)