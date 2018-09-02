package com.dega.boletosvera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import org.json.JSONArray
import java.io.InputStream
import java.util.*
import kotlin.concurrent.timerTask

class VeraPresenter(var context: Context, var view: ContractVera.View) : ContractVera.Presenter {


    private val MY_PERMISSIONS_REQUEST_CAMERA = 1
    private val LAUNCH_CAMERA = 2


    private lateinit var idsList: ArrayList<String>


    override fun onScannPressed() {

        val cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((context as Activity),
                    arrayOf(Manifest.permission.CAMERA),
                    MY_PERMISSIONS_REQUEST_CAMERA)
        } else {
            launchCamera()
        }
    }

    override fun onStop() {
        saveCurrentStatus()
    }

    override fun initializeTickets() {
        val arrayIds = this.loadIdsFromPrefs()
        if (arrayIds != null && !arrayIds[0].equals("")) {
            idsList = arrayIds as ArrayList<String>
            view.displayLeftAndScannedTickets(idsList.size)
            Log.e("VERA", "CARGADOS DE MEMORIA " + idsList.size)
        } else {
            readFile()
            view.displayLeftAndScannedTickets(idsList.size)

            Log.e("VERA", "CARGADOS DE ARCHIVO " + idsList.size)
        }
    }

    override fun saveCurrentStatus() {
        idsToStringAndSave(idsList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // check if the requestCode is the wanted one and if the result is what we are expecting
        if (requestCode == LAUNCH_CAMERA && resultCode == AppCompatActivity.RESULT_OK) {
            val scannedId = data?.getStringExtra(CameraActivity.SCANNED_QR)
            Toast.makeText(context, scannedId, Toast.LENGTH_SHORT).show()
            scannedId?.let { checkId(it) }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    launchCamera()
                }
                return
            }
        }
    }

    override fun launchCamera() {
        val intent = Intent(context, CameraActivity::class.java)
        (context as Activity).startActivityForResult(intent, LAUNCH_CAMERA)
    }

    private fun readFile() {
        try {
            val inputStream: InputStream = context.assets.open("ids.json")
            val inputString = inputStream.bufferedReader().use { it.readText() }

            val jsonArray = JSONArray(inputString)

            idsList = ArrayList<String>()

            for (i in 0 until jsonArray.length()) {
                idsList.add(jsonArray[i] as String)
            }
        } catch (e: Exception) {
            Log.e("READ", e.toString())
        }
    }

    private fun idsToStringAndSave(ids: ArrayList<String>) {
        val sb = StringBuilder()
        for (i in 0 until ids.size) {
            if (ids[i] != "") {
                // If last item do not append comma
                if (i == ids.size - 1) {
                    sb.append(ids[i])
                } else {
                    sb.append(ids[i]).append(",")
                }
            }
        }
        save(sb.toString())
    }

    override fun checkId(id: String) {
        if (idsList.contains(id)) {
            view.showGreenScreen()
            idsList.remove(id)
        } else {
            view.showRedScreen()
        }
        val timer = Timer()
        timer.schedule(timerTask {
            (context as Activity).runOnUiThread(Runnable
            {
                view.resetScreen()
                view.displayLeftAndScannedTickets(idsList.size)
            })
        }, 500)
    }

    override fun save(stringIds: String) {
        val sharedPref = context.getSharedPreferences(context.getString(R.string.ids),
                Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(context.getString(com.dega.boletosvera.R.string.ids), stringIds)
            apply()
        }
    }

    override fun loadIdsFromPrefs(): List<String>? {
        val sharedPref = context.getSharedPreferences(context.getString(R.string.ids),
                Context.MODE_PRIVATE)
        val stringIds = sharedPref?.getString(context.getString(R.string.ids), null)
        return stringIds?.split(",")
    }

    override fun showVersionCode(): Boolean {
        Toast.makeText(context, "Versión: " + BuildConfig.VERSION_NAME + "\n git:davedega", Toast.LENGTH_SHORT).show()
        return true
    }


}