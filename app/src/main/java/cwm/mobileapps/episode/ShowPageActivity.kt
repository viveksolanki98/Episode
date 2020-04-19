package cwm.mobileapps.episode

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_show_page.*


class ShowPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_page)

        val showTitle = intent.getStringExtra("show_title")

        showTitle_txt.text = showTitle
        //https://image.tmdb.org/t/p/w500//AkFlhbRl7HZVWpIqaMPVXCuOQHI.jpg

        //Glide.with(holder.itemView.context).load("https://image.tmdb.org/t/p/w500/" + showImageLocations[position]).into(holder.showPosterIV)

        // Set background image
        var posterURI =  intent.getStringExtra("show_poster")
                Glide
            .with(applicationContext)
            .asBitmap()
            .load(posterURI)
            .apply(bitmapTransform(BlurTransformation(8, 3)))
            .into(object : CustomTarget<Bitmap?>(100, 100) {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    val dr: Drawable = BitmapDrawable(resource)
                    showPage_cl.background = dr
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }
}
