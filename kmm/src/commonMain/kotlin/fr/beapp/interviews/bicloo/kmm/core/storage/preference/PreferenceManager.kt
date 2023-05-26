package fr.beapp.interviews.bicloo.kmm.core.storage.preference

object PreferenceManager {
	
	private const val KEY_IS_ONBOARDING_DONE = "KEY_IS_ONBOARDING_DONE"
	var isOnboardingDone: Boolean
		get() = PreferenceStorage.getBoolean(KEY_IS_ONBOARDING_DONE, false)
		set(value) {
			PreferenceStorage.putBoolean(KEY_IS_ONBOARDING_DONE, value)
		}

	private const val KEY_DETAIL_ARTICLE_TEXTSIZE_MODIFIER = "KEY_DETAIL_ARTICLE_TEXTSIZE_MODIFIER"
	var detailArticleTextSizeModifier: Float
		get() = PreferenceStorage.getFloat(KEY_DETAIL_ARTICLE_TEXTSIZE_MODIFIER) ?: 1f
		set(value) {
			PreferenceStorage.putFloat(KEY_DETAIL_ARTICLE_TEXTSIZE_MODIFIER, value)
		}
}
