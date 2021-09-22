package nl.romster.tm.manager.strategy

import nl.romster.tm.test.util.TestProcessGenerator
import nl.romster.tm.test.util.toRunningProcess
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

internal class DefaultTaskManagerStrategyTest {

    @Test
    fun `Add process out of capacity - process is not added`() {
        val capacity = 3
        val strategy = getStrategy(capacity)
        val processList = TestProcessGenerator.generateTmProcessSpyList(capacity + 1).map { it.toRunningProcess() }

        val elementOutOfCapacity = processList[capacity]

        val addingResultList = processList.map { strategy.add(it) }
        assertFalse(addingResultList[capacity], "Strategy returns false when can't add a process")

        val runningProcessList = strategy.list()
        assertFalse(
            runningProcessList.contains(elementOutOfCapacity),
            "Element out of capacity is not added to the list"
        )
    }

    private fun getStrategy(capacity: Int): TaskManagerStrategy {
        return DefaultTaskManagerStrategy(capacity)
    }
}