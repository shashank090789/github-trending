package com.androidiots.nayan_assignment.ui.trendingrepo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.androidiots.nayan_assignment.R
import com.androidiots.nayan_assignment.binding.FragmentDataBindingComponent
import com.androidiots.nayan_assignment.databinding.RepoDetailFragmentBinding
import com.androidiots.nayan_assignment.di.Injectable
import com.androidiots.nayan_assignment.utils.autoCleared
import javax.inject.Inject

class RepoDetailFragment : Fragment(), Injectable {

    companion object {
        fun newInstance() = RepoDetailFragment()
    }

    var binding by autoCleared<RepoDetailFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel: RepoDetailViewModel by activityViewModels {
        viewModelFactory
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.repo_detail_fragment,
            container,
            false,
            dataBindingComponent
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.repoLiveData.observe(viewLifecycleOwner, Observer { repo->
            binding.repo = repo
            binding.executePendingBindings()
        })
    }
}