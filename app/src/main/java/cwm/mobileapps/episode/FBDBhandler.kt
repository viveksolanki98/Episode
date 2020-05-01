package cwm.mobileapps.episode

import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*


object FBDBhandler {
    lateinit var database: DatabaseReference

    fun addRecord(episodeID : String, showID : String, userID : String){
        if ((episodeID != "tt1") and (showID != "tt1")){
            query("UserID_ShowID", "${userID}_${showID}", fun(data : DataSnapshot?){
                if (data?.getValue() == null){
                 addRecord("tt1", showID, userID)
                }
            })
        }
        database = Firebase.database.reference
        var dbRecordRef = getRootRef().child(UUID.randomUUID().toString())
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

    fun queryChildListener(recordKey : String, recordValue : String, changeCallback : (DataSnapshot?) -> Unit, addedCallback : (DataSnapshot?) -> Unit, removedCallback : (DataSnapshot?) -> Unit) {
        database = Firebase.database.reference

        getRootRef().orderByChild(recordKey).equalTo(recordValue).addChildEventListener(object : ChildEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
                println("apperror: FBDB Error in queryChildListener: $recordKey: $recordValue: ${databaseError.toException()}")
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                println("appdebug: In FBDB Query Handler onChildChanged: " + dataSnapshot.getValue())
                changeCallback(dataSnapshot)
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                println("appdebug: In FBDB Query Handler onChildAdded: " + dataSnapshot.getValue())
                addedCallback(dataSnapshot)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                println("appdebug: In FBDB Query Handler onChildRemoved: " + dataSnapshot.getValue())
                removedCallback(dataSnapshot)
            }

        })

    }

    fun deleteRecord(recordKey: String, recordValue: String, successCallback: () -> Unit, failCallback: () -> Unit){
        query(recordKey, recordValue,
        fun(data : DataSnapshot?){
            val querySize  = data?.childrenCount?.toInt()
            var count = 0
            var successTracker = true
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
