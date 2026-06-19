package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.AurumRepository
import com.example.ui.AppNav
import com.example.ui.theme.AurumGridTheme
import com.example.vm.MainViewModel
import com.example.vm.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Support edge-to-edge full bleed rendering
        enableEdgeToEdge()

        // Core compilation of local database persistence
        val database = AppDatabase.getDatabase(this)
        val repository = AurumRepository(database)
        
        // Initialize reactive application ViewModel
        val viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(application, repository)
        )[MainViewModel::class.java]

        setContent {
            AurumGridTheme {
                AppNav(viewModel)
            }
        }
    }
}
