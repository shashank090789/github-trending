package com.androidiots.nayan_assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.androidiots.nayan_assignment.network.model.Repo
import com.androidiots.nayan_assignment.ui.trendingrepo.RepoDetailViewModel
import com.androidiots.nayan_assignment.ui.trendingrepo.TrendingRepoFragment
import com.androidiots.nayan_assignment.ui.trendingrepo.TrendingRepoFragmentDirections
import com.androidiots.nayan_assignment.ui.trendingrepo.TrendingRepoViewModel
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector,
    TrendingRepoFragment.OnRepoItemClickListener {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val repoDetailViewModel: RepoDetailViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector
    override fun onRepoClick(repo: Repo) {
        findNavController(this,R.id.container).navigate(
            TrendingRepoFragmentDirections.showRepoDetail())
        repoDetailViewModel.repoLiveData.value = repo
    }
}