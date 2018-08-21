package com.dega.boletosvera

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView


class CameraActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    lateinit var mScannerView: ZXingScannerView

    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        mScannerView = ZXingScannerView(this)   // Programmatically initialize the scanner view
        setContentView(mScannerView)                // Set the scanner view as the content view
    }

    public override fun onResume() {
        super.onResume()
        mScannerView?.let {
            it.setResultHandler(this) // Register ourselves as a handler for scan results.
            it.startCamera()          // Start camera on resume
        }
    }

    public override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera()           // Stop camera on pause
    }

    override fun handleResult(result: Result?) {
//        mScannerView!!.resumeCameraPreview(this)
        val resultIntent = Intent()
        resultIntent.putExtra(SCANNED_QR, result.toString())
        setResult(Activity.RESULT_OK, resultIntent)
        finish()

    }

    companion object {
        @JvmField
        val SCANNED_QR = "SCANNED_QR"
    }

}