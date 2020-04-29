package cwm.mobileapps.episode


import android.R.attr.key
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    //Google sign in code from: https://johncodeos.com/how-to-add-google-login-button-to-your-android-app-using-kotlin/
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private var channelId : String = ""
    private val description = "Test notification"

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("700140263399-6u7use2ta07uanlm80d23hg3eokvrkpb.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        sign_in_button.setOnClickListener {
            signIn()
        }

        //NOTIFICATION TEST-----------------------
        channelId = getString(R.string.package_name)
        //createNotificationChannel()
        //buildAndShowNotification()
        //-----------------------------------------

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "An app to track watched T.V. shows and discover new ones. Episode"
            val channel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = descriptionText
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
            }

            // Register the channel with the system
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildAndShowNotification(){
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, UserHomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("My notification")
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1234, builder.build())
        }
    }

    override fun onStart() {
        super.onStart()

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null){
            putSharedPrefUserID((account.id)!!.toString())
            launchUser()
        }
    }

    private fun signIn(){
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun launchUser(){
        val intentToUserHomeActivity = Intent(this, UserHomeActivity::class.java)
        startActivity(intentToUserHomeActivity)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                putSharedPrefUserID((account.id)!!.toString())

                FBDBhandler.query("UserID_ShowID", (account.id)!!.toString() + "_tt1", fun(data:DataSnapshot?){
                    if (data?.getValue() == null) {
                        println("appdebug: Sign In: newUserAdded: " +  data?.getValue())
                        FBDBhandler.addRecord("tt1", "tt1", (account.id)!!.toString())
                    }else{println("appdebug: Sign In: existingUser: " + data?.getValue())}
                })
            }
            // Signed in successfully
            launchUser()

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }

    private fun putSharedPrefUserID(googleUserID : String){
        val myPref = getSharedPreferences("Episode_pref", Context.MODE_PRIVATE)
        val myEditor = myPref.edit()
        myEditor.clear()
        myEditor.putString("user_id_google", googleUserID)
        myEditor.apply()
    }
}
