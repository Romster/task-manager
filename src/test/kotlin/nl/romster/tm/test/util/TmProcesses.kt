package nl.romster.tm.test.util

import nl.romster.tm.domain.TmRunningProcess
import nl.romster.tm.domain.TmProcess

fun TmProcess.toRunningProcess() = TmRunningProcess(process = this)