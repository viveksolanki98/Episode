package cwm.mobileapps.episode

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent

//This is the job service that triggers the other service that checks for new episodes to watch
class MyJobService : JobService() {
    var jobCancelled = false
    override fun onStopJob(params: JobParameters?): Boolean {
        println("appdebug: myJobService: onStopJob: job cancelled")
        jobCancelled = true
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        println("appdebug: myJobService: starting job")
        workingMethod(params)
        return true
    }

    private fun workingMethod(params: JobParameters?){
        startService()
        println("appdebug: myJobService: workingMethod: finished")
        jobFinished(params,false)
    }

    private fun startService(){
        val intent = Intent(this,MyService::class.java)
        intent.putExtra("triggerBy", "jobScheduler")
        startService(intent)
    }
}