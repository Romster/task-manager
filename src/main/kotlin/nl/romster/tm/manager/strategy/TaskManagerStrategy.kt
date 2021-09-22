package nl.romster.tm.manager.strategy

import nl.romster.tm.domain.TmPriority
import nl.romster.tm.domain.TmRunningProcess

/**
 * Implementation of TaskManagerStrategy must be thread safe
 */
interface TaskManagerStrategy {

    fun add(process: TmRunningProcess): Boolean

    /**
     * List all the running processes, sorting them by time of creation, priority or id.
     */
    fun list(): List<TmRunningProcess>

    /**
     * kill a specific process
     */
    fun kill(id: Long): Boolean

    /**
     * kill all processes with a specific priority
     */
    fun killGroup(priority: TmPriority): Boolean

    /**
     * kill all running processes
     */
    fun killAll()
}