package com.androidiots.nayan_assignment.di

import com.androidiots.nayan_assignment.ui.trendingrepo.RepoDetailFragment
import com.androidiots.nayan_assignment.ui.trendingrepo.TrendingRepoFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeTrendingRepoFragment(): TrendingRepoFragment

    @ContributesAndroidInjector
    abstract fun contributeRepoDetailFragment(): RepoDetailFragment
}