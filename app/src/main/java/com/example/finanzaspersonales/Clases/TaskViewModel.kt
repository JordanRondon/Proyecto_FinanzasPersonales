package com.example.finanzaspersonales.Clases

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel()
{
    var categoria =  MutableLiveData<String>()
    var monto = MutableLiveData<Float>()
}