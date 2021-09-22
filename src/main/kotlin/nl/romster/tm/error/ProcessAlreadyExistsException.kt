package nl.romster.tm.error

class ProcessAlreadyExistsException(id: Long): RuntimeException("Can't run process with id:$id. Process with the same id already exists") {
}