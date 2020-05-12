package cwm.mobileapps.episode

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.json.JSONObject
//This service checks if there are any newly aired episodes to watch from the users watch list
//template from: https://github.com/kmvignesh/MyServiceExample/blob/master/app/src/main/java/com/example/vicky/myserviceexample/MyService.kt
class MyService : Service() {
    lateinit var notificationManager : NotificationManager
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

        if (intent?.getStringExtra("triggerBy") == "alarm"){
            runOnAlarm()
        }

        if (intent?.getStringExtra("triggerBy") == "jobScheduler"){
            runOnAlarm()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun runOnAlarm(){
        channelId = getString(R.string.package_name)
        createNotificationChannel()
        var newEpisodesAvailable = false
        val runable = Runnable {
            val availableNewEpisodesList = ArrayList<String>()
            //get all the records in the database
            val result = ContentProviderHandler().query(contentResolver, null)
            if (result == null){
                ShowLog("database QUERY: NO RECORD EXISTS")
            }else {
                ShowLog("database QUERY: RECORDS EXIST")
                //for each record in the database...
                result.forEach {
                    //Get the latest aired episode from the api
                    val apiRes = APIhandler.trackitAPISync("https://api.trakt.tv/shows/${it.showID}/last_episode")
                    val latestEpisodeID = JSONObject(apiRes.body!!.string()).getJSONObject("ids").getString("trakt")
                    //if its not the same as in the database then there is a new episode to watch
                    if (it.episodeID != latestEpisodeID){
                        newEpisodesAvailable = true
                        availableNewEpisodesList.add(latestEpisodeID)
                    }
                    ShowLog("episode status for show ${it.showID} is ${it.episodeID != latestEpisodeID}")
                }
                //newEpisodesAvailable
                //If a new episode was found then trigger a notification
                if (newEpisodesAvailable){
                    buildAndShowNotification("New Episodes", "Check your watch list, there are new episodes available to watch.")
                }
            }
            stopSelf()
        }

        val thread = Thread(runable)
        thread.start()
    }

    override fun onDestroy() {
        ShowLog("onDestroy")
        super.onDestroy()
    }

    fun ShowLog(message: String) {
        println("appdebug: myService: $message")
    }

    private fun createNotificationChannel() {
        //This function builds the notification framework
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

    private fun buildAndShowNotification(title: String, message : String){
        //This function applies configuration settings to the notification and adds the message. Then launches it.
        val intent = Intent(this, UserHomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(1234, builder.build())
        }

    }
}
