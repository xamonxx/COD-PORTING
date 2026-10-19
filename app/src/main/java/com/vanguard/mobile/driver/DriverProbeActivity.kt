package com.vanguard.mobile.driver

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class DriverProbeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContentView(TextView(this).apply {
            text = "GPU probe process is isolated and awaiting a verified driver package."
            textSize = 18f
            setPadding(48, 48, 48, 48)
        })
    }
}
