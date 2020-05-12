package cwm.mobileapps.episode

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri

//This class keeps all the uses of the content provider consistent. It helps reduce repetitive code in the activity related classes
class ContentProviderHandler() {
    var uri = Uri.parse("content://cwm.mobileapps.episode.PROVIDER")

    fun insert(contentResolver: ContentResolver,showID : String, episodeID : String){
        if (query(contentResolver, showID) == null){
            val cv = ContentValues()
            cv.put("showID",showID)
            cv.put("episodeID",episodeID)
            contentResolver.insert(uri,cv)
            println("appdebug: contentProviderHandler: insert: INSERTED")
        }else{
            println("appdebug: contentProviderHandler: insert: RECORD EXISTS")
        }

    }

    fun query(contentResolver: ContentResolver, showID : String?): ArrayList<NextEpisodeDBDataClass>? {

        //This If statement helps decide if a select all call is needed
        val queryCursor = if (showID ==null) {
            contentResolver.query(uri, null, null, null, null)
        }else{
            contentResolver.query(uri, null, "showID", arrayOf(showID), null)
        }
        var list : ArrayList<NextEpisodeDBDataClass>? = arrayListOf(NextEpisodeDBDataClass())
        //iterate through the results and put into a list
        if(queryCursor!!.moveToFirst()){
            list?.removeAt(0)
            do {
                val nextEpisodeDC = NextEpisodeDBDataClass()
                nextEpisodeDC.showID = queryCursor.getString(0)
                nextEpisodeDC.episodeID = queryCursor.getString(1)
                list?.add(nextEpisodeDC)
                println("appdebug: contentProviderHandler: QUERY: ${nextEpisodeDC.showID} ${nextEpisodeDC.episodeID}")
            }while (queryCursor.moveToNext())
        }else{
            list = null
            println("appdebug: contentProviderHandler: QUERY: NO RECORD EXISTS")
        }

        return list

    }

    fun update(contentResolver: ContentResolver,showID : String, episodeID : String): Int {
        val contentValues = ContentValues()
        contentValues.put("showID", showID)
        contentValues.put("episodeID", episodeID)
        val updatedRows = contentResolver.update(uri, contentValues, "showID", arrayOf(showID))

        println("appdebug: contentProviderHandler: UPDATE: $updatedRows")
        return updatedRows
    }

    fun safeInsert(contentResolver: ContentResolver,showID : String, episodeID : String): Int{
        //Instead of using insert in the main app classes, this is used to make sure no errors are thrown and no duplicate records are made.
        // It first checks if the record exists or not. If it doesn't then only it inserts.
        val res : Int

        if (query(contentResolver, showID) == null){
            insert(contentResolver, showID, episodeID)
            res =-1
            println("appdebug: contentProviderHandler: SAFEINSERT: inserted: done $showID $episodeID $res")
        }else{
            res = update(contentResolver, showID, episodeID)
            println("appdebug: contentProviderHandler: SAFEINSERT: updated: done $showID $episodeID $res")
        }

        return res
    }

    fun delete(contentResolver: ContentResolver, showID : String) : Int{
        val deletedNumber = contentResolver.delete(uri, "showID", arrayOf(showID))
        println("appdebug: contentProviderHandler: DELETE: $deletedNumber")
        return deletedNumber
    }
}