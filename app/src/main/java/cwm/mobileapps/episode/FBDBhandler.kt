package cwm.mobileapps.episode

import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

//This object helps keep all firebase related tasks consistent. It also cuts down on a lot of repetitive code.
object FBDBhandler {
    lateinit var database: DatabaseReference

    fun addRecord(episodeID : String, showID : String, userID : String){
        //If an episode it being inserted without the show initializer present then it will also get inserted
        if ((episodeID != "tt1") and (showID != "tt1")){
            query("UserID_ShowID", "${userID}_${showID}", fun(data : DataSnapshot?){
                if (data?.getValue() == null){
                    //If no show exists then add a blank episode for that show
                    addRecord("tt1", showID, userID)
                }
            })
        }
        database = Firebase.database.reference
        val dbRecordRef = getRootRef().child(UUID.randomUUID().toString())
        dbRecordRef.child("UserID").setValue(userID)
        dbRecordRef.child("ShowID").setValue(showID)
        dbRecordRef.child("EpisodeID").setValue(episodeID)
        dbRecordRef.child("UserID_ShowID").setValue("${userID}_${showID}")
        dbRecordRef.child("UserID_EpisodeID").setValue("${userID}_${episodeID}")
        dbRecordRef.child("ShowID_EpisodeID").setValue("${showID}_${episodeID}")
    }

    fun query(recordKey : String, recordValue : String, callBack : (DataSnapshot?) -> Unit) {
        database = Firebase.database.reference

        getRootRef().orderByChild(recordKey).equalTo(recordValue).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println("appdebug: In FBDB Query Handler: " + dataSnapshot.getValue())
                callBack(dataSnapshot)

            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("apperror: FBDB Error in query: $recordKey: $recordValue: ${databaseError.toException()}")
            }
        })

    }

    fun queryListener(recordKey : String, recordValue : String, callBack : (DataSnapshot?) -> Unit) {
        database = Firebase.database.reference

        getRootRef().orderByChild(recordKey).equalTo(recordValue).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println("appdebug: In FBDB Query Handler: " + dataSnapshot.getValue())
                callBack(dataSnapshot)

            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("apperror: FBDB Error in queryListener: $recordKey: $recordValue: ${databaseError.toException()}")
            }
        })

    }

    fun deleteRecord(recordKey: String, recordValue: String, successCallback: () -> Unit, failCallback: () -> Unit){
        query(recordKey, recordValue,
        fun(data : DataSnapshot?){
            val querySize  = data?.childrenCount?.toInt()
            var count = 0
            var successTracker = true
            //This for loop makes sure that all instances of the record is deleted
            for (singleSnapshot in data!!.getChildren()) {
                count++
                if(count == querySize){
                    singleSnapshot.ref.removeValue().addOnSuccessListener {
                        if (successTracker) {
                            successCallback()
                        } else{
                            failCallback()
                        }
                    }.addOnFailureListener{
                        failCallback()
                    }
                }else {
                    singleSnapshot.ref.removeValue().addOnSuccessListener {

                    }.addOnFailureListener {
                        successTracker = false
                    }
                }
            }
        })
    }

    private fun getRootRef(): DatabaseReference {
        return database.child("EpisodeData")
    }
}
