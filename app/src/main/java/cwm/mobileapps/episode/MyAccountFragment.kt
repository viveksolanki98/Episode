package cwm.mobileapps.episode

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MyAccountFragment : Fragment() {
    private var mStorageRef: StorageReference? = null
    var userProfileImageIV : ImageView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        val view = inflater?.inflate(R.layout.fragment_my_account, container, false)

        mStorageRef = FirebaseStorage.getInstance().getReference()?.child("UserProfilePics/${GoogleSignIn.getLastSignedInAccount(context)?.id}")


        val myAccountHeaddingTXT: TextView? = view?.findViewById(R.id.myAccountHeadding_txt)
        myAccountHeaddingTXT?.text = "My Account"

        val signOutBTN: Button? = view?.findViewById(R.id.signOut_btn)
        signOutBTN?.setOnClickListener{
            (activity as UserHomeActivity).signOut()
        }

        userProfileImageIV = view?.findViewById(R.id.userProfileImage_iv)
        userProfileImageIV?.setOnClickListener{
            println("appdebug: myAccount: in click listener")
            FileChooser()
        }

        //Load profile picture from storage if it exists
        mStorageRef?.downloadUrl?.addOnSuccessListener {
            println("appdebug: myAccount: image location: $it")
            Glide.with(view!!.context).load(it).into(userProfileImageIV!!)
        }

        return view
    }

    private fun FileChooser(){
        val intentToImageChooser = Intent(Intent.ACTION_GET_CONTENT)
        intentToImageChooser.type = "image/*"
        startActivityForResult(intentToImageChooser,1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.data!=null){
            val imguri = data.data
            println("appdebug: myAccount: image uri: ${imguri?.lastPathSegment}")
            var uploadTask = mStorageRef?.putFile(imguri!!)

// Listen for state changes, errors, and completion of the upload.
            uploadTask?.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                println("Upload is $progress% done")
            }?.addOnPausedListener {
                println("Upload is paused")
            }?.addOnFailureListener {
                // Handle unsuccessful uploads
            }?.addOnSuccessListener {
                // Handle successful uploads on complete
                mStorageRef?.downloadUrl?.addOnSuccessListener {
                    println("appdebug: myAccount: image location: $it")
                    Glide.with(view!!.context).load(it).into(userProfileImageIV!!)
                }
            }
        }
    }

}
