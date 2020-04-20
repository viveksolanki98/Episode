package cwm.mobileapps.episode

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.DataSnapshot
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_show_page.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.math.RoundingMode


class ShowPageActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_page)

        val userAccountDetails= GoogleSignIn.getLastSignedInAccount(this)

        val showTitle = intent.getStringExtra("show_title")
        val showID = intent.getStringExtra("show_id")

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

        var urlSTR = "https://api.trakt.tv/shows/$showID?extended=full"

        val request = Request
            .Builder()
            .url(urlSTR)
            .addHeader("Content-Type","application/json")
            .addHeader("trakt-api-version","2")
            .addHeader("trakt-api-key","60208da48cb89f83f54f9686b0027df865b8aa8e51d2af64e7e4429b2cac7b28")
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException){
                println("FAIL API")
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                val showDataObj = JSONObject(response.body!!.string())
                val showStartYear = showDataObj.getString("first_aired").split("-")[0]

                val showRating = showDataObj.getDouble("rating").toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
                val showStatus = showDataObj.getString("status").split(" ").joinToString(" ") { it.capitalize() }.trimEnd()

                val showDetails = "$showStartYear, ${showDataObj.getString("network")}, $showStatus, $showRating"
                runOnUiThread(Runnable {
                    showDescription_txt.text = showDataObj.getString("overview")
                    showDetails_txt.text = showDetails

                })

            }
        })

        FBDBhandler.query("UserID_ShowID", "${userAccountDetails?.id}_${showID}", fun(data : DataSnapshot?){
            if (data!!.getValue() == null){
                addRemoveShow_btn.text = "+"
            }else{
                addRemoveShow_btn.text = "-"
            }
        })

        addRemoveShow_btn.setOnClickListener {
            if (addRemoveShow_btn.text == "+"){
                FBDBhandler.addRecord("tt1", showID, (userAccountDetails?.id)!!.toString())
                addRemoveShow_btn.text = "-"
            }else{
                FBDBhandler.deleteRecord("UserID_ShowID", "${userAccountDetails?.id}_${showID}", fun(){
                    println("appdebug: delete: show page: SUCCESS")}, fun(){
                    println("appdebug: delete: show page: FAIL")
                })
                addRemoveShow_btn.text = "+"
            }
        }

    }
}
