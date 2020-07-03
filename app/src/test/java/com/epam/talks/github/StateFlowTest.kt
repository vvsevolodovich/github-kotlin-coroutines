package com.epam.talks.github

import android.util.Base64
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Test

class StateFlowTest {


    @Test
    fun shouldListenToEvents() {
        val flow = MutableStateFlow("")
        var read = false
        flow.value = "Hello!"

        CoroutineScope(TestCoroutineDispatcher()).launch {
            flow.collect { it ->
                print(it)
                read = true
            }
        }

        assertTrue(read)
    }
}