package cwm.mobileapps.episode

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.DataSnapshot
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_show_page.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.math.RoundingMode


class ShowPageActivity : AppCompatActivity() {
    var userID : String? = ""
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_page)

        //Get user ID from shared preferences
        val myPref: SharedPreferences = this.getSharedPreferences("Episode_pref",
            Context.MODE_PRIVATE
        )
        userID = myPref?.getString("user_id_google", "")



        val adapter = viewPagerAdapter(supportFragmentManager)
        adapter.addFragment(ShowTrackingSPFragment(), " Show Tracking ")
        adapter.addFragment(ShowCommentsSpFragment(), " Show Comments ")
        showPage_vp.adapter = adapter


        val showTitle = intent.getStringExtra("show_title")
        val showID = intent.getStringExtra("show_id")
        var posterURI =  intent.getStringExtra("show_poster")

        showTitle_txt.text = showTitle
        Glide
            .with(this)
            .asBitmap()
            .load(posterURI)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(8, 3)))
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


        FBDBhandler.queryListener("UserID_ShowID", "${userID}_${showID}", fun(data : DataSnapshot?){
            if (data!!.getValue() == null){
                addRemoveShow_btn.text = "+"
            }else{
                addRemoveShow_btn.text = "-"
            }
        })


        addRemoveShow_btn.setOnClickListener {
            if (addRemoveShow_btn.text == "+"){
                FBDBhandler.addRecord("tt1", showID, userID!!)
                addRemoveShow_btn.text = "-"
            }else{
                FBDBhandler.deleteRecord("UserID_ShowID", "${userID}_${showID}", fun(){
                    println("appdebug: delete: show page: SUCCESS")}, fun(){
                    println("appdebug: delete: show page: FAIL")
                })
                addRemoveShow_btn.text = "+"
                finish()
            }
        }

        //---------------------------------------




    }
}
