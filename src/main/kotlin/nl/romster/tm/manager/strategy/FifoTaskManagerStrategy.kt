package nl.romster.tm.manager.strategy

import nl.romster.tm.domain.TmRunningProcess

class FifoTaskManagerStrategy(capacity: Int) : AbstractSynchronizedArrayBasedTaskManagerStrategy(capacity) {

    override fun replaceByIndexWhenCapacityExceeded(process: TmRunningProcess): Int {
        if(runningProcessIndexMap.isEmpty()) {
            return -1
        }
        val firstIndex = runningProcessIndexMap.values.first()
        var indexToReplace = firstIndex
        var oldestTimeOfCreation = runningProcessArray[firstIndex]!!.timeOfCreation
        runningProcessIndexMap.values.forEach { index ->
            val timeOfCreation = runningProcessArray[index]!!.timeOfCreation
            if (timeOfCreation.isBefore(oldestTimeOfCreation)) {
                indexToReplace = index
                oldestTimeOfCreation = timeOfCreation
            }
        }
        return indexToReplace
    }
}