package nl.romster.tm.util

import java.util.concurrent.atomic.AtomicLong

interface ProcessIdGenerator {
    fun generateId(): Long
}

object ProcessIdGeneratorDefault: ProcessIdGenerator {
    private val currentValue = AtomicLong(0)

    override fun generateId(): Long {
        return currentValue.incrementAndGet()
    }
}
