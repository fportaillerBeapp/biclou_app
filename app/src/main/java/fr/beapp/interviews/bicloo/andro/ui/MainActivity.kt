package fr.beapp.interviews.bicloo.andro.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import fr.beapp.interviews.bicloo.andro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


	private lateinit var binding: ActivityMainBinding
	private val viewModel: MainViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		viewModel.loadAllContracts()
	}
}