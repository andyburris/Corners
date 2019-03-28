package com.andb.apps.corners

import org.junit.Test

class ValuesTest {

    @Test
    fun allSizes() {
        Values.sizes = Values.listFromSize(16)
        assert(Values.sizes == arrayListOf(16, 16, 16, 16))
    }

    @Test
    fun commonSize() {
        Values.sizes = Values.listFromSize(16)
        assert(Values.commonSize() == 16)
        Values.sizes = arrayListOf(1, 43, 23, 43)
        assert(Values.commonSize() == 43)
        Values.sizes = arrayListOf(43, 1, 23, 44)
        System.out.println("no common: sizes = ${Values.commonSize()} aka top left")
    }
}