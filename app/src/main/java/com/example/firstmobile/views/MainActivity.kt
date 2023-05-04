package com.example.firstmobile.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import com.example.firstmobile.views.layouts.MainLayout

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    
    private val blockViewModel = CodeBlockViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainLayout(blockViewModel)
        }
    }
}
