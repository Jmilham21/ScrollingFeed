package com.jmilham.scrollingfeed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jmilham.scrollingfeed.ui.main.MainFragment
import com.jwplayer.pub.api.license.LicenseUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        LicenseUtil.setLicenseKey(this, BuildConfig.JWPLAYER_LICENSE_KEY)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
    }
}