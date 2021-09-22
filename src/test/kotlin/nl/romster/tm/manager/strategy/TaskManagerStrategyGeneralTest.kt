package nl.romster.tm.manager.strategy

import nl.romster.tm.domain.TmPriority
import nl.romster.tm.error.ProcessAlreadyExistsException
import nl.romster.tm.test.util.TestProcessGenerator
import nl.romster.tm.test.util.toRunningProcess
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.time.Instant
import java.util.stream.Stream

/**
 * Implementation of TaskManagerStrategy must be thread safe
 */
internal class TaskManagerStrategyGeneralTest {

    @ParameterizedTest
    @MethodSource("provideStrategyClasses")
    fun `Add processes within capacity`(strategyClass: Class<TaskManagerStrategy>) {
        val capacity = 3
        val strategy = getStrategy(capacity, strategyClass)
        val processList = TestProcessGenerator.generateTmProcessSpyList(capacity).map { it.toRunningProcess() }

        repeat(capacity) { index ->
            val process = processList[index]

            val isAdded = strategy.add(process)
            Assertions.assertTrue(isAdded, "Strategy returns true on process adding")
        }

        Assertions.assertTrue(strategy.list().containsAll(processList), "Strategy list() returns all added processes")
    }

    @ParameterizedTest
    @MethodSource("provideStrategyClasses")
    fun `Add processes with the same id causes exception`(strategyClass: Class<TaskManagerStrategy>) {
        val capacity = 3
        val strategy = getStrategy(capacity, strategyClass)
        val runningProcess =
            TestProcessGenerator.generateTmProcessSpyList(capacity).map { it.toRunningProcess() }.first()
        val runningProcessDuplicate = runningProcess.copy(timeOfCreation = Instant.now())
        strategy.add(runningProcess)
        Assertions.assertThrows(ProcessAlreadyExistsException::class.java) {
            strategy.add(runningProcessDuplicate)
        }
    }

    @ParameterizedTest
    @MethodSource("provideStrategyClasses")
    fun `kill process by id - process exists`(strategyClass: Class<TaskManagerStrategy>) {
        val capacity = 3
        val strategy = getStrategy(capacity, strategyClass)
        val processList = TestProcessGenerator.generateTmProcessSpyList(capacity).map { it.toRunningProcess() }
        processList.forEach { strategy.add(it) }

        val processToKill = processList[1]
        val result = strategy.kill(processToKill.process.id)
        val list = strategy.list()

        Assertions.assertTrue(result, "Strategy returns true when kills a process")
        Assertions.assertEquals(capacity - 1, list.size, "Only ${capacity - 1} elements left")
        Assertions.assertFalse(strategy.list().contains(processToKill), "Strategy list() method doesn't ")
        verify(processToKill.process, times(1)).kill()
    }

    @ParameterizedTest
    @MethodSource("provideStrategyClasses")
    fun `kill process by id - process does not exist`(strategyClass: Class<TaskManagerStrategy>) {
        val capacity = 3
        val strategy = getStrategy(capacity, strategyClass)
        val processList = TestProcessGenerator.generateTmProcessSpyList(capacity + 1).map { it.toRunningProcess() }
        processList.subList(0, capacity).forEach { strategy.add(it) }
        val listBefore = strategy.list()

        val processToKillNotExist = processList[capacity]
        val result = strategy.kill(processToKillNotExist.process.id)
        val listAfter = strategy.list()

        Assertions.assertFalse(result, "Strategy returns false when can not kill a process")
        Assertions.assertEquals(listBefore, listAfter, "Process list isn't't changed")
    }

    @ParameterizedTest
    @MethodSource("providePriorityAndStrategy")
    fun `killGroup - processes exist`(priorityToKill: TmPriority, strategyClass: Class<TaskManagerStrategy>) {
        val strategy = getStrategy(100, strategyClass)
        val processSize = TmPriority.values().size * 2
        val processList = TestProcessGenerator
            .generateTmProcessSpyList(processSize, TestProcessGenerator.FairDistributionPriorityResolver)
            .map { it.toRunningProcess() }
        processList.forEach { strategy.add(it) }

        val runningProcesses = strategy.list()
        TmPriority.values().forEach { priority ->
            Assertions.assertEquals(
                2,
                runningProcesses.filter { it.process.priority == priority }.size,
                "There are 2 processes for each priority type"
            )
        }

        val result = strategy.killGroup(priorityToKill)
        Assertions.assertTrue(result, "Strategy returns true when a group was removed by priority")

        val processesLeft = strategy.list()
        Assertions.assertEquals(runningProcesses.size - 2, processesLeft.size, "2 processes were removed")
        Assertions.assertFalse(
            processesLeft.any { it.process.priority == priorityToKill },
            "No $priorityToKill processes left"
        )
    }

    @ParameterizedTest
    @MethodSource("provideStrategyClasses")
    fun `killGroup - processes don't  exist`(strategyClass: Class<TaskManagerStrategy>) {
        val strategy = getStrategy(100, strategyClass)
        val result = strategy.killGroup(TmPriority.LOW)
        Assertions.assertFalse(result, "Strategy returns false when no processes were removed by priority")
    }


    @ParameterizedTest
    @MethodSource("provideStrategyClasses")
    fun `killAll removed all processes`(strategyClass: Class<TaskManagerStrategy>) {
        val capacity = 5
        val strategy = getStrategy(capacity, strategyClass)
        val processList = TestProcessGenerator.generateTmProcessSpyList(capacity).map { it.toRunningProcess() }
        processList.forEach { strategy.add(it) }
        val runningProcesses = strategy.list()
        Assertions.assertEquals(capacity, runningProcesses.size)

        strategy.killAll()

        Assertions.assertTrue(strategy.list().isEmpty(), "No running processes left")
    }


    private fun getStrategy(capacity: Int, clazz: Class<TaskManagerStrategy>): TaskManagerStrategy {
        return when(clazz) {
            DefaultTaskManagerStrategy::class.java -> DefaultTaskManagerStrategy(capacity)
            FifoTaskManagerStrategy::class.java -> FifoTaskManagerStrategy(capacity)
            PriorityTaskManagerStrategy::class.java -> PriorityTaskManagerStrategy(capacity)
            else -> throw IllegalArgumentException("Unknown strategy class: $clazz")
        }
    }

    companion object {
        @JvmStatic  fun provideStrategyClasses(): Stream<Class<out TaskManagerStrategy>> {
            return Stream.of(
                DefaultTaskManagerStrategy::class.java,
                FifoTaskManagerStrategy::class.java,
                PriorityTaskManagerStrategy::class.java
            )
        }

        @JvmStatic  fun providePriorityAndStrategy(): Stream<Arguments> {
            return TmPriority.values().toList()
                .stream().flatMap {
                    listOf(
                        Arguments.of(it, DefaultTaskManagerStrategy::class.java),
                        Arguments.of(it, FifoTaskManagerStrategy::class.java),
                        Arguments.of(it, PriorityTaskManagerStrategy::class.java)
                    ).stream()
                }
        }
    }
}