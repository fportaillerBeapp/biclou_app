package fr.beapp.interviews.bicloo.kmm.logic.contract

import fr.beapp.interviews.bicloo.kmm.core.log.SharedLogger
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.CacheManager
import fr.beapp.interviews.bicloo.kmm.core.storage.cache.strategy.StrategyType
import fr.beapp.interviews.bicloo.kmm.data.contract.ContractDataSource
import fr.beapp.interviews.bicloo.kmm.logic.contract.entity.ContractEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.builtins.ListSerializer

internal class ContractManager(
	private val cacheManager: CacheManager,
	private val contractDataSource: ContractDataSource
) {
	companion object {
		private const val KEY_CONTRACTS = "ContractManager.KEY_CONTRACTS"
	}

	fun getContracts(): Flow<List<ContractEntity>> = cacheManager.from<List<ContractEntity>>(KEY_CONTRACTS)
		.withAsync {
			contractDataSource.getContracts().mapNotNull { contractDTO ->
				try {
					contractDTO.toEntity()
				} catch (throwable: Throwable) {
					SharedLogger.warn("Fail to parse contract (skipped)", throwable)
					null
				}
			}
		}
		.withStrategy(StrategyType.JUST_ASYNC)
		.withTtl(CacheManager.StrategyBuilder.TTL_MONTH)
		.withSerializer(ListSerializer(ContractEntity.serializer()))
		.execute()
}