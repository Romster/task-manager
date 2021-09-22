package nl.romster.tm.domain

open class TmProcess(
    val id: Long,
    val priority: TmPriority
) {

    open fun kill() {
        println("Process $id is killed")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TmProcess

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + priority.hashCode()
        return result
    }

    override fun toString(): String {
        return "TmProcess(id=$id, priority=$priority)"
    }


}