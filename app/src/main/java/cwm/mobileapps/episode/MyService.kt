package cwm.mobileapps.episode

import android.app.Service
import android.content.Intent
import android.os.IBinder
//template from https://github.com/kmvignesh/MyServiceExample/blob/master/app/src/main/java/com/example/vicky/myserviceexample/MyService.kt
class MyService : Service() {

    val TAG = "MyService"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        ShowLog("onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ShowLog("onStartCommand")

        val runable = Runnable {
            for (i in 1..10) {
                ShowLog("Service doing something." + i.toString())
                Thread.sleep(1000)
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
}
