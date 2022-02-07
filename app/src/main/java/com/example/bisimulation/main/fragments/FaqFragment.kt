package com.example.bisimulation.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bisimulation.R
import com.example.bisimulation.databinding.FragmentFaqBinding
import com.example.bisimulation.main.SharedViewModel

class FaqFragment : Fragment() {
    private lateinit var binding: FragmentFaqBinding
    private val viewModel: SharedViewModel by activityViewModels()

    private val isAnswerVisible = arrayOf(false, false, false, false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        faqFragmentSetup(inflater, container)

        binding.question1Group.setOnClickListener {
            setItemVisibility(binding.answer1TextView, 0, binding.expand1ImageView)
        }
        binding.question2Group.setOnClickListener {
            setItemVisibility(binding.answer2TextView, 1, binding.expand2ImageView)
        }
        binding.question3Group.setOnClickListener {
            setItemVisibility(binding.answer3TextView, 2, binding.expand3ImageView)
        }
        binding.question4Group.setOnClickListener {
            setItemVisibility(binding.answer4TextView, 3, binding.expand4ImageView)
        }

        return binding.root
    }

    private fun setItemVisibility(answer: TextView, i: Int, icon: ImageView) {
        if (isAnswerVisible[i]) {
            answer.visibility = View.GONE
            isAnswerVisible[i] = false
            icon.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_expand_more_24,
                    context?.theme
                )
            )
        } else {
            answer.visibility = View.VISIBLE
            isAnswerVisible[i] = true
            icon.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_expand_less_24,
                    context?.theme
                )
            )
        }
    }

    private fun faqFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentFaqBinding.inflate(inflater, container, false)
        binding.sharedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}