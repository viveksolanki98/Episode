package cwm.mobileapps.episode

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
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
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    println("appdebug: myAccount: storage permission has been revoked")
                    Toast.makeText(context, "Storage permission required to change profile picture.", Toast.LENGTH_LONG).show()
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1);
                } else {
                    println("appdebug: myAccount: storage permission has never been requested")
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1);
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

        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        println("appdebug: myAccount: onRequestPermissionsResult")
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay!
                    FileChooser()
                    println("appdebug: myAccount: permission was granted")
                } else {
                    // permission denied,
                    println("appdebug: myAccount: permission was denied")
                    Toast.makeText(context, "Storage permission required to change profile picture.", Toast.LENGTH_LONG).show()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                println("appdebug: myAccount: this is another permission request")
                // Ignore all other requests.
            }
        }
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

            uploadTask?.addOnFailureListener {
                // Handle unsuccessful uploads
                println("appdebug: myAccount: image upload FAIL")
                Toast.makeText(context, "Profile picture upload unsuccessful, try again.", Toast.LENGTH_LONG).show()
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
