package cwm.mobileapps.episode

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    private lateinit var database: DatabaseReference

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



        //TEST FIREBASE DB----------
        database = Firebase.database.reference
        database.child("UserData").child("123123").child("shows").child("4567").setValue("Billions")
        //--------------------------

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

    fun getGoogleAccountInfo(): GoogleSignInAccount? {
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            /*
            val personName = acct.displayName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            val personPhoto: Uri? = acct.photoUrl
             */
            return acct
        }
        return null
    }

}
