package nl.romster.tm.manager

import nl.romster.tm.domain.TmPriority
import nl.romster.tm.domain.TmRunningProcess
import nl.romster.tm.domain.TmProcess
import nl.romster.tm.manager.strategy.TaskManagerStrategy
import nl.romster.tm.util.RunningProcessComparator
import nl.romster.tm.util.TimeOfCreationOldFirstRunningProcessComparator

class TaskManager(
    private val strategy: TaskManagerStrategy
) {

    /**
     * Trying to add
     */
    fun add(process: TmProcess): Boolean {
        return strategy.add(process.toRunningProcess())
    }

    /**
     * List all the running processes, sorting them by time of creation, priority or id.
     */
    fun list(comparator: RunningProcessComparator = TimeOfCreationOldFirstRunningProcessComparator): List<TmRunningProcess> {
        return strategy.list().sortedWith(comparator)
    }

    /**
     * kill a specific process
     */
    fun kill(id: Long) = strategy.kill(id)

    /**
     * kill all processes with a specific priority
     */
    fun killGroup(priority: TmPriority) = strategy.killGroup(priority)

    /**
     * kill all running processes
     */
    fun killAll() = strategy.killAll()

    private fun TmProcess.toRunningProcess(): TmRunningProcess {
        return TmRunningProcess(process = this)
    }
}