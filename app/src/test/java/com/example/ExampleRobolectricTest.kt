package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app_name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AkaUpkeep", appName)
  }

  @Test
  fun `test 48 hours date advance detection`() {
    val today = DateUtils.todayEpochDay()
    assertTrue(DateUtils.isWithinNext48Hours(today))
    assertTrue(DateUtils.isWithinNext48Hours(today + 1))
    assertTrue(DateUtils.isWithinNext48Hours(today + 2))
    assertEquals("Hoy", DateUtils.formatRelativeDays(today))
    assertEquals("En 2 días (¡Alerta previa!)", DateUtils.formatRelativeDays(today + 2))
  }
}
