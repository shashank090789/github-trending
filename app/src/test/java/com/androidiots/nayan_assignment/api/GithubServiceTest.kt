package com.androidiots.nayan_assignment.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.androidiots.nayan_assignment.network.ApiSuccessResponse
import com.androidiots.nayan_assignment.network.GithubService
import com.androidiots.nayan_assignment.util.getOrAwaitValue
import com.androidiots.nayan_assignment.utils.LiveDataCallAdapterFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.hamcrest.CoreMatchers
import org.hamcrest.core.IsNull
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class GithubServiceTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: GithubService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(GithubService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun search() {
        val next = """<https://api.github.com/search/repositories?q=foo&page=2>; rel="next""""
        val last = """<https://api.github.com/search/repositories?q=foo&page=34>; rel="last""""
        enqueueResponse(
            "search.json", mapOf(
                "link" to "$next,$last"
            )
        )
        val response = service.searchRepos("foo").getOrAwaitValue() as ApiSuccessResponse

        Assert.assertThat(response, IsNull.notNullValue())
        Assert.assertThat(response.body.total, CoreMatchers.`is`(41))
        Assert.assertThat(response.body.items.size, CoreMatchers.`is`(30))
        Assert.assertThat<String>(
            response.links["next"],
            CoreMatchers.`is`("https://api.github.com/search/repositories?q=foo&page=2")
        )
        Assert.assertThat<Int>(response.nextPage, CoreMatchers.`is`(2))
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader!!
            .getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }
}