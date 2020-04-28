package cwm.mobileapps.episode

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri


class ContentProviderHandler() {
    var uri = Uri.parse("content://cwm.mobileapps.episode.PROVIDER")

    fun insert(contentResolver: ContentResolver,showID : String, episodeID : String){
        var cv = ContentValues()
        cv.put("showID",showID)
        cv.put("episodeID",episodeID)
        contentResolver.insert(uri,cv)
        println("appdebug: contentProviderHandler: INSERTED")
    }

    fun query(contentResolver: ContentResolver, showID : String): ArrayList<NextEpisodeDBDataClass>? {
        var queryCursor = contentResolver.query(uri, null, "showID", arrayOf(showID), null)
        var list : ArrayList<NextEpisodeDBDataClass>? = arrayListOf(NextEpisodeDBDataClass())
        if(queryCursor!!.moveToFirst()){
            list?.removeAt(0)
            do {
                var nextEpisodeDC = NextEpisodeDBDataClass()
                nextEpisodeDC.showID = queryCursor.getString(0)
                nextEpisodeDC.episodeID = queryCursor.getString(1)
                list?.add(nextEpisodeDC)
            }while (queryCursor.moveToNext())
            println("appdebug: contentProviderHandler: QUERY: ${list?.get(0)?.showID} ${list?.get(0)?.episodeID}")
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

    fun delete(contentResolver: ContentResolver, showID : String) : Int{
        var deletedNumber = contentResolver.delete(uri, "showID", arrayOf(showID))
        println("appdebug: contentProviderHandler: DELETE: $deletedNumber")
        return deletedNumber
    }
}