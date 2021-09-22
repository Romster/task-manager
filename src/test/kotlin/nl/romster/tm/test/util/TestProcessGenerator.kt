package nl.romster.tm.test.util

import nl.romster.tm.domain.TmPriority
import nl.romster.tm.domain.TmProcess
import nl.romster.tm.util.ProcessIdGeneratorDefault
import org.mockito.kotlin.spy

object TestProcessGenerator {

    fun generateTmProcessSpyList(
        size: Int,
        priorityResolver: (Int) -> TmPriority = FairDistributionPriorityResolver
    ): List<TmProcess> {
        return (0 until size).toList().map {
            spy(
                TmProcess(
                    id = ProcessIdGeneratorDefault.generateId(),
                    priority = priorityResolver.invoke(it)
                )
            )
        }
    }

    object FairDistributionPriorityResolver : (Int) -> TmPriority {
        override fun invoke(index: Int): TmPriority {
            val reminded = index % 3
            return when (reminded) {
                0 -> TmPriority.LOW
                1 -> TmPriority.MEDIUM
                else -> TmPriority.HIGH
            }
        }
    }

}