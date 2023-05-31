package fr.beapp.interviews.bicloo.andro.ui.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewBinding> : Fragment() {

	/**
	 * _binding: provides a way to destroy the view once the fragment view is destroyed
	 */
	private var _binding: T? = null

	/**
	 * Binding : the accessible binding attribute.
	 * Do not access it before/while [BaseFragment.onCreateView], prefer using it in [BaseFragment.onViewCreated]
	 */
	protected val binding: T
		get() = _binding!!

	/**
	 * Build view binding for [BaseFragment.onCreateView] method
	 *
	 * @param inflater the layout inflater
	 * @param container the Fragment's view Container
	 * @return
	 */
	abstract fun buildViewBinding(inflater: LayoutInflater, container: ViewGroup?): T

	/**
	 * On create view
	 * WARNING : If you happen to overload this method, please do so while calling this super, and using also
	 * @sample Fragment.onCreateView(inflater, container, savedInstanceState).also{ viewBinding ->
	 *      // do what you want here
	 * }
	 *
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = buildViewBinding(inflater, container)
		return binding.root
	}

	/**
	 * On destroy view
	 * set the viewBinding to null, thus it frees some cache
	 */
	override fun onDestroyView() {
		super.onDestroyView()
		// Free some cache as soon as the fragment is not visible
		_binding = null
	}
}