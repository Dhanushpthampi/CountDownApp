package com.redgravity.countdown

import com.redgravity.countdown.model.CountdownCardStyle
import com.redgravity.countdown.model.CountdownCategory
import com.redgravity.countdown.model.CountdownItem
import com.redgravity.countdown.model.CountdownVisualTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CountdownModelTest {

    @Test
    fun testRemainingCalculation_FutureTarget() {
        val now = 1_000_000_000L
        val oneDayMillis = 86_400_000L
        val target = now + (2 * oneDayMillis) + (5 * 3_600_000L) + (30 * 60_000L)

        val item = CountdownItem(
            title = "Test Milestone",
            targetEpochMillis = target,
            createdEpochMillis = now - (10 * oneDayMillis)
        )

        val remaining = item.calculateRemaining(currentTimeMillis = now)

        assertFalse(remaining.isFinished)
        assertEquals(2L, remaining.days)
        assertEquals(5L, remaining.hours)
        assertEquals(30L, remaining.minutes)
        assertTrue(remaining.progressRatio > 0.5f)
        assertEquals("DAYS", remaining.daysLabel)
    }

    @Test
    fun testRemainingCalculation_FinishedTarget() {
        val now = 1_000_000_000L
        val target = now - 5000L

        val item = CountdownItem(
            title = "Past Event",
            targetEpochMillis = target,
            createdEpochMillis = now - 100000L
        )

        val remaining = item.calculateRemaining(currentTimeMillis = now)

        assertTrue(remaining.isFinished)
        assertEquals(0L, remaining.days)
        assertEquals(0L, remaining.hours)
        assertEquals(0L, remaining.minutes)
        assertEquals(0L, remaining.seconds)
        assertEquals(1.0f, remaining.progressRatio, 0.001f)
    }

    @Test
    fun testVisualTheme_Fallback() {
        val theme = CountdownVisualTheme.fromId("unknown_id")
        assertEquals(CountdownVisualTheme.OBSIDIAN_GOLD.id, theme.id)
    }

    @Test
    fun testCategory_Fallback() {
        val cat = CountdownCategory.fromId("astronomy")
        assertEquals(CountdownCategory.ASTRONOMY, cat)

        val unknown = CountdownCategory.fromId("random")
        assertEquals(CountdownCategory.MILESTONE, unknown)
    }

    @Test
    fun testCardStyle_Parsing() {
        val style = CountdownCardStyle.fromId("neo_glass")
        assertEquals(CountdownCardStyle.NEO_GLASS, style)
    }
}
