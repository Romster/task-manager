package nl.romster.tm.domain

import java.time.Instant

data class TmRunningProcess(
    val process: TmProcess,
    val timeOfCreation: Instant = Instant.now()
)