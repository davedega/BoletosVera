package com.dega.boletosvera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask


class MainActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST_CAMERA = 1
    private val LAUNCH_CAMERA = 2

    private lateinit var list: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        scanBtn.setOnClickListener {
            // Check camera permission and launch camera if grant
            val cameraPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA),
                        MY_PERMISSIONS_REQUEST_CAMERA)
            } else {
                launchCamera()
            }
        }

        initializeTickets()
    }


    override fun onStop() {
        super.onStop()
        saveCurrentStatus()
    }

    fun initializeTickets() {
        val arrayIds = this.loadIds()
        if (arrayIds != null && !arrayIds[0].equals("")) {
            list = arrayIds as ArrayList<String>
            Log.e("VERA", "CARGADOS DE MEMORIA")
        } else {
            Log.e("VERA", "CARGADOS DE ARCHIVO")
            readFile()
        }
    }

    fun saveCurrentStatus() {
        idsToStringAndSave(list)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // check if the requestCode is the wanted one and if the result is what we are expecting
        if (requestCode == LAUNCH_CAMERA && resultCode == RESULT_OK) {
            val scannedId = data?.getStringExtra(CameraActivity.SCANNED_QR)
            Toast.makeText(applicationContext, scannedId, Toast.LENGTH_SHORT).show()
            scannedId?.let { checkId(it) }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera()
                }
                return
            }
        }
    }

    fun launchCamera() {
        val intent = Intent(applicationContext, CameraActivity::class.java)
        startActivityForResult(intent, LAUNCH_CAMERA)
    }

    fun readFile() {
        try {
            val inputStream: InputStream = assets.open("ids.json")
            val inputString = inputStream.bufferedReader().use { it.readText() }

            val jsonArray = JSONArray(inputString)

            list = ArrayList<String>()

            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray[i] as String)
            }
        } catch (e: Exception) {
            Log.e("READ", e.toString())
        }
    }

    fun checkId(id: String) {
        if (list.contains(id)) {
            showGreenScreen()
            list.remove(id)
        } else {
            showRedScreen()
        }
        val timer = Timer()
        timer.schedule(timerTask { runOnUiThread(Runnable { resetScreen() }) }, 2000)
    }

    fun showGreenScreen() {
        background.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.green))
        scanBtn.visibility = View.GONE
        result.visibility = View.VISIBLE
        result.text = getString(R.string.bienvenido)
    }

    fun showRedScreen() {
        background.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.red))
        scanBtn.visibility = View.GONE
        result.visibility = View.VISIBLE
        result.text = getString(R.string.invalido)
    }

    fun resetScreen() {
        background.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.white))
        scanBtn.visibility = View.VISIBLE
        result.visibility = View.GONE
    }


    fun idsToStringAndSave(ids: ArrayList<String>) {
        val sb = StringBuilder()
        for (i in 0 until ids.size) {
            sb.append(ids[i]).append(",")
        }
        save(sb.toString())
    }

    fun save(stringIds: String) {
        val sharedPref = applicationContext?.getSharedPreferences(getString(R.string.ids),
                Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(getString(R.string.ids), stringIds)
            commit()
        }
    }

    fun loadIds(): List<String>? {
        val sharedPref = applicationContext?.getSharedPreferences(getString(R.string.ids),
                Context.MODE_PRIVATE)
        val stringIds = sharedPref?.getString(getString(R.string.ids), "")
        return stringIds?.split(",")
    }
}
