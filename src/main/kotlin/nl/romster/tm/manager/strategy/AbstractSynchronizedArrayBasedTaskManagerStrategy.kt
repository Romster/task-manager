package nl.romster.tm.manager.strategy

import nl.romster.tm.domain.TmPriority
import nl.romster.tm.domain.TmRunningProcess
import nl.romster.tm.error.ProcessAlreadyExistsException
import java.util.*
import kotlin.collections.HashSet

abstract class AbstractSynchronizedArrayBasedTaskManagerStrategy(private val capacity: Int) : TaskManagerStrategy {

    protected val runningProcessArray = arrayOfNulls<TmRunningProcess>(capacity)
    protected val runningProcessIndexMap = HashMap<Long, Int>()
    protected val freeIndexes = HashSet<Int>()

    init {
        initFreeIndexes()
    }

    @Synchronized
    override fun add(process: TmRunningProcess): Boolean {
        if(runningProcessIndexMap.contains(process.process.id)){
            throw ProcessAlreadyExistsException(process.process.id)
        }

        val index = if (freeIndexes.isEmpty()) {
            val replaceProcessIndex = replaceByIndexWhenCapacityExceeded(process)
            if (replaceProcessIndex < 0) {
                return false
            }
            replaceProcessIndex
        } else {
            val iterator = freeIndexes.iterator()
            val freeIndex = iterator.next()
            freeIndex.also {
                iterator.remove()
            }
        }
        runningProcessArray[index] = process
        runningProcessIndexMap.put(process.process.id, index)
        return true
    }

    @Synchronized
    override fun list(): List<TmRunningProcess> {
        return runningProcessArray.asList().filterNotNull()
    }

    @Synchronized
    override fun kill(id: Long): Boolean {
        val indexToKill = runningProcessIndexMap.remove(id) ?: return false
        runningProcessArray[indexToKill]!!.process.kill()
        runningProcessArray[indexToKill] = null
        freeIndexes.add(indexToKill)
        return true
    }

    @Synchronized
    override fun killGroup(priority: TmPriority): Boolean {
        val processPointerIterator = runningProcessIndexMap.entries.iterator()
        var removalResult = false
        while (processPointerIterator.hasNext()) {
            val (_, index) = processPointerIterator.next()
            val process = runningProcessArray[index]!!
            if (process.process.priority == priority) {
                process.process.kill()
                runningProcessArray[index] = null
                freeIndexes.add(index)
                processPointerIterator.remove()
                removalResult = true
            }
        }
        return removalResult
    }

    @Synchronized
    override fun killAll() {
        val processPointerIterator = runningProcessIndexMap.entries.iterator()
        while (processPointerIterator.hasNext()) {
            val (_, index) = processPointerIterator.next()
            val process = runningProcessArray[index]!!
            process.process.kill()
            runningProcessArray[index] = null
            freeIndexes.add(index)
            processPointerIterator.remove()
        }
    }

    /**
     * @return index of element which should be replaced with the new process.
     * index must be in range [0, capacity)
     * if index < 0, process will not be added
     */
    protected abstract fun replaceByIndexWhenCapacityExceeded(process: TmRunningProcess): Int

    private fun initFreeIndexes() {
        (0 until capacity).forEach {
            freeIndexes.add(it)
        }
    }

}