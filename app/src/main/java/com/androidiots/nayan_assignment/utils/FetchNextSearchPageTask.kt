package com.androidiots.nayan_assignment.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androidiots.nayan_assignment.network.*
import com.androidiots.nayan_assignment.network.model.Resource
import java.io.IOException

class FetchNextSearchPageTask constructor(
    private val query: String,
    private val githubService: GithubService,
    private val nextPage : Int?
) : Runnable {
    private val _liveData = MutableLiveData<Resource<ApiResponse<RepoSearchResponse>>>()
    val liveData: MutableLiveData<Resource<ApiResponse<RepoSearchResponse>>> = _liveData

    override fun run() {
        if (nextPage == null) {
            _liveData.postValue(Resource.error("false",null))
            return
        }
        val newValue = try {
            val response = githubService.searchRepos(query, nextPage).execute()
            when (val apiResponse = ApiResponse.create(response)) {
                is ApiSuccessResponse -> {
                    Resource.success(apiResponse)
                }
                is ApiEmptyResponse -> {
                    Resource.success(apiResponse)
                }
                is ApiErrorResponse -> {
                    Resource.error(apiResponse.errorMessage, apiResponse)
                }
            }

        } catch (e: IOException) {
            Resource.error(e.message!!, null)
        }
        _liveData.postValue(newValue)
    }
}