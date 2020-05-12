package cwm.mobileapps.episode

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.activity_user_home2.*


class UserHomeActivity : AppCompatActivity() {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    var userID : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home2)
        //Get User Id
        val myPref: SharedPreferences = getSharedPreferences("Episode_pref", Context.MODE_PRIVATE)
        userID = myPref.getString("user_id_google", "")

        //Define google sign in object ready for sign put trigger
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("700140263399-6u7use2ta07uanlm80d23hg3eokvrkpb.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        //Define view pager and set fragments
        val adapter = viewPagerAdapter(supportFragmentManager)
        adapter.addFragment(WatchListFragment(), " Watch List ")
        adapter.addFragment(DiscoverAndSearchFragment(), " D & S")
        adapter.addFragment(MyAccountFragment(), " Watch List ")
        userHome_vp.adapter = adapter

        //add tabs to the tab layout
        vpTab_tl.addTab(vpTab_tl.newTab().setText("Watch List"))
        vpTab_tl.addTab(vpTab_tl.newTab().setText("Discover"))
        vpTab_tl.addTab(vpTab_tl.newTab().setText("My Account"))

        //On tab selected, change visible fragment to selected one
        vpTab_tl.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab) {
                userHome_vp.currentItem = tab.position
            }
        })
        //On fragment changed by swiping, change the selected tab in the tab layout
        userHome_vp.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                val tab = vpTab_tl.getTabAt(position)
                tab?.select()
            }
        })


        FBDBhandler.queryListener("UserID_EpisodeID", "${userID}_tt1", fun(data : DataSnapshot?){
            if(data!!.childrenCount.toInt() <= 1){
                userHome_vp.currentItem = 1
            }
        })




        //JOB SCHEDULER DEFINITION----------------
        val componentName = ComponentName(this, MyJobService::class.java)
        val info = JobInfo.Builder(123, componentName)
            //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .setPersisted(true)
            .setPeriodic(15 *60 * 1000)
            .build()
        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val resCode = scheduler.schedule(info)
        if (resCode == JobScheduler.RESULT_SUCCESS){
            println("appdebug: userHome: Job Scheduled ")
        }else{
            println("appdebug: userHome: Job NOT Scheduled ")
        }

        //-------------------------------------
        //Start the service to define the alarm
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
        //Sign the user out of the app
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                val intentToMainActivity = Intent(this, MainActivity::class.java)
                startActivity(intentToMainActivity)
            }
    }

}
