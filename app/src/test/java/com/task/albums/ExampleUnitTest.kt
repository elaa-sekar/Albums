package com.task.albums

import com.task.albums.utils.DataUtils
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun mapping_isCorrect() {
        assertEquals("None", DataUtils.getFilterItemsList()[0].title)
        assertEquals(2, DataUtils.getFilterItemsList()[1].id)
    }
}