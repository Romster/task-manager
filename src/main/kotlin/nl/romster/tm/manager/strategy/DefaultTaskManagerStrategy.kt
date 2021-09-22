package nl.romster.tm.manager.strategy

import nl.romster.tm.domain.TmRunningProcess

class DefaultTaskManagerStrategy(capacity: Int) : AbstractSynchronizedArrayBasedTaskManagerStrategy(capacity) {

    override fun replaceByIndexWhenCapacityExceeded(process: TmRunningProcess): Int {
        return -1
    }
}