package cwm.mobileapps.episode

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.firebase.database.DataSnapshot
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.popup_discover.view.*
import org.json.JSONArray
import rm.com.longpresspopup.LongPressPopupBuilder
import java.lang.Exception

///val showTitle : ArrayList<String>, val showIDs : ArrayList<String>, val showImageLocations : ArrayList<String>, val showTrailer : ArrayList<String>,
class RecyclerAdapterDSLists(val allShowsArr : JSONArray, val nestedFlag : Boolean) : RecyclerView.Adapter<RecyclerAdapterDSLists.ViewHolder>(), YouTubePlayer.OnInitializedListener {
    var userID : String? = ""
    lateinit var mvGroup : ViewGroup

    override fun getItemCount() = allShowsArr.length()
        //showTitle.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myPref: SharedPreferences = holder.itemView.context!!.getSharedPreferences("Episode_pref", Context.MODE_PRIVATE)
        userID = myPref?.getString("user_id_google", "")

        val item = allShowsArr.getJSONObject(position)
        val showData = if(nestedFlag) item.getJSONObject("show") else item
        val traktID = showData.getJSONObject("ids").getString("trakt")

        holder.showTitleTXT.text = showData.getString("title")

        Thread{
            val imageLocation = APIhandler.imageFromID(showData.getJSONObject("ids"))
            (holder.itemView.context as Activity?)?.runOnUiThread {
                Glide.with(holder.itemView.context as Activity).load(imageLocation).into(holder.showPosterIV)
            }

            holder.showPosterIV.setOnClickListener {
                val intentToShowPageActivity = Intent(holder.itemView.context, ShowPageActivity::class.java)
                intentToShowPageActivity.putExtra("show_title", showData.getString("title"))
                intentToShowPageActivity.putExtra("show_id", traktID)
                intentToShowPageActivity.putExtra("show_poster", imageLocation)

                holder.itemView.context.startActivity(intentToShowPageActivity)
            }
        }.start()
        FBDBhandler.queryListener("UserID_ShowID", "${userID}_${traktID}", fun(data : DataSnapshot?) {
            println("appdebug: showExistsNEW: " + data!!.getValue())
            holder.addShowBTN.visibility = if (data.getValue() != null) View.GONE else View.VISIBLE
        })

        holder.addShowBTN.setOnClickListener {
            FBDBhandler.addRecord("tt1", traktID, userID!!)
            holder.addShowBTN.visibility = View.GONE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ViewHolder{
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.discover_lists_rv_card, parent, false)
        mvGroup = parent
        return ViewHolder(view)

    }

    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val showTitleTXT: TextView = itemView.findViewById(R.id.showTitle_txt)
        val showPosterIV: ImageView = itemView.findViewById(R.id.showPoster_iv)
        val addShowBTN : Button = itemView.findViewById(R.id.addShow_btn)
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider?, player: YouTubePlayer?, wasRestored: Boolean) {
        if (!wasRestored) {
            player?.cueVideo("wKJ9KzGQq0w");
        }
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
    }
}
