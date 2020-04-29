package cwm.mobileapps.episode

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

//template from https://github.com/kmvignesh/MyServiceExample/blob/master/app/src/main/java/com/example/vicky/myserviceexample/MyService.kt
class MyService : Service() {
    val TAG = "MyService"
    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private var channelId : String = ""
    private val description = "Test notification"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        ShowLog("onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ShowLog("onStartCommand")
        channelId = getString(R.string.package_name)
        createNotificationChannel()


        val runable = Runnable {
            for (i in 1..5) {
                ShowLog("Service doing something." + i.toString())
                Thread.sleep(1000)
                if (i==5){
                    buildAndShowNotification()
                }
            }
            stopSelf()
        }

        val thread = Thread(runable)
        thread.start()


        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        ShowLog("onDestroy")
        super.onDestroy()
    }

    fun ShowLog(message: String) {
        println("appdebug: myService: $message")
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
}
