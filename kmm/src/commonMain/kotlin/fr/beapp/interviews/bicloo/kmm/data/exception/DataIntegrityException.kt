package fr.beapp.interviews.bicloo.kmm.data.exception

data class DataIntegrityException(val data: Any) : Exception("Something went wrong when trying to parse data $data")
