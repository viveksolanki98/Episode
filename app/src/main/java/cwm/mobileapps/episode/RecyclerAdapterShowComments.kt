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


class RecyclerAdapterShowComments(val commentDataArr : JSONArray) : RecyclerView.Adapter<RecyclerAdapterShowComments.ViewHolder>() {
    val size = commentDataArr.length()
    override fun getItemCount() = size
    //commentDataArr.length()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println("appdebug: recyclerAdapterShowComments: in RV")
        val singleCommentData = commentDataArr.getJSONObject(position)
        try {
            val name = singleCommentData.getJSONObject("user").getString("name")
            holder.userNameTXT.text = if(name=="") singleCommentData.getJSONObject("user").getString("username") else name
        }catch (e : Exception){
            holder.userNameTXT.text = singleCommentData.getJSONObject("user").getString("username")
        }

        val postDate = singleCommentData.getString("created_at")

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        formatter.setLenient(false)
        val timeInMilliseconds = formatter.parse(postDate).time



        val timeSinceMILI = System.currentTimeMillis() - timeInMilliseconds
        val minutes = timeSinceMILI / 60000
        val hours = minutes / 60
        val days = hours / 24
        val months = days / 30
        val years = days / 365


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
        val commentProficePicIV : ImageView = itemView.findViewById(R.id.commentProficePic_iv)

    }


}