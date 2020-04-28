package cwm.mobileapps.episode

import android.app.PendingIntent.getActivity
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.widget.Toast

class MyContentProvider : ContentProvider() {
    val DATABASE_NAME = "MyDB"
    val TABLE_NAME = "NextEpisode"

    private var dbHandler: DataBaseHandler? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHandler?.writableDatabase
        var numDeleted : Int?
        numDeleted = db?.delete(TABLE_NAME, selection + "=?", selectionArgs)

        return numDeleted!!
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHandler?.writableDatabase

        db?.insert(TABLE_NAME, null, values)

        return uri
    }

    override fun onCreate(): Boolean {
        dbHandler = DataBaseHandler(getContext()!!)

        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        //projection is columns to select
        //selection is column in where clause
        //selectionArgs is data  in where clause (right side of equals)

        val db = dbHandler?.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $selection =  \"${selectionArgs!![0]}\""
        val result = db?.rawQuery(query,null)

        return result
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        //selection is column in where clause
        //selectionArgs is data  in where clause (right side of equals)
        val updatedRows : Int?
        val db = dbHandler?.writableDatabase

        updatedRows = db?.update(TABLE_NAME, values, "$selection = ?", selectionArgs)

        return updatedRows!!
    }
}
