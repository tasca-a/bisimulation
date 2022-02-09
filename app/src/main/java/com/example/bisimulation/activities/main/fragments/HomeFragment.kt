package com.example.bisimulation.activities.main.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bisimulation.R
import com.example.bisimulation.databinding.FragmentHomeBinding
import com.example.bisimulation.activities.main.SharedViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeFragmentSetup(inflater, container)

        //Discover more button redirects to the Profile page
        binding.discoverMoreButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToProfile()
            findNavController().navigate(action)
        }

        binding.playNowButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToPlayNow()
            findNavController().navigate(action)
        }

        //Clicking on the Share fab created a dialog that let you choose if you want to share or not
        binding.shareFab.setOnClickListener {
            //Create the dialog
            val alertDialog: AlertDialog? = activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setPositiveButton(R.string.shareDialogPositiveButton) { _, _ ->
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.shareMessage))
                            type = "text/plain"
                        }

                        val shareIntent = Intent.createChooser(sendIntent, null)
                        startActivity(shareIntent)
                    }
                    setNegativeButton(R.string.shareDialogNegativeButton) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                }

                builder.setTitle(R.string.shareDialogTitle)
                builder.setMessage(R.string.shareDialogMessage)

                builder.create()
            }

            //Show the dialog
            alertDialog?.show()
        }

        return binding.root
    }

    private fun homeFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.sharedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}