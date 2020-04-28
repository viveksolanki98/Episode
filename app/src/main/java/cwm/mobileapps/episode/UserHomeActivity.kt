package cwm.mobileapps.episode

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
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


        var insertDataClass = NextEpisodeDBDataClass("tt1234", "tt6789")
        var db = DataBaseHandler(this)
        db.insertData(insertDataClass)

        var data = db.getNextEpisode("tt1234")
        for (i in 0..(data.size - 1)) {
            println("appdebug: userHome: database results: ${data.get(i).showID} ${data.get(i).episodeID}")
        }

        db.updateData("tt1234", "tt4455")
        println("appdebug: userHome: database update")

        var data2 = db.getNextEpisode("tt1234")
        println("appdebug: userHome: data 2 size: ${data2.size}")
        for (i in 0..(data2.size - 1)) {
            println("appdebug: userHome: database results 2: ${data2.get(i).showID} ${data2.get(i).episodeID}")
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
                val myLogInPref: SharedPreferences = getSharedPreferences("LogInStatusPref", Context.MODE_PRIVATE)
                myLogInPref.edit().putBoolean("login", false)
                myLogInPref.edit().apply()
                val intentToMainActivity = Intent(this, MainActivity::class.java)
                startActivity(intentToMainActivity)
            }
    }

}
