package cwm.mobileapps.episode

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.FirebaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RecyclerAdapterDSLists(val showTitle : ArrayList<String>, val showIDs : ArrayList<String>, val showImageLocations : ArrayList<String>) : RecyclerView.Adapter<RecyclerAdapterDSLists.ViewHolder>() {
    private lateinit var database: DatabaseReference

    override fun getItemCount() = showTitle.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userAccountDetails= GoogleSignIn.getLastSignedInAccount(holder.itemView.context)
        database = Firebase.database.reference

        val dbRootRef = database.child("EpisodeData")

        holder.showTitleTXT.text = showTitle[position]
        //"https://image.tmdb.org/t/p/w500/"
        Glide.with(holder.itemView.context).load(showImageLocations[position]).into(holder.showPosterIV)
        fun call(){

        }
        FBDBhandler.query("UserID_ShowID", "${(userAccountDetails?.id).toString()}_${showIDs[position]}", fun(data : Any?) {
            println("appdebug: showExistsNEW: " + data)
            holder.addShowBTN.visibility = if (data != null) View.GONE else View.VISIBLE
        })

        holder.showPosterIV.setOnClickListener {
            val intentToShowPageActivity =
                Intent(holder.itemView.context, ShowPageActivity::class.java)
            intentToShowPageActivity.putExtra("show_title", showTitle[position])
            intentToShowPageActivity.putExtra("show_id", showIDs[position])
            intentToShowPageActivity.putExtra("show_poster", showImageLocations[position])

            holder.itemView.context.startActivity(intentToShowPageActivity)
        }

        holder.addShowBTN.setOnClickListener {
            //database.child("UserData").child((userAccountDetails?.id).toString()).child("shows").child(showIDs[position]).setValue(showTitle[position])
            FBDBhandler.addRecord("tt1", showIDs[position], (userAccountDetails?.id)!!.toString())
            holder.addShowBTN.visibility = View.GONE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ViewHolder{
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.discover_lists_rv_card, parent, false)
        return ViewHolder(view)

    }

    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val showTitleTXT: TextView = itemView.findViewById(R.id.showTitle_txt)
        val showPosterIV: ImageView = itemView.findViewById(R.id.showPoster_iv)
        val addShowBTN : Button = itemView.findViewById(R.id.addShow_btn)
    }
}