package com.yzt.gallery

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.yzt.gallery.util.AlbumLogUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            data?.let {
                it.getStringExtra("intent_path").let { path ->
                    when (requestCode) {
                        1000 -> {
                            Glide
                                .with(this)
                                .load(path)
                                .into(iv)
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

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