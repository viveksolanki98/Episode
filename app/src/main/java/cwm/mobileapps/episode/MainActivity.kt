package cwm.mobileapps.episode


import android.R.attr.key
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

//This is the sign in activity of the app
class MainActivity : AppCompatActivity() {
    //Google sign in code from: https://johncodeos.com/how-to-add-google-login-button-to-your-android-app-using-kotlin/
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    val USERHOME_RQ = 1200



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialize google sign in objects and variables
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("700140263399-6u7use2ta07uanlm80d23hg3eokvrkpb.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        sign_in_button.setOnClickListener {
            signIn()
        }

        //Launch manual if button pressed
        launchManual_btn.setOnClickListener {
            val intentToUserManualActivity = Intent(this, UserManualActivity::class.java)
            startActivity(intentToUserManualActivity)
        }


    }

    override fun onStart() {
        super.onStart()

        // Check for existing Google Sign In account, if the user is already signed in. If so then go straight to the user home
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null){
            putSharedPrefUserID((account.id)!!.toString())
            launchUser()
        }
    }

    private fun signIn(){
        //Start google sign in process
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //If main activity triggered from the google sign in process the continue the sign in
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

        //If main activity has been triggered from the user home by the back button then close app
        if(requestCode == USERHOME_RQ){
            finish()
        }
    }

    private fun launchUser(){
        val intentToUserHomeActivity = Intent(this, UserHomeActivity::class.java)
        startActivityForResult(intentToUserHomeActivity, USERHOME_RQ)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        //This function adds the user to the database if the sign in was successful
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                putSharedPrefUserID((account.id)!!.toString())

                //Query checks if the user exists. If not then they are added to the FBDB
                FBDBhandler.query("UserID_ShowID", (account.id)!!.toString() + "_tt1", fun(data:DataSnapshot?){
                    if (data?.getValue() == null) {
                        println("appdebug: Sign In: newUserAdded: " +  data?.getValue())
                        FBDBhandler.addRecord("tt1", "tt1", (account.id)!!.toString())
                    }else{println("appdebug: Sign In: existingUser: " + data.getValue())}
                })
            }
            // Once existing user is checked, then move to user home
            launchUser()

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }

    private fun putSharedPrefUserID(googleUserID : String){
        //Create a shared preference for the google user id so that it can be used in database functions.
        val myPref = getSharedPreferences("Episode_pref", Context.MODE_PRIVATE)
        val myEditor = myPref.edit()
        myEditor.clear()
        myEditor.putString("user_id_google", googleUserID)
        myEditor.apply()
    }
}
