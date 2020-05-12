package cwm.mobileapps.episode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_user_manual.*
//This activity is to display the user manual
class UserManualActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_manual)

        //Get user manual directory
        userManual_wv.loadUrl("file:///android_asset/user_manual/index.html")
        userManual_wv.settings.javaScriptEnabled = true

        back_btn.setOnClickListener {
            onBackPressed()
        }
    }
}
