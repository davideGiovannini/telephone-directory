package com.demiurgo.telephonedirectory

import com.demiurgo.telephonedirectory.model.Entry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by demiurgo on 4/14/16.
 */

class EntryTests{

    @Test
    fun validEntry(){
        assertTrue(Entry("Pico", "De Paperis", "+39 456 345677").isValid())
        assertTrue(Entry("Pico", "De Paperis", "+1234 5678 1234567890").isValid())
    }

    @Test
    fun invalidEntry(){
        assertFalse(Entry("Pico", "", "+39 456 345677").isValid())
        assertFalse(Entry("", "De Paperis", "+39 456 345677").isValid())
        assertFalse(Entry("Pico", "De Paperis", "").isValid())
        assertFalse(Entry("Pico", "De Paperis", "spazzatura").isValid())
        assertFalse(Entry("Pico", "De Paperis", "+39").isValid())
        assertFalse(Entry("Pico", "De Paperis", "+39 56574").isValid())
        assertFalse(Entry("Pico", "De Paperis", "+39 3434 34").isValid())
        assertFalse(Entry("Pico", "De Paperis", "+39 34 12345").isValid())
    }
}