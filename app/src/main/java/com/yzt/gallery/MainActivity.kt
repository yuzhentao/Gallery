package com.yzt.gallery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn.setOnClickListener { openAlbum() }
    }

    private fun openAlbum() {
        require(Album.get() != null) {
            return
        }

        Album
            .get()!!
            .setContext(this)
            .maxSelectedCount(1)
            .hasSystemCamera(true)
            .hasSystemAlbum(true)
            .startActivityForResult(this, 1000)
    }

}