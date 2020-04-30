package cwm.mobileapps.episode

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri


class ContentProviderHandler() {
    var uri = Uri.parse("content://cwm.mobileapps.episode.PROVIDER")

    fun insert(contentResolver: ContentResolver,showID : String, episodeID : String){
        if (query(contentResolver, showID) == null){
            var cv = ContentValues()
            cv.put("showID",showID)
            cv.put("episodeID",episodeID)
            contentResolver.insert(uri,cv)
            println("appdebug: contentProviderHandler: insert: INSERTED")
        }else{
            println("appdebug: contentProviderHandler: insert: RECORD EXISTS")
        }

    }

    fun query(contentResolver: ContentResolver, showID : String?): ArrayList<NextEpisodeDBDataClass>? {

        var queryCursor = if (showID ==null) {
            contentResolver.query(uri, null, null, null, null)
        }else{
            contentResolver.query(uri, null, "showID", arrayOf(showID), null)
        }
        var list : ArrayList<NextEpisodeDBDataClass>? = arrayListOf(NextEpisodeDBDataClass())
        if(queryCursor!!.moveToFirst()){
            list?.removeAt(0)
            do {
                var nextEpisodeDC = NextEpisodeDBDataClass()
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
        var updatedRows = contentResolver.update(uri, contentValues, "showID", arrayOf(showID))

        println("appdebug: contentProviderHandler: UPDATE: $updatedRows")
        return updatedRows
    }

    fun safeInsert(contentResolver: ContentResolver,showID : String, episodeID : String): Int{
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
        var deletedNumber = contentResolver.delete(uri, "showID", arrayOf(showID))
        println("appdebug: contentProviderHandler: DELETE: $deletedNumber")
        return deletedNumber
    }
}