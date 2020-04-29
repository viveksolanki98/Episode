//https://github.com/kmvignesh/SqliteExample/blob/master/app/src/main/java/com/example/vicky/sqliteexample/DataBaseHandler.kt
package cwm.mobileapps.episode

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

val DATABASE_NAME = "MyDB"
val TABLE_NAME = "NextEpisode"
val COL_SHOW = "showID"
val COL_EPISODE = "episodeID"

class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null,1){
    override fun onCreate(db: SQLiteDatabase?) {

        val createTable = "CREATE TABLE " + TABLE_NAME +" (" +
                COL_SHOW + " VARCHAR(256) PRIMARY KEY," +
                COL_EPISODE + " VARCHAR(256))"

        db?.execSQL(createTable)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insertData(dataClass : NextEpisodeDBDataClass){
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_SHOW,dataClass.showID)
        cv.put(COL_EPISODE,dataClass.episodeID)
        var result = db.insert(TABLE_NAME,null,cv)
        if(result == -1.toLong())
            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context,"Success", Toast.LENGTH_SHORT).show()
    }

    fun getNextEpisode(showID : String) : MutableList<NextEpisodeDBDataClass>{
        var list : MutableList<NextEpisodeDBDataClass> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COL_SHOW =  \"$showID\""
        val result = db.rawQuery(query,null)

        if(result.moveToFirst()){
            do {
                var nextEpisodeDC = NextEpisodeDBDataClass()
                nextEpisodeDC.showID = result.getString(result.getColumnIndex(COL_SHOW))
                nextEpisodeDC.episodeID = result.getString(result.getColumnIndex(COL_EPISODE))
                list.add(nextEpisodeDC)
            }while (result.moveToNext())
        }

        result.close()
        db.close()
        return list
    }

    fun deleteShow(showID : String){
        val db = this.writableDatabase
        db.delete(TABLE_NAME, COL_SHOW + "=?", arrayOf(showID))
        db.close()
    }

    fun updateData(showID : String, episodeID : String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_SHOW, showID)
        contentValues.put(COL_EPISODE, episodeID)
        db.update(TABLE_NAME, contentValues, "$COL_SHOW = ?", arrayOf(showID))
        db.close()
    }
}