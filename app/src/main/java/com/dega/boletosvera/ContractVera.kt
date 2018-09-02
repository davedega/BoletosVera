package com.dega.boletosvera

import android.content.Intent

interface ContractVera {

    interface Presenter {

        /**
         * Load ids from file in case that ids are not in Prefs
         */
        fun initializeTickets()

        /**
         * Retrieve ids from Prefs, split it by commas and return the list
         */
        fun loadIdsFromPrefs(): List<String>?

        /**
         * Check camera permission and launch camera if granted
         */
        fun onScannPressed()

        fun launchCamera()

        /**
         * Scanned ids are saved in SharedPrefs
         */
        fun saveCurrentStatus()

        /**
         * Whether the id is in the list or not, in that case the id is removed
         * from the list
         */
        fun checkId(id: String)

        /**
         * Save all strings separated by comma
         */
        fun save(stringIds: String)

        /**
         * Handle lifecycle methods, saveCurrentStatus() is called
         */
        fun onStop()

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

        fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)

        fun showVersionCode(): Boolean


    }

    interface View {
        fun showGreenScreen()
        fun showRedScreen()
        fun resetScreen()
        fun displayLeftAndScannedTickets(size: Int)
    }
}