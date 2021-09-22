package nl.romster.tm.manager

import nl.romster.tm.domain.TmPriority
import nl.romster.tm.domain.TmProcess
import nl.romster.tm.domain.TmRunningProcess
import nl.romster.tm.manager.strategy.TaskManagerStrategy
import nl.romster.tm.util.RunningProcessComparator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.*

internal class TaskManagerUnitTest {

    lateinit var taskManager: TaskManager
    lateinit var strategyMock: TaskManagerStrategy

    @BeforeEach
    fun initTest() {
        strategyMock = mock(TaskManagerStrategy::class.java)
        taskManager = TaskManager(strategyMock)
    }

    @Test
    fun `Test add`() {
        val testData = TmProcess(
            id = 1L,
            TmPriority.LOW
        )
        whenever(strategyMock.add(argThat { process == testData })).thenReturn(true, false)

        assertTrue(taskManager.add(testData))
        assertFalse(taskManager.add(testData))
    }

    @Test
    fun `Test list`() {
        val comparatorMockAlwaysRevert = mock(RunningProcessComparator::class.java)
        whenever(comparatorMockAlwaysRevert.compare(any(), any())).thenReturn(1)

        val actualList = listOf(
            TmRunningProcess(
                process = TmProcess(
                    id = 1L,
                    TmPriority.HIGH
                )
            ),
            TmRunningProcess(
                process = TmProcess(
                    id = 1L,
                    TmPriority.LOW
                )
            )
        )
        whenever(strategyMock.list()).thenReturn(actualList)
        val expectedList = actualList.sortedWith(comparatorMockAlwaysRevert)
        assertEquals(expectedList, taskManager.list(comparatorMockAlwaysRevert))
    }

    @Test
    fun `Test kill`() {
        val idExists = 1L
        val idNotExist = 2L
        whenever(strategyMock.kill(idExists)).thenReturn(true)
        whenever(strategyMock.kill(idNotExist)).thenReturn(false)

        assertTrue(taskManager.kill(idExists))
        assertFalse(taskManager.kill(idNotExist))
    }

    @Test
    fun `Test killGroup`() {
        val groupExists = TmPriority.HIGH
        val groupNotExist = TmPriority.MEDIUM
        whenever(strategyMock.killGroup(groupExists)).thenReturn(true)
        whenever(strategyMock.killGroup(groupNotExist)).thenReturn(false)

        assertTrue(taskManager.killGroup(groupExists))
        assertFalse(taskManager.killGroup(groupNotExist))
    }

    @Test
    fun `Test killAll`(){
        taskManager.killAll()
        verify(strategyMock, times(1)).killAll()
    }

}