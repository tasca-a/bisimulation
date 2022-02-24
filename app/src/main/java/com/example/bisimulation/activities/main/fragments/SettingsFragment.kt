package com.example.bisimulation.activities.main.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bisimulation.databinding.FragmentSettingsBinding
import com.example.bisimulation.activities.main.SharedViewModel
import java.util.*

class SettingsFragment : Fragment(){
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsFragmentSetup(inflater, container)

        binding.englishImageButton.setOnClickListener {
            setLanguage("en")
        }

        binding.italianImageButton.setOnClickListener {
            setLanguage("it")
        }

        return binding.root
    }

    // Change the app locale
    // We should not change the locale at runtime because we are actively
    // working against the system and we should instead let the OS decide
    // which locale to load. All methods to change locale at runtime are
    // difficult, deprecated and prone to errors.
    // Ask if it is possible to change this requirement.
    private fun setLanguage(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        activity?.baseContext?.resources?.updateConfiguration(config,
            activity?.baseContext?.resources?.displayMetrics)

        val sharedPreferences = activity?.getSharedPreferences("com.example.bisimulation", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.putString("language", lang)?.apply()

        activity?.recreate()
    }

    private fun settingsFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.sharedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}