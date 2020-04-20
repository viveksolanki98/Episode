package cwm.mobileapps.episode

import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

object FBDBhandler {
    private lateinit var database: DatabaseReference


    fun addRecord(episodeID : String, showID : String, userID : String){
        database = Firebase.database.reference
        var dbRecordRef = getRootRef().child(UUID.randomUUID().toString())
        dbRecordRef.child("UserID").setValue(userID)
        dbRecordRef.child("ShowID").setValue(showID)
        dbRecordRef.child("EpisodeID").setValue(episodeID)
        dbRecordRef.child("UserID_ShowID").setValue("${userID}_${showID}")
        dbRecordRef.child("UserID_EpisodeID").setValue("${userID}_${episodeID}")
        dbRecordRef.child("ShowID_EpisodeID").setValue("${showID}_${episodeID}")
    }

    fun query(recordKey : String, recordValue : String, callBack : (Any?) -> Unit) {
        database = Firebase.database.reference

        getRootRef().orderByChild(recordKey).equalTo(recordValue).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println("appdebug: In FBDB Query Handler: " + dataSnapshot.getValue())
                callBack(dataSnapshot.getValue())

            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("apperror: FBDB Error in query: $recordKey: $recordValue: ${databaseError.toException()}")
            }
        })

    }

    private fun getRootRef(): DatabaseReference {
        return database.child("EpisodeData")
    }
}