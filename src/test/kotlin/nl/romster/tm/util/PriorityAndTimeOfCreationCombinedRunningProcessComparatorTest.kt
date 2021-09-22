package nl.romster.tm.util

import nl.romster.tm.domain.TmPriority
import nl.romster.tm.domain.TmProcess
import nl.romster.tm.domain.TmRunningProcess
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

internal class PriorityAndTimeOfCreationCombinedRunningProcessComparatorTest {
    @Test
    fun `A less than B - different priority`() {
        val (a, b) = generateAandB(
            priorityA = TmPriority.LOW,
            timeA = Instant.now(),
            priorityB = TmPriority.MEDIUM,
            timeB = Instant.now().minusSeconds(10)
        )
        assertEquals(-1, PriorityAndTimeOfCreationCombinedRunningProcessComparator.compare(a, b))
    }

    @Test
    fun `A less than B - same priority`() {
        val (a, b) = generateAandB(
            priorityA = TmPriority.MEDIUM,
            timeA = Instant.now(),
            priorityB = TmPriority.MEDIUM,
            timeB = Instant.now().plusSeconds(10)
        )
        assertEquals(-1, PriorityAndTimeOfCreationCombinedRunningProcessComparator.compare(a, b))
    }

    @Test
    fun `A equals B`() {
        val time = Instant.now()
        val (a, b) = generateAandB(
            priorityA = TmPriority.MEDIUM,
            timeA = time,
            priorityB = TmPriority.MEDIUM,
            timeB = time
        )
        assertEquals(0, PriorityAndTimeOfCreationCombinedRunningProcessComparator.compare(a, b))
    }

    @Test
    fun `A bigger than B - different priority`() {
        val (a, b) = generateAandB(
            priorityA = TmPriority.MEDIUM,
            timeA = Instant.now(),
            priorityB = TmPriority.LOW,
            timeB = Instant.now().minusSeconds(10)
        )
        assertEquals(1, PriorityAndTimeOfCreationCombinedRunningProcessComparator.compare(a, b))
    }

    @Test
    fun `A bigger than B - same priority`() {
        val (a, b) = generateAandB(
            priorityA = TmPriority.LOW,
            timeA = Instant.now(),
            priorityB = TmPriority.LOW,
            timeB = Instant.now().minusSeconds(10)
        )
        assertEquals(1, PriorityAndTimeOfCreationCombinedRunningProcessComparator.compare(a, b))
    }

    private fun generateAandB(
        timeA: Instant,
        priorityA: TmPriority,
        timeB: Instant,
        priorityB: TmPriority): Pair<TmRunningProcess, TmRunningProcess> {
        val a = TmRunningProcess(
            process = TmProcess(
                id = 1L,
                priority = priorityA
            ),
            timeOfCreation = timeA
        )

        val b = TmRunningProcess(
            process = TmProcess(
                id = 2L,
                priority = priorityB
            ),
            timeOfCreation = timeB
        )
        return a to b
    }
}