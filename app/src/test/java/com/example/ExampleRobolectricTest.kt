package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.RugbyCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TRC Fotos", appName)
    val manifesto = context.getString(R.string.club_manifesto)
    assertEquals("Construido en el campo. Unidos después.", manifesto)
  }

  @Test
  fun `verify rugby categories defined correctly`() {
    val categories = RugbyCategory.entries
    assertEquals(8, categories.size)
    assertNotNull(RugbyCategory.fromCode("ESCUELA"))
    assertNotNull(RugbyCategory.fromCode("SUB14"))
    assertNotNull(RugbyCategory.fromCode("SUB16"))
    assertNotNull(RugbyCategory.fromCode("SUB18"))
    assertNotNull(RugbyCategory.fromCode("SEN_MASC"))
    assertNotNull(RugbyCategory.fromCode("SEN_FEM"))
    assertNotNull(RugbyCategory.fromCode("TOUCH"))
    assertNotNull(RugbyCategory.fromCode("VET"))

    // Verify TRC Escuela / Cantera classification
    assertTrue(RugbyCategory.ESCUELA.isCantera)
    assertTrue(RugbyCategory.SUB_14.isCantera)
    assertTrue(RugbyCategory.SUB_16.isCantera)
    assertTrue(RugbyCategory.SUB_18.isCantera)
    assertFalse(RugbyCategory.SENIOR_MASCULINO.isCantera)
    assertFalse(RugbyCategory.SENIOR_FEMENINO.isCantera)
    assertFalse(RugbyCategory.TOUCH.isCantera)
    assertFalse(RugbyCategory.VETERANOS.isCantera)

    // Verify backward compatibility aliases
    assertEquals(RugbyCategory.ESCUELA, RugbyCategory.fromCode("SUB10"))
    assertEquals(RugbyCategory.ESCUELA, RugbyCategory.fromCode("SUB12"))
    assertEquals(RugbyCategory.SENIOR_MASCULINO, RugbyCategory.fromCode("SENIOR"))
  }
}
