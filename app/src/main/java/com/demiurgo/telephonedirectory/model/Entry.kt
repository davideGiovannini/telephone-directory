package com.demiurgo.telephonedirectory.model

import org.jetbrains.anko.db.RowParser
import rx.Observable
import rx.Subscriber
import java.util.*

/**
 * Created by demiurgo on 4/14/16.
 */

data class Entry(var id: Long,
                 var firstName: String,
                 var lastName: String,
                 var phoneNumber: String) {

    constructor(firstName: String,
                lastName: String,
                phoneNumber: String) : this(-1, firstName, lastName, phoneNumber)

    fun isValid(): Boolean =
            firstName.isNotBlank() && lastName.isNotBlank() && phoneRegexp.matches(phoneNumber)


    fun hasSameData(entry: Entry): Boolean =
            firstName == entry.firstName && lastName == entry.lastName && phoneNumber == entry.phoneNumber


    companion object {
        val phoneRegexp = Regex("^\\+\\d+ \\d+ \\d{6,}$")
        val parser = object : RowParser<Entry> {
            override fun parseRow(columns: Array<Any>): Entry {
                return Entry(columns[0] as Long, columns[1] as String, columns[2] as String, columns[3] as String)
            }
        }
    }
}



class FutureEntry(): Observable.OnSubscribe<Entry?>{
    val subscribers = HashSet<Subscriber<in Entry?>>()

    override fun call(t: Subscriber<in Entry?>) {
        subscribers.add(t)
        t.onStart()
    }

    fun sendValue(entry: Entry?){
        subscribers.forEach {
            it.onNext(entry)
            it.onCompleted()
        }
        subscribers.clear()
    }
}


