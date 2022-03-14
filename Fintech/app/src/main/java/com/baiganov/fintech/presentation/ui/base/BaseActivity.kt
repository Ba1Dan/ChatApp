package com.baiganov.fintech.presentation.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<V : ViewBinding> : AppCompatActivity() {

    lateinit var binding: V

    abstract fun getBinding(inflater: LayoutInflater): V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getBinding(layoutInflater)
        setContentView(binding.root)
    }
}