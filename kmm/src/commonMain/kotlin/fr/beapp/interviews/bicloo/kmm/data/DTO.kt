package fr.beapp.interviews.bicloo.kmm.data

import fr.beapp.interviews.bicloo.kmm.data.exception.DataIntegrityException

interface DTO<Entity> {

	@Throws(DataIntegrityException::class)
	fun toEntity(): Entity
}