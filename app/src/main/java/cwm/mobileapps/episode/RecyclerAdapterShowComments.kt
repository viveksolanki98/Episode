package cwm.mobileapps.episode

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

// This adapter handles the recycler view in the show comments fragment of the show page avtivity
class RecyclerAdapterShowComments(val commentDataArr : JSONArray) : RecyclerView.Adapter<RecyclerAdapterShowComments.ViewHolder>() {
    val size = commentDataArr.length()
    override fun getItemCount() = size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println("appdebug: recyclerAdapterShowComments: in RV")
        val singleCommentData = commentDataArr.getJSONObject(position)
        //Try to get the real name of the user, if cant then use the username
        try {
            val name = singleCommentData.getJSONObject("user").getString("name")
            //If name is blank then use the username
            holder.userNameTXT.text = if(name=="") singleCommentData.getJSONObject("user").getString("username") else name
        }catch (e : Exception){
            holder.userNameTXT.text = singleCommentData.getJSONObject("user").getString("username")
        }
        holder.likesCounterTXT.text = singleCommentData.getString("likes")

        //Get the timestamp the post was posted on
        val postDate = singleCommentData.getString("created_at")
        //Format the timestamp
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        formatter.setLenient(false)
        val timeInMilliseconds = formatter.parse(postDate)?.time

        //Work out the time that has elapsed since the post was made
        val timeSinceMILI = System.currentTimeMillis() - timeInMilliseconds!!
        val minutes = timeSinceMILI / 60000
        val hours = minutes / 60
        val days = hours / 24
        val months = days / 30
        val years = days / 365
        //Work out what time unit to display the timeSince post in
        val timeSincePrefix = if (years >= 1){
            "$years years"
        }else if(months >= 1){
            "$months Months"
        }else if(days >= 1){
            "$days days"
        }else if(hours >= 1){
            "$hours hours"
        }else{
            "$minutes minutes"
        }
        holder.timeSincePostTXT.text = timeSincePrefix + " ago"

        holder.commentBodyTXT.text = singleCommentData.getString("comment")

        //Try to apply the user profile picture if it exists
        try{
            val imageLocation = singleCommentData
            .getJSONObject("user")
            .getJSONObject("images")
            .getJSONObject("avatar")
            .getString("full")
        (holder.itemView.context as Activity?)?.runOnUiThread {
            Glide.with(holder.itemView.context as Activity)
                .load(imageLocation)
                .circleCrop()
                .into(holder.commentProficePicIV)
        }
        }catch (e : Exception){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ViewHolder{
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.show_page_comment_card, parent, false)
        return ViewHolder(view)

    }

    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val userNameTXT : TextView = itemView.findViewById(R.id.userName_txt)
        val timeSincePostTXT : TextView = itemView.findViewById(R.id.timeSincePost_txt)
        val commentBodyTXT : TextView = itemView.findViewById(R.id.commentBody_txt)
        val likesCounterTXT : TextView = itemView.findViewById(R.id.likesCounter_txt)
        val commentProficePicIV : ImageView = itemView.findViewById(R.id.commentProficePic_iv)

    }


}