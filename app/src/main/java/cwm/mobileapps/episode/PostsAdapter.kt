package cwm.mobileapps.episode

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.security.AccessController.getContext

class PostsAdapter(val posts : ArrayList<String>, val showIDs : ArrayList<String>, val showImageLocations : ArrayList<String>) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.showTitleTXT.text = posts[position]
        //"https://image.tmdb.org/t/p/w500/"
        Glide.with(holder.itemView.context).load(showImageLocations[position]).into(holder.showPosterIV)
//------------------------------


//------------------------------
        holder.showPosterIV.setOnClickListener {
            val intentToShowPageActivity = Intent(holder.itemView.context, ShowPageActivity::class.java)
            intentToShowPageActivity.putExtra("show_title", posts[position])
            intentToShowPageActivity.putExtra("show_poster", showImageLocations[position])
            holder.itemView.context.startActivity(intentToShowPageActivity)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ViewHolder{
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_posts, parent, false)
        return ViewHolder(view)

    }

    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val showTitleTXT: TextView = itemView.findViewById(R.id.showTitle_txt)
        val showPosterIV: ImageView = itemView.findViewById(R.id.showPoster_iv)
    }
}