package nl.romster.tm.util

import nl.romster.tm.domain.TmPriority
import nl.romster.tm.domain.TmProcess
import nl.romster.tm.domain.TmRunningProcess
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant

internal class IdRunningProcessComparatorTest {

    @Test
    fun `A less than B`() {
        val (a, b) = generateAandB(idA = 1L, idB = 2L)
        assertEquals(-1, IdRunningProcessComparator.compare(a, b))
    }

    @Test
    fun `A equals B`() {
        val (a, b) = generateAandB(idA = 1L, idB = 1L)
        assertEquals(0, IdRunningProcessComparator.compare(a, b))
    }

    @Test
    fun `A bigger than B`() {
        val (a, b) = generateAandB(idA = 2L, idB = 1L)
        assertEquals(1, IdRunningProcessComparator.compare(a, b))
    }

    private fun generateAandB(idA: Long, idB: Long): Pair<TmRunningProcess, TmRunningProcess> {
        val priority = TmPriority.LOW
        val timestamp = Instant.now()
        val a = TmRunningProcess(
            process = TmProcess(
                id = idA,
                priority = priority
            ),
            timeOfCreation = timestamp
        )

        val b = TmRunningProcess(
            process = TmProcess(
                id = idB,
                priority = priority
            ),
            timeOfCreation = timestamp
        )
        return a to b
    }

}