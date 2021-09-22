package nl.romster.tm.util

import nl.romster.tm.domain.TmPriority
import nl.romster.tm.domain.TmProcess
import nl.romster.tm.domain.TmRunningProcess
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant

internal class PriorityLowFirstRunningProcessComparatorTest {

    @Test
    fun `A less than B`() {
        val (a, b) = generateAandB(priorityA = TmPriority.LOW, priorityB = TmPriority.MEDIUM)
        assertEquals(-1, PriorityLowFirstRunningProcessComparator.compare(a, b))
    }

    @Test
    fun `A equals B`() {
        val (a, b) = generateAandB(priorityA = TmPriority.MEDIUM, priorityB = TmPriority.MEDIUM)
        assertEquals(0, PriorityLowFirstRunningProcessComparator.compare(a, b))
    }

    @Test
    fun `A bigger than B`() {
        val (a, b) = generateAandB(priorityA = TmPriority.HIGH, priorityB = TmPriority.MEDIUM)
        assertEquals(1, PriorityLowFirstRunningProcessComparator.compare(a, b))
    }

    private fun generateAandB(priorityA: TmPriority, priorityB: TmPriority): Pair<TmRunningProcess, TmRunningProcess> {
        val timestamp = Instant.now()
        val a = TmRunningProcess(
            process = TmProcess(
                id = 1L,
                priority = priorityA
            ),
            timeOfCreation = timestamp
        )

        val b = TmRunningProcess(
            process = TmProcess(
                id = 2L,
                priority = priorityB
            ),
            timeOfCreation = timestamp
        )
        return a to b
    }
}