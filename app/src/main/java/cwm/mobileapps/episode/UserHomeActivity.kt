package cwm.mobileapps.episode

import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.drm.DrmStore.Playback.START
import android.drm.DrmStore.Playback.STOP
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.insert
import android.speech.tts.TextToSpeech.STOPPED
import android.support.v4.media.session.PlaybackStateCompat
import android.telephony.ServiceState
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_user_home2.*

class UserHomeActivity : AppCompatActivity() {
    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home2)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("700140263399-6u7use2ta07uanlm80d23hg3eokvrkpb.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        val adapter = viewPagerAdapter(supportFragmentManager)
        adapter.addFragment(WatchListFragment(), " Watch List ")
        adapter.addFragment(DiscoverAndSearchFragment(), " D & S")
        adapter.addFragment(MyAccountFragment(), " Watch List ")
        userHome_vp.adapter = adapter

        //CONTENT PROVIDER EXAMPLES:
        /*
        //INSERT--------------------------------
        ContentProviderHandler().insert(contentResolver, "ttHELLO", "ttTHERE")
        println("appdebug: userHome: database results with CP Handler: INSERTED")
        //--------------------------------------

        //UPDATE--------------------------------
        var updateRes = ContentProviderHandler().update(contentResolver, "ttHELLO", "ttBYEEE")
        println("appdebug: userHome: database UPDATE with CP Handler: $updateRes")
        //--------------------------------------

        //DELETE--------------------------------
        var numberOfDeleted = ContentProviderHandler().delete(contentResolver, "ttHELLO")
        println("appdebug: userHome: database DELETE with CP Handler: $numberOfDeleted")
        //--------------------------------------

        //QUERY--------------------------------
        var result = ContentProviderHandler().query(contentResolver, "ttHELLO")
        if (result == null){
            println("appdebug: userHome: database QUERY with CP Handler 3: NO RECORD EXISTS")
        }else {
            println("appdebug: userHome: database QUERY with CP Handler 3: ${result.get(0).showID} ${result.get(0).episodeID}")
        }
        //-------------------------------------
        */

        val intent = Intent(this,MyAlarmService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            println("appdebug: userHome: Starting the service in >=26 Mode")
            //startForegroundService(intent)
            startService(intent)
        }else{
            println("appdebug: userHome: Starting the service in < 26 Mode")
            startService(intent)
        }
    }



    override fun onStart() {
        super.onStart()

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null){
            val intentToMainActivity = Intent(this, MainActivity::class.java)
            startActivity(intentToMainActivity)
        }
    }

    fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                val intentToMainActivity = Intent(this, MainActivity::class.java)
                startActivity(intentToMainActivity)
            }
    }

}
