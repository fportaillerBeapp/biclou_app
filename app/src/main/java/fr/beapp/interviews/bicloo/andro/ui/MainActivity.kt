package fr.beapp.interviews.bicloo.andro.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import fr.beapp.interviews.bicloo.andro.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {


	private lateinit var binding: MainActivityBinding
	private val viewModel: MainViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = MainActivityBinding.inflate(layoutInflater)
		setContentView(binding.root)

		viewModel.loadAllContracts()
		viewModel.loadAllStations()
	}
}