package cwm.mobileapps.episode

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.ShareActionProvider
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.viewpager.widget.ViewPager
import com.beust.klaxon.JsonArray
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_show_page.*
import okhttp3.Response
import org.json.JSONArray


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
        userID = myPref.getString("user_id_google", "")


        //Add fragments to view pager
        val adapter = viewPagerAdapter(supportFragmentManager)
        adapter.addFragment(ShowTrackingSPFragment(), " Show Tracking ")
        adapter.addFragment(ShowCommentsSpFragment(), " Show Comments ")
        showPage_vp.adapter = adapter
        //Add tabs to tab layout
        vpTabShowPage_tl.addTab(vpTabShowPage_tl.newTab().setText("Details"))
        vpTabShowPage_tl.addTab(vpTabShowPage_tl.newTab().setText("Comments"))

        //On tab selected, change visible fragment to selected one
        vpTabShowPage_tl.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab) {
                showPage_vp.currentItem = tab.position
            }
        })
        //On fragment changed by swiping, change the selected tab in the tab layout
        showPage_vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                val tab = vpTabShowPage_tl.getTabAt(position)
                tab?.select()
            }
        })

        //Get data from intent
        val showTitle = intent.getStringExtra("show_title")
        val showID = intent.getStringExtra("show_id")
        val posterURI =  intent.getStringExtra("show_poster")

        showTitle_txt.text = showTitle
        //Apply the show poster to the background of the page, blurred
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
                    val dr: Drawable = BitmapDrawable(resources, resource)
                    showPage_cl.background = dr
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })

        //IF show in FBDB then show remove icon, else show add icon
        FBDBhandler.queryListener("UserID_ShowID", "${userID}_${showID}", fun(data : DataSnapshot?){
            if (data!!.getValue() == null){
                addRemoveShow_btn.text = "+"
            }else{
                addRemoveShow_btn.text = "-"
            }
        })


        addRemoveShow_btn.setOnClickListener {
            if (addRemoveShow_btn.text == "+"){
                //To add the show to the collection
                FBDBhandler.addRecord("tt1", showID!!, userID!!)
                addRemoveShow_btn.text = "-"
            }else{
                //To remove show from collection and end activity
                FBDBhandler.deleteRecord("UserID_ShowID", "${userID}_${showID}", fun(){
                    println("appdebug: showPage: delete from FBDB: SUCCESS")
                    //Then remove from SQL Db
                    val deleteRes = ContentProviderHandler().delete(contentResolver, showID!!)
                    println("appdebug: showPage: delete from SQLDb: $deleteRes")
                }, fun(){
                    println("appdebug: showPage: delete from FBDB: FAIL")
                })
                addRemoveShow_btn.text = "+"
                finish()
            }
        }

        //---------------------------------------

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val showID = intent.getStringExtra("show_id")

        APIhandler.trackitAPIAsync("https://api.trakt.tv/search/trakt/$showID", fun(apiDATA : Response){
            //This try and catch is needed to respond to a valid show id
            try {
                //get IMDb Id
                val imdbID = JSONArray(apiDATA.body!!.string()).getJSONObject(0).getJSONObject("show").getJSONObject("ids").getString("imdb")
                runOnUiThread {
                    //Inflate sharing menu
                    menuInflater.inflate(R.menu.share_menu, menu)
                    val shareItem = menu!!.findItem(R.id.action_share)
                    //Prepare share intent
                    val showTitle = intent.getStringExtra("show_title")
                    val myShareActionProvider: ShareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider
                    val myShareIntent = Intent(Intent.ACTION_SEND)
                    //Write message
                    myShareIntent.type = "text/html"
                    myShareIntent.putExtra(Intent.EXTRA_TEXT, "I am watching this great show. You should check it out: $showTitle https://www.imdb.com/title/$imdbID")
                    //Ready to share
                    myShareActionProvider.setShareIntent(myShareIntent)
                }
            }catch (e : Exception){}
        })

        return super.onCreateOptionsMenu(menu)
    }
}
