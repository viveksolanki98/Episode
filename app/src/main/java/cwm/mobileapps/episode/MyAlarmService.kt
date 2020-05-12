package cwm.mobileapps.episode

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.telephony.ServiceState
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

//this a service is used to create an alarm to trigger the notification check
class MyAlarmService : Service() {
    lateinit var alarmManager: AlarmManager

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        ShowLog("onCreate")

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ShowLog("onStartCommand")

        //For Testing::
        //startService()

        createAlarm()
        ShowLog("Created alarm from service")
        return  START_STICKY
    }

    override fun onDestroy() {
        ShowLog("onDestroy")

        super.onDestroy()
    }

    private fun createAlarm(){
        //https://github.com/kmvignesh/AlarmManagerExample/blob/master/app/src/main/java/com/example/vicky/alarmmanagerexample/MainActivity.kt
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intentAlarm = Intent(this, MyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT)
        println("appdebug: myAlarmService: Create Alarm: ${Date()} TRIGGER")

        //From android documentation, to start alarm at specific time each day
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 15)
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun ShowLog(message: String) {
        println("appdebug: myAlarmService: $message")
    }

    //To trigger checking of new episodes on launch for testing
    fun startService(){
        val intent = Intent(this,MyService::class.java)
        intent.putExtra("triggerBy", "alarm")
        startService(intent)
    }

}
