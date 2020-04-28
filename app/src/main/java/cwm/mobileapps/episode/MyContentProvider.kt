package cwm.mobileapps.episode

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class MyContentProvider : ContentProvider() {

    private var dbHandler: DataBaseHandler? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("Implement this to handle requests to delete one or more rows")
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Implement this to handle requests to insert a new row.")
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
        val query = "SELECT * FROM NextEpisode WHERE $selection =  \"${selectionArgs!![0]}\""
        val result = db?.rawQuery(query,null)

        return result
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        TODO("Implement this to handle requests to update one or more rows.")
    }
}
