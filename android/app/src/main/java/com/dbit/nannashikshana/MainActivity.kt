package com.dbit.nannashikshana

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dbit.nannashikshana.ui.navigation.NannaMainScaffold
import com.dbit.nannashikshana.ui.theme.NannaShikshanaTheme
import com.dbit.nannashikshana.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val baseContext = LocalContext.current
            val baseConfig = LocalConfiguration.current
            val langCode = uiState.language.code

            val (localizedContext, localizedConfig) = remember(baseContext, baseConfig, langCode) {
                val locale = Locale(langCode)
                Locale.setDefault(locale)
                val config = Configuration(baseConfig).apply {
                    setLocale(locale)
                }
                baseContext.createConfigurationContext(config) to config
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedConfig
            ) {
                NannaShikshanaTheme {
                    NannaMainScaffold(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
