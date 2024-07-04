package com.example.finanzaspersonales

import RecordatorioViewModel
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RecordatorioViewModelFactory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordatorioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordatorioViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
