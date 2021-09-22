package nl.romster.tm.util

import nl.romster.tm.domain.TmPriority
import nl.romster.tm.domain.TmProcess
import nl.romster.tm.domain.TmRunningProcess
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

internal class TimeOfCreationOldFirstRunningProcessComparatorTest {

    @Test
    fun `A less than B`() {
        val time = Instant.now()
        val (a, b) = generateAandB(timeA = time, timeB = time.plusSeconds(1))
        assertEquals(-1, TimeOfCreationOldFirstRunningProcessComparator.compare(a, b))
    }

    @Test
    fun `A equals B`() {
        val time = Instant.now()
        val (a, b) = generateAandB(timeA = time, timeB = time)
        assertEquals(0, TimeOfCreationOldFirstRunningProcessComparator.compare(a, b))
    }

    @Test
    fun `A bigger than B`() {
        val time = Instant.now()
        val (a, b) = generateAandB(timeA = time, timeB = time.minusSeconds(10))
        assertEquals(1, TimeOfCreationOldFirstRunningProcessComparator.compare(a, b))
    }

    private fun generateAandB(timeA: Instant, timeB: Instant): Pair<TmRunningProcess, TmRunningProcess> {
        val a = TmRunningProcess(
            process = TmProcess(
                id = 1L,
                priority = TmPriority.LOW
            ),
            timeOfCreation = timeA
        )

        val b = TmRunningProcess(
            process = TmProcess(
                id = 2L,
                priority = TmPriority.LOW
            ),
            timeOfCreation = timeB
        )
        return a to b
    }
}