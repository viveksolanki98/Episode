package cwm.mobileapps.episode

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_my_account.*
import org.json.JSONArray
import org.json.JSONObject
import okhttp3.Response


class MyAccountFragment : Fragment() {
    private var mStorageRef: StorageReference? = null
    var userProfileImageIV : ImageView? = null
    var userID : String? = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_account, container, false)

        //Get user ID from shared preferences
        val myPref: SharedPreferences = this.activity!!.getSharedPreferences("Episode_pref", MODE_PRIVATE)
        userID = myPref.getString("user_id_google", "")

        //Connect to FireBase FireStore and get profile picture reference
        mStorageRef = FirebaseStorage.getInstance().getReference().child("UserProfilePics/$userID")

        //Assign fragment title
        val myAccountHeaddingTXT: TextView? = view?.findViewById(R.id.myAccountHeadding_txt)
        myAccountHeaddingTXT?.text = "My Account"

        val signOutBTN: Button? = view?.findViewById(R.id.signOut_btn)
        signOutBTN?.setOnClickListener{
            (activity as UserHomeActivity).signOut()
        }

        //On press of the user profile pic, open file chooser and upload image to firebase.
        userProfileImageIV = view?.findViewById(R.id.userProfileImage_iv)
        userProfileImageIV?.setOnClickListener{
            //Work out if we need to request permissions
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    println("appdebug: myAccount: storage permission has been revoked")
                    Toast.makeText(context, "Storage permission required to change profile picture.", Toast.LENGTH_LONG).show()
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1)
                } else {
                    println("appdebug: myAccount: storage permission has never been requested")
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1)
                }
            }else {
                // Permission is already granted
                FileChooser()
            }
        }

        //Load profile picture from storage if it exists
        mStorageRef?.downloadUrl?.addOnSuccessListener {
            println("appdebug: myAccount: image location: $it")
            Glide.with(view!!.context).load(it).into(userProfileImageIV!!)
        }

        //Work out how many minutes of T.V. has been watched
        setTotalTimeWatched()

        //If text view is pressed then refresh the figure
        timeWatchedCounter_txt?.setOnClickListener {
            setTotalTimeWatched()
        }

        //Open user manual
        val launchUserManualBTN : Button? = view.findViewById(R.id.launchUserManual_btn)
        launchUserManualBTN?.setOnClickListener {
            val intentToUserManualActivity = Intent(this.context, UserManualActivity::class.java)
            startActivity(intentToUserManualActivity)
        }

        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        println("appdebug: myAccount: onRequestPermissionsResult")
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted so proceed
                    FileChooser()
                    println("appdebug: myAccount: permission was granted")
                } else {
                    // permission denied, notify user why it is needed.
                    println("appdebug: myAccount: permission was denied")
                    Toast.makeText(context, "Storage permission required to change profile picture.", Toast.LENGTH_LONG).show()
                }
                return
            }
            else -> {
                println("appdebug: myAccount: this is another permission request")
                // Ignore all other requests.
            }
        }
    }

    private fun FileChooser(){
        //This function triggers the file chooser activity
        val intentToImageChooser = Intent(Intent.ACTION_GET_CONTENT)
        intentToImageChooser.type = "image/*"
        startActivityForResult(intentToImageChooser,1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //if result is from file chooser then proceed
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.data!=null){
            //get image location
            val imguri = data.data
            val uploadTask = mStorageRef?.putFile(imguri!!)

            //upload image
            uploadTask?.addOnFailureListener {
                // Handle unsuccessful uploads
                println("appdebug: myAccount: image upload FAIL")
                Toast.makeText(context, "Profile picture upload unsuccessful, try again.", Toast.LENGTH_LONG).show()
            }?.addOnSuccessListener {
                // if upload successful then change the profile picture.
                mStorageRef?.downloadUrl?.addOnSuccessListener {
                    println("appdebug: myAccount: image location: $it")
                    Glide.with(view!!.context).load(it).into(userProfileImageIV!!)
                }
            }
        }
    }

    private fun setTotalTimeWatched(){
        //This function works out how many minutes of T.V has been watched.
        var totalTimeWatched : Long = 0

        //Get all the shows that the user has added
        FBDBhandler.query("UserID_EpisodeID", "${userID}_tt1", fun(dataSnap : DataSnapshot?){
            //for each show...
            for(singleShowSnapshot in dataSnap!!.children){
                val userShowID = JSONObject(singleShowSnapshot.getValue().toString()).getString("ShowID")
                //skip blank show record
                if(userShowID != "tt1") {
                    //Get all show data from api
                    APIhandler.trackitAPIAsync("https://api.trakt.tv/search/trakt/$userShowID?extended=full", fun(apiData: Response) {
                        val showDataObj = JSONArray(apiData.body!!.string()).getJSONObject(0)
                        //get how long each episode is
                        val showRunTime = showDataObj.getJSONObject("show").getInt("runtime")
                        //get how many shows the user has watched
                        FBDBhandler.query("UserID_ShowID", "${userID}_$userShowID", fun(dataShowSnap: DataSnapshot?) {
                            //minus one to skip the blank episode record
                            val numberWatched = dataShowSnap!!.children.count() - 1
                            //number of episodes watched times by the length of an episode
                            totalTimeWatched += (numberWatched * showRunTime)
                            timeWatchedCounter_txt?.text = "You have spent ${totalTimeWatched} minutes watching T.V."
                        })
                    })
                }
            }
        })

    }

}
