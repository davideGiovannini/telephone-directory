package com.demiurgo.telephonedirectory.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.demiurgo.telephonedirectory.model.Entry
import org.jetbrains.anko.db.*

/**
 * Created by demiurgo on 4/14/16.
 */

const val ENTRIES_TABLE = "entries"
const val ROW_ID = "_id"
const val ROW_FIRST_NAME = "firstName"
const val ROW_LAST_NAME = "lastName"
const val ROW_PHONE = "phoneNumber"

class DatabaseManager(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "TelephoneDirectoryDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.i(TAG, "Creating Database")
        db.createTable(ENTRIES_TABLE, true, // IF_NOT_EXISTS
                ROW_ID to INTEGER + PRIMARY_KEY + UNIQUE,
                ROW_FIRST_NAME to TEXT + NOT_NULL,
                ROW_LAST_NAME to TEXT + NOT_NULL,
                ROW_PHONE to TEXT + NOT_NULL
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, v1: Int, v2: Int) {
        //TODO improve and handle db migration
        db.dropTable(ENTRIES_TABLE)
        onCreate(db)
    }


    companion object {
        const val TAG = "DatabaseManager"


        private var instance: DatabaseManager? = null

        fun getInstance(ctx: Context): DatabaseManager {
            if (instance == null) {
                instance = DatabaseManager(ctx.applicationContext)
            }
            return instance!!
        }
    }
}

// Access property for Context
val Context.database: DatabaseManager
    get() = DatabaseManager.getInstance(applicationContext)


fun SQLiteDatabase.insertEntry(entry: Entry) {
    if (!entry.isValid()) {
        throw Exception("Attempting to insert invalid entry in db! $entry")
    }

    insert(ENTRIES_TABLE,
            ROW_FIRST_NAME to entry.firstName,
            ROW_LAST_NAME to entry.lastName,
            ROW_PHONE to entry.phoneNumber
    )
}