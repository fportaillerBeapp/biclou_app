package fr.beapp.interviews.bicloo.kmm.presentation.contract

import fr.beapp.interviews.bicloo.kmm.logic.contract.ContractManager
import fr.beapp.interviews.bicloo.kmm.logic.contract.entity.ContractEntity
import kotlinx.coroutines.flow.Flow

class ContractPresenter internal constructor(private val contractManager: ContractManager) {

	fun getContracts(): Flow<List<ContractEntity>> = contractManager.getContracts()
}