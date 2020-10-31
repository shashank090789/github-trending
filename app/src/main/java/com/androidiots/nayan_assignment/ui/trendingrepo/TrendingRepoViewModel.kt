package com.androidiots.nayan_assignment.ui.trendingrepo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.androidiots.nayan_assignment.AppExecutors
import com.androidiots.nayan_assignment.network.ApiResponse
import com.androidiots.nayan_assignment.network.ApiSuccessResponse
import com.androidiots.nayan_assignment.network.GithubService
import com.androidiots.nayan_assignment.network.RepoSearchResponse
import com.androidiots.nayan_assignment.network.model.Repo
import com.androidiots.nayan_assignment.network.model.Resource
import com.androidiots.nayan_assignment.network.model.Status
import com.androidiots.nayan_assignment.utils.FetchNextSearchPageTask
import com.androidiots.nayan_assignment.utils.NetworkBoundResource
import com.androidiots.nayan_assignment.utils.QUERY_API
import timber.log.Timber
import javax.inject.Inject

class TrendingRepoViewModel @Inject constructor(
    private val appExecutors: AppExecutors,
    private val githubService: GithubService
) :
    ViewModel() {
    private val nextPageHandler = NextPageHandler()
    private var _repoListResLiveData: MutableLiveData<Resource<List<Repo>>>? = null
    private var nextPage: Int? = null
    val repoListResLiveData: LiveData<Resource<List<Repo>>>
        get() {
            if (_repoListResLiveData == null) {
                _repoListResLiveData = fetchTrendingRepo()
            }
            return _repoListResLiveData ?: throw AssertionError("Set to null by another thread")
        }


    private fun fetchTrendingRepo(): MutableLiveData<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, RepoSearchResponse>(appExecutors) {
            override fun createCall() = githubService.searchRepos(QUERY_API)

            override fun getData(item: RepoSearchResponse): LiveData<List<Repo>> {
                var repoListLiveData = MutableLiveData<List<Repo>>()
                repoListLiveData.value = item.items
                nextPage = item.nextPage
                return repoListLiveData
            }

            override fun processResponse(response: ApiSuccessResponse<RepoSearchResponse>)
                    : RepoSearchResponse {
                val body = response.body
                body.nextPage = response.nextPage
                return body
            }

        }.asLiveData()
    }

    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState

    fun loadNextPage() {
        nextPageHandler.queryNextPage(
            query = QUERY_API,
            githubService = githubService,
            nextPage = nextPage,
            appExecutors = appExecutors,
            repoLiveData = _repoListResLiveData
        )
    }

    fun refresh() {

    }

    class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
        private var handledError = false

        val errorMessageIfNotHandled: String?
            get() {
                if (handledError) {
                    return null
                }
                handledError = true
                return errorMessage
            }
    }

    class NextPageHandler : Observer<Resource<ApiResponse<RepoSearchResponse>>> {
        private var nextPageLiveData: MutableLiveData<Resource<ApiResponse<RepoSearchResponse>>>? = null
        val loadMoreState = MutableLiveData<LoadMoreState>()
        private var query: String? = null
        private var _hasMore: Boolean = false
        private var _nextPage: Int? = null
        private var _repoListResLiveData: MutableLiveData<Resource<List<Repo>>>? = null
        val hasMore
            get() = _hasMore

        init {
            reset()
        }

        fun queryNextPage(
            query: String,
            githubService: GithubService,
            nextPage: Int?,
            appExecutors: AppExecutors,
            repoLiveData: MutableLiveData<Resource<List<Repo>>>?
        ) {
            _repoListResLiveData = repoLiveData
            if (this.query == query) {
                return
            }
            unregister()
            this.query = query
            if (_nextPage == null) {
                _nextPage = nextPage
            }
            Timber.d("Next Page:$_nextPage")
            val fetchNextSearchPageTask = FetchNextSearchPageTask(
                query = query,
                githubService = githubService,
                nextPage = _nextPage
            )
            appExecutors.networkIO().execute(fetchNextSearchPageTask)
            nextPageLiveData = fetchNextSearchPageTask.liveData
            loadMoreState.value = LoadMoreState(
                isRunning = true,
                errorMessage = null
            )
            nextPageLiveData?.observeForever(this)
        }

        override fun onChanged(result: Resource<ApiResponse<RepoSearchResponse>>?) {
            if (result == null) {
                reset()
            } else {
                when (result.status) {
                    Status.SUCCESS -> {
                        _hasMore = result.data != null
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = null
                            )
                        )
                        val data = result.data
                        if (data is ApiSuccessResponse) {
                            _nextPage = data?.nextPage

                            if (_repoListResLiveData != null) {
                                _repoListResLiveData!!.value =
                                    Resource.success(data = data.body.items)
                            }

                        }

                    }
                    Status.ERROR -> {
                        _hasMore = true
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = result.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        // ignore
                    }
                }
            }
        }

        private fun unregister() {
            nextPageLiveData?.removeObserver(this)
            nextPageLiveData = null
            if (_hasMore) {
                query = null
            }
        }

        fun reset() {
            unregister()
            _hasMore = true
            loadMoreState.value = LoadMoreState(
                isRunning = false,
                errorMessage = null
            )
        }
    }
}