package cwm.mobileapps.episode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapterWLNextEpisode(val episodeIDs : ArrayList<String>) : RecyclerView.Adapter<RecyclerAdapterWLNextEpisode.ViewHolder>() {

    override fun getItemCount() = episodeIDs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.showTitleTXT.text = episodeIDs[position]


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ViewHolder{
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.watch_list_next_episode_rv_card, parent, false)
        return ViewHolder(view)

    }

    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val showTitleTXT : TextView = itemView.findViewById(R.id.showTitle_txt)
    }


}