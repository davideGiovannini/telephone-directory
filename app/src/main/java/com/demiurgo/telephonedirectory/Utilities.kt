package com.demiurgo.telephonedirectory

import android.Manifest.permission.READ_CONTACTS
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Contacts
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.demiurgo.telephonedirectory.model.Entry

/**
 * Created by demiurgo on 4/15/16.
 */


fun Activity.extractContactInformation(contactData: Uri?): Entry? {
    val c = contentResolver.query(contactData, null, null, null, null);
    if (c.moveToFirst()) {
        val fullname = c.getString(c.getColumnIndex(Contacts.DISPLAY_NAME));

        val firstSpace = fullname.indexOfFirst { ' ' == it }
        val name = if (firstSpace > 0) fullname.substring(0..firstSpace) else fullname
        val surname = if (firstSpace in 0..(fullname.length - 1)) fullname.substring(firstSpace + 1) else ""


        val contactId = c.getString(c.getColumnIndex(Contacts._ID));
        val hasPhone = c.getString(c.getColumnIndex(Contacts.HAS_PHONE_NUMBER));
        val phoneNumber = when (hasPhone) {
            "1" -> {
                val phones = contentResolver.query(Phone.CONTENT_URI,
                        null,
                        Phone.CONTACT_ID + " = " + contactId, null, null);
                val res = if (phones.moveToFirst()) {
                    phones.getString(phones.getColumnIndex(Phone.NUMBER))
                } else {
                    ""
                }
                phones.close()
                res // RETURN value of WHEN "1"
            }
            else -> ""
        }

        return Entry(name, surname, phoneNumber.improveParsedPhone())
    }
    return null
}


private fun String.improveParsedPhone(): String {
    if (Entry.phoneRegexp.matches(this)) {
        return this
    }

    var returnVal = this.trim()

    if ('+' !in this) {
        returnVal = "+$returnVal"
    }

    if (Entry.phoneRegexp.matches(returnVal)) {
        return returnVal
    }

    return returnVal.replace('-', ' ')
}


const val READ_CONTACTS_REQUEST = 123

fun Activity.requestPermission() {

    // Here, thisActivity is the current activity
    if (ContextCompat.checkSelfPermission(this, READ_CONTACTS) != PERMISSION_GRANTED) {

        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_CONTACTS)) {

        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    arrayOf(READ_CONTACTS),
                    READ_CONTACTS_REQUEST);
        }
    }
}