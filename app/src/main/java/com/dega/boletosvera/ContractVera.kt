package com.dega.boletosvera

import android.content.Intent

interface ContractVera {

    interface Presenter {

        fun onScannPRessed()
        fun onStop()
        fun initializeTickets()
        fun saveCurrentStatus()
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
        fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
        fun launchCamera()
        fun readFile()
        fun idsToStringAndSave(ids: ArrayList<String>)
        fun checkId(id: String)
        fun save(stringIds: String)
        fun loadIds(): List<String>?
    }

    interface View {
        fun showGreenScreen()
        fun showRedScreen()
        fun resetScreen()
    }
}