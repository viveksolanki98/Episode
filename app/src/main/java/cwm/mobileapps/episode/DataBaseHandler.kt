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

//This class creates the SQL database. I use this database to keep track of the next episode for each show in the watch list. It is used to decide if a notification needs triggering
class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null,1){
    override fun onCreate(db: SQLiteDatabase?) {

        val createTable = "CREATE TABLE " + TABLE_NAME +" (" +
                COL_SHOW + " VARCHAR(256)," +
                COL_EPISODE + " VARCHAR(256))"

        db?.execSQL(createTable)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}