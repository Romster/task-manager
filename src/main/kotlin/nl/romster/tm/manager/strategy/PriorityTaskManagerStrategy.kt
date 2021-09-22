package nl.romster.tm.manager.strategy

import nl.romster.tm.domain.TmRunningProcess
import nl.romster.tm.util.PriorityAndTimeOfCreationCombinedRunningProcessComparator

class PriorityTaskManagerStrategy(capacity: Int) : AbstractSynchronizedArrayBasedTaskManagerStrategy(capacity) {

    override fun replaceByIndexWhenCapacityExceeded(process: TmRunningProcess): Int {
        if (runningProcessIndexMap.isEmpty()) {
            return -1
        }
        val maxPriorityToReplace = process.process.priority
         return runningProcessIndexMap.values
            .map { it to runningProcessArray[it]!! }
            .filter { (_, process) -> process.process.priority <= maxPriorityToReplace }
            .map { (index, process) -> RunningProcessWithIndex(index, process) }
            .minOrNull()?.index ?: -1
    }


    private data class RunningProcessWithIndex(
        val index: Int,
        val process: TmRunningProcess
    ): Comparable<RunningProcessWithIndex> {

        override fun compareTo(other: RunningProcessWithIndex): Int {
            return PriorityAndTimeOfCreationCombinedRunningProcessComparator
                .compare(this.process, other.process)
        }

    }
}