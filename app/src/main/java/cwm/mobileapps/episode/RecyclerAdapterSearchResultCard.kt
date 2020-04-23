package cwm.mobileapps.episode

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.DataSnapshot
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class RecyclerAdapterSearchResultCard(val searchResultsArr : JSONArray) : RecyclerView.Adapter<RecyclerAdapterSearchResultCard.ViewHolder>() {

    override fun getItemCount() = searchResultsArr.length()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var showDataObj = searchResultsArr.getJSONObject(position).getJSONObject("show")
        holder.showTitleTXT.text = showDataObj.getString("title")
        holder.showDescriptionTXT.text = showDataObj.getString("overview")
        println("appdebug: rv search results card: position: $position: data: ${showDataObj.toString()}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ViewHolder{
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.search_result_rv_card, parent, false)
        return ViewHolder(view)

    }

    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val showTitleTXT : TextView = itemView.findViewById(R.id.showTitleInSearchCard_txt)
        val showDescriptionTXT : TextView = itemView.findViewById(R.id.showDescriptionInSearchCard_txt)
        val showDetailsTXT : TextView = itemView.findViewById(R.id.showDetailsInSearchCard_txt)
        val showPosterIV : ImageView = itemView.findViewById(R.id.showPosterInSearchCard_iv)
    }


}