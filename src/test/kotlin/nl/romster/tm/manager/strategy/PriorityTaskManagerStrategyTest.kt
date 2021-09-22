package nl.romster.tm.manager.strategy

import nl.romster.tm.domain.TmPriority
import nl.romster.tm.test.util.TestProcessGenerator
import nl.romster.tm.test.util.toRunningProcess
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PriorityTaskManagerStrategyTest {

    @Test
    fun `Add process out of capacity - process is replacing the oldest one with the same or lower priority`() {
        val processList = TestProcessGenerator.generateTmProcessSpyList(8).map { it.toRunningProcess() }
        val elementOutOfCapacityLowPrio = processList[6]
            .also { assertEquals(TmPriority.LOW, it.process.priority) }
        val elementOutOfCapacityMediumPrio = processList[7]
            .also { assertEquals(TmPriority.MEDIUM, it.process.priority) }

        val capacity = 4
        val strategy = getStrategy(capacity)

        val processListNoLow = processList.filter { it.process.priority != TmPriority.LOW }
            .subList(0, capacity)
            .also { assertEquals(capacity, it.size) }

        processListNoLow.forEach { strategy.add(it) }
        with(strategy.list()){
            assertFalse(contains(elementOutOfCapacityLowPrio))
            assertFalse(contains(elementOutOfCapacityMediumPrio))
        }

        val resultAddLowPrio = strategy.add(elementOutOfCapacityLowPrio)
        assertFalse(resultAddLowPrio, "Process with lower priority is not added")
        with(strategy.list()){
            assertFalse(contains(elementOutOfCapacityLowPrio))
        }

        val resultAddMediumPrio = strategy.add(elementOutOfCapacityMediumPrio)
        assertTrue(resultAddMediumPrio, "Process with medium priority replaces the oldest medium one")
        with(strategy.list()){
            assertTrue(contains(elementOutOfCapacityMediumPrio))
        }

        val replacedProcess = processListNoLow.filter { it.process.priority == TmPriority.MEDIUM }
            .minByOrNull { it.timeOfCreation }!!
        with(strategy.list()){
            assertFalse(contains(replacedProcess))
        }

    }

    private fun getStrategy(capacity: Int): TaskManagerStrategy {
        return PriorityTaskManagerStrategy(capacity)
    }

}