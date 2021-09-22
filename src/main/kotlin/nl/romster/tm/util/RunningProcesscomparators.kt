package nl.romster.tm.util

import nl.romster.tm.domain.TmRunningProcess

interface RunningProcessComparator: Comparator<TmRunningProcess>

object TimeOfCreationOldFirstRunningProcessComparator: RunningProcessComparator {
    override fun compare(rp1: TmRunningProcess, rp2: TmRunningProcess): Int {
        return rp1.timeOfCreation.compareTo(rp2.timeOfCreation)
    }
}

object PriorityLowFirstRunningProcessComparator: RunningProcessComparator {
    override fun compare(rp1: TmRunningProcess, rp2: TmRunningProcess): Int {
        return rp1.process.priority.compareTo(rp2.process.priority)
    }
}

object IdRunningProcessComparator: RunningProcessComparator {
    override fun compare(rp1: TmRunningProcess, rp2: TmRunningProcess): Int {
        return rp1.process.id.compareTo(rp2.process.id)
    }
}

object PriorityAndTimeOfCreationCombinedRunningProcessComparator: RunningProcessComparator {
    override fun compare(rp1: TmRunningProcess, rp2: TmRunningProcess): Int {
        val priorityResult =  PriorityLowFirstRunningProcessComparator.compare(rp1, rp2)
        return if(priorityResult == 0) {
            TimeOfCreationOldFirstRunningProcessComparator.compare(rp1, rp2)
        } else {
            priorityResult
        }
    }
}