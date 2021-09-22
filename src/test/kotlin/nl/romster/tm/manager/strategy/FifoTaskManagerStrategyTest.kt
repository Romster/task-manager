package nl.romster.tm.manager.strategy

import nl.romster.tm.test.util.TestProcessGenerator
import nl.romster.tm.test.util.toRunningProcess
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class FifoTaskManagerStrategyTest {

    @Test
    fun `Add process out of capacity - process is replacing the oldest one`() {
        val capacity = 3
        val strategy = getStrategy(capacity)
        val processList = TestProcessGenerator.generateTmProcessSpyList(capacity + 1).map { it.toRunningProcess() }

        val oldestProcess = processList.first()
        val elementOutOfCapacity = processList[capacity]

        val addingResultList = processList.map { strategy.add(it) }
        assertTrue(
            addingResultList[capacity],
            "Strategy returns true because the process is added"
        )

        val runningProcessList = strategy.list()
        assertTrue(
            runningProcessList.contains(elementOutOfCapacity),
            "Element out of capacity is added to the list"
        )
        assertFalse(
            runningProcessList.contains(oldestProcess),
            "The oldest element was replaced with the new one"
        )
    }

    private fun getStrategy(capacity: Int): TaskManagerStrategy {
        return FifoTaskManagerStrategy(capacity)
    }
}