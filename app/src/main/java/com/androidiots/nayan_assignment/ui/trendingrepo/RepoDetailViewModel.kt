package com.androidiots.nayan_assignment.ui.trendingrepo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidiots.nayan_assignment.network.model.Repo
import javax.inject.Inject

class RepoDetailViewModel @Inject constructor() :
    ViewModel() {
    private var _repoLiveData: MutableLiveData<Repo>? = null

    val repoLiveData: MutableLiveData<Repo>
        get() {
            if (_repoLiveData == null) {
                _repoLiveData = MutableLiveData()
            }
            return _repoLiveData ?: throw AssertionError("Set to null ")
        }
}