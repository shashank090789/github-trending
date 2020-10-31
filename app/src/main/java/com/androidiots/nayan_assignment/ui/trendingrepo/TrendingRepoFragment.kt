package com.androidiots.nayan_assignment.ui.trendingrepo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidiots.nayan_assignment.AppExecutors
import com.androidiots.nayan_assignment.MainActivity
import com.androidiots.nayan_assignment.R
import com.androidiots.nayan_assignment.binding.FragmentDataBindingComponent
import com.androidiots.nayan_assignment.databinding.TrendingRepoFragmentBinding
import com.androidiots.nayan_assignment.di.Injectable
import com.androidiots.nayan_assignment.network.model.Repo
import com.androidiots.nayan_assignment.ui.common.RetryCallback
import com.androidiots.nayan_assignment.utils.autoCleared
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class TrendingRepoFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var onRepoItemClickListener: OnRepoItemClickListener? = null

    companion object {
        fun newInstance() = TrendingRepoFragment()
    }

    var adapter by autoCleared<RepoListAdapter>()

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<TrendingRepoFragmentBinding>()

    var repoListData: MutableList<Repo> = mutableListOf()

    val viewModel: TrendingRepoViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.trending_repo_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        initRecyclerView()
        val rvAdapter = RepoListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors,
            showFullName = true
        ) { repo ->
            onRepoItemClickListener?.onRepoClick(repo)
        }
        binding.repoList.adapter = rvAdapter
        adapter = rvAdapter
        adapter.submitList(repoListData)
        binding.callback = object : RetryCallback {
            override fun retry() {

            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            onRepoItemClickListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        onRepoItemClickListener = null
    }

    private fun initRecyclerView() {
        binding.repoList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) {
                    viewModel.loadNextPage()
                }
            }
        })
        binding.searchResult = viewModel.repoListResLiveData
        viewModel.repoListResLiveData.observe(viewLifecycleOwner, Observer { result ->
            result.data?.toMutableList()?.let { repoListData.addAll(it) }
            adapter.notifyDataSetChanged()
        })

        viewModel.loadMoreStatus.observe(viewLifecycleOwner, Observer { loadingMore ->
            if (loadingMore == null) {
                binding.loadingMore = false
            } else {
                binding.loadingMore = loadingMore.isRunning
                val error = loadingMore.errorMessageIfNotHandled
                if (error != null) {
                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    interface OnRepoItemClickListener {
        fun onRepoClick(repo: Repo)
    }
}