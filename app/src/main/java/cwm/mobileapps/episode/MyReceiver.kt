package cwm.mobileapps.episode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast
import java.util.*

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        println("appdebug: myReceiver: A broadcast triggered the class"  + Date().toString())
        val intent = Intent(context,MyService::class.java)
        intent.putExtra("triggerBy", "alarm")
        context.startService(intent)
    }
}
