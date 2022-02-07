package com.example.bisimulation.main.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bisimulation.R
import com.example.bisimulation.databinding.FragmentBugReportBinding
import com.example.bisimulation.main.SharedViewModel

class BugReportFragment : Fragment() {
    private lateinit var binding: FragmentBugReportBinding
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bugReportFragmentSetup(inflater, container)

        //Send the bug report via email
        binding.sendEmailButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:test@mail.com")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Bug report - Bisimulation app")
            intent.putExtra(Intent.EXTRA_TEXT, binding.bugReportEditText.text.toString())

            startActivity(Intent.createChooser(intent, resources.getString(R.string.selectEmail_chooser)))
        }

        return binding.root
    }

    private fun bugReportFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentBugReportBinding.inflate(inflater, container, false)
        binding.sharedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}