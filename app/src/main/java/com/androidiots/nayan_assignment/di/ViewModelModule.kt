package com.androidiots.nayan_assignment.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.androidiots.nayan_assignment.ui.trendingrepo.RepoDetailViewModel
import com.androidiots.nayan_assignment.ui.trendingrepo.TrendingRepoViewModel
import com.androidiots.nayan_assignment.viewmodel.GithubViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: GithubViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TrendingRepoViewModel::class)
    abstract fun bindTrendingRepoViewModel(trendingRepoViewModel: TrendingRepoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RepoDetailViewModel::class)
    abstract fun bindRepoDetailViewModel(repoDetailViewModel: RepoDetailViewModel): ViewModel
}