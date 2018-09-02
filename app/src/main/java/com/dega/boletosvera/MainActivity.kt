package com.dega.boletosvera

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ContractVera.View {


    private lateinit var presenter: VeraPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = VeraPresenter(context = this, view = this)

        scanBtn.setOnClickListener {
            presenter.onScannPRessed()
        }

        presenter.initializeTickets()
    }


    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun showGreenScreen() {
        background.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.green))
        scanBtn.visibility = View.GONE
        result.visibility = View.VISIBLE
        result.text = getString(R.string.bienvenido)
    }

    override fun showRedScreen() {
        background.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.red))
        scanBtn.visibility = View.GONE
        result.visibility = View.VISIBLE
        result.text = getString(R.string.invalido)
    }

    override fun resetScreen() {
        background.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.white))
        scanBtn.visibility = View.VISIBLE
        result.visibility = View.GONE
    }

}
