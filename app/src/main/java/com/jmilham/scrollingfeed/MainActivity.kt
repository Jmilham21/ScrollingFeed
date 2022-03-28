package com.jmilham.scrollingfeed

import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.jmilham.scrollingfeed.databinding.MainActivityBinding
import com.jmilham.scrollingfeed.ui.main.MainFragment
import com.jwplayer.pub.api.license.LicenseUtil


class MainActivity : AppCompatActivity() {

    private val fragmentTag = "MainFragmentTag"
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        LicenseUtil().setLicenseKey(this, BuildConfig.JWPLAYER_LICENSE_KEY)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, MainFragment.newInstance(), fragmentTag)
                .commitNow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // handle button activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.change_playlist) {
            showDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Input Playlist ID")

        // Set up the input
        val input = EditText(this)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.hint = "Enter playlist ID"
        input.inputType = InputType.TYPE_CLASS_TEXT

        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            val playlistId = input.text.toString()
            if (playlistId.isNotEmpty()) {
                val frag = supportFragmentManager.findFragmentByTag(fragmentTag)
                (frag as MainFragment).updatePlaylistId(playlistId)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()

    }
}