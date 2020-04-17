package cwm.mobileapps.episode

import android.app.PendingIntent.getActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import java.security.AccessController.getContext

class PostsAdapter(val posts : ArrayList<String>, val showImageLocations : ArrayList<String>) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.showTitleTXT.text = posts[position]
        Glide.with(holder.itemView.context).load("https://image.tmdb.org/t/p/w500/" + showImageLocations[position]).into(holder.showPosterIM)
        //https://image.tmdb.org/t/p/w500/lbIMe94gXNGBzlFACqbrUyEXpyN.jpg
        //Picasso.get().load("https://image.tmdb.org/t/p/w500/" + showImageLocations[position]).into(holder.showPosterIM);
        println("My Image: " + "https://image.tmdb.org/t/p/w500/" + showImageLocations[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ViewHolder{
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_posts, parent, false)
        return ViewHolder(view)

    }

    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val showTitleTXT: TextView = itemView.findViewById(R.id.showTitle_txt)
        val showPosterIM: ImageView = itemView.findViewById(R.id.showPoster_iv)
    }
}