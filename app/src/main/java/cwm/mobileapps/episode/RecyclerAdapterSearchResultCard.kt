package cwm.mobileapps.episode

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_show_tracking_sp.*
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.math.RoundingMode


class RecyclerAdapterSearchResultCard(val searchResultsArr : JSONArray) : RecyclerView.Adapter<RecyclerAdapterSearchResultCard.ViewHolder>() {

    override fun getItemCount() = searchResultsArr.length()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val showDataObj = searchResultsArr.getJSONObject(position).getJSONObject("show")
        val traktID = showDataObj.getJSONObject("ids").getString("trakt")
        if (!traktID.matches("\\d+".toRegex())){
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0,0,0,0)

            holder.searchResultContainerCV.layoutParams = layoutParams

            holder.searchResultContainerCV.visibility = View.GONE
            holder.searchResultContainerCV.layoutParams.height = 0
        }else {
            println("appdebug: rv search results card: position: $position: data: $showDataObj")
            holder.showTitleTXT.text = showDataObj.getString("title")
            holder.showDescriptionTXT.text = showDataObj.getString("overview")

            //var tvdbID = showDataObj.getJSONObject("ids").getString("tvdb")
            //var urlSTRImage = "http://webservice.fanart.tv/v3/tv/$tvdbID?api_key=cc52af8ac688a6c7a9a83e293624fe35"


            val showStartYear = showDataObj.getString("first_aired").split("-")[0]

            val showRating = showDataObj.getDouble("rating").toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
            val showStatus = showDataObj.getString("status").split(" ").joinToString(" ") { it.capitalize() }.trimEnd()
            holder.showDetailsTXT.text = "$showStartYear, ${showDataObj.getString("network")}, $showStatus, $showRating"

            Thread{
                val imageLocation = APIhandler.imageFromID(showDataObj.getJSONObject("ids"))
                (holder.itemView.context as Activity?)?.runOnUiThread{
                    Glide.with(holder.itemView.context as Activity).load(imageLocation)
                        .into(holder.showPosterIV)

                    holder.showPosterIV.setOnClickListener {
                        val intentToShowPageActivity =
                            Intent(holder.itemView.context, ShowPageActivity::class.java)
                        intentToShowPageActivity.putExtra(
                            "show_title",
                            showDataObj.getString("title")
                        )
                        intentToShowPageActivity.putExtra("show_id", traktID)
                        intentToShowPageActivity.putExtra("show_poster", imageLocation)

                        holder.itemView.context.startActivity(intentToShowPageActivity)
                    }
                }

            }.start()
        }
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
        val searchResultContainerCV : CardView = itemView.findViewById(R.id.searchResultContainer_cv)
    }


}