package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.GameDatabase
import com.example.data.GameSaveEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Astraforge", appName)
  }

  @Test
  fun `verify Room database opens and reads schema cleanly`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = GameDatabase.getInstance(context)
    val dao = db.gameSaveDao()
    dao.insertOrUpdate(GameSaveEntity(credits = 999L, equipmentLevel = 5))
    val save = dao.getSaveDirect()
    assertNotNull(save)
    assertEquals(999L, save?.credits)
    assertEquals(5, save?.equipmentLevel)
  }
}
