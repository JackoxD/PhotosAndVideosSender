package com.gawel.sender.presentation.mainScreen

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.gawel.photosandvideossender.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 5000)
        }

        afterPermissions()

    }

    private fun afterPermissions() {

        binding.sendBtn.setOnClickListener {
            Log.d(TAG, "button clicked.")
            val editText = binding.albumName.editText
            val text = editText!!.text
            val adressIP: String
            if (text.isNullOrEmpty()) {
                Snackbar.make(editText, "Nazwa albumu nie może być pusta", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {
                val adressIPEdiTText = binding.addressIp.editText!!.text
                if (adressIPEdiTText.isNullOrEmpty()) {
                    Snackbar.make(editText, "Adress IP nie może być pusty", Snackbar.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                } else{
                    adressIP = adressIPEdiTText.toString()
                }
            }
            viewModel.requestWorkerManager(adressIP, text.toString())
        }

        viewModel.ipsAdresses.observe(
            this, {
                Log.d(TAG, "afterPermissions: observe: $it")
                binding.addressIp.editText!!.setText(it)
            }
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}