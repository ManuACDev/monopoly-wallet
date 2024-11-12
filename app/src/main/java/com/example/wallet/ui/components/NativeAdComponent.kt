package com.example.wallet.ui.components

import android.content.res.Resources
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import android.graphics.Color
import android.widget.LinearLayout
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView


@Composable
fun NativeAdComponent(modifier: Modifier = Modifier, adUnitId: String) {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var isAdLoading by remember { mutableStateOf(true) }

    // Cargar el anuncio nativo al inicio Prueba: "ca-app-pub-3940256099942544/2247696110"
    LaunchedEffect(Unit) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad : NativeAd ->
                nativeAd = ad
                isAdLoading = false
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e("NativeAd", "Error al cargar el anuncio: $loadAdError")
                    isAdLoading = false
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    DisposableEffect(nativeAd) {
        onDispose {
            nativeAd?.destroy()
        }
    }

    nativeAd?.let { ad ->
        AndroidView(
            modifier = modifier,
            factory = { context ->
                NativeAdView(context).apply {
                    setNativeAd(ad)
                    val adContainer = LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                        gravity = android.view.Gravity.CENTER
                    }

                    // Configurar los elementos del anuncio dentro de NativeAdView
                    val headlineView = TextView(context).apply {
                        text = ad.headline
                        setTextColor(Color.WHITE)
                        textSize = 16f
                    }
                    val bodyView = TextView(context).apply {
                        text = ad.body
                        setTextColor(Color.GRAY)
                        textSize = 14f
                    }
                    val iconView = ImageView(context).apply {
                        setImageDrawable(ad.icon?.drawable)
                        layoutParams = LinearLayout.LayoutParams(
                            150.dp.toPx().toInt(), // Ancho del icono
                            150.dp.toPx().toInt() // Alto del icono
                        ).apply {
                            topMargin = 14.dp.toPx().toInt() // Padding superior para la imagen
                        }
                    }

                    // Añadir las vistas al contenedor
                    adContainer.addView(headlineView)
                    adContainer.addView(bodyView)
                    adContainer.addView(iconView)

                    // Añadir el contenedor al NativeAdView
                    this.addView(adContainer)

                    // Enlazar las vistas con NativeAdView
                    this.headlineView = headlineView
                    this.bodyView = bodyView
                    this.iconView = iconView
                }
            }
        )
    }
}

fun Dp.toPx(): Float {
    return this.value * Resources.getSystem().displayMetrics.density
}
