package com.androidiots.nayan_assignment.network

import com.androidiots.nayan_assignment.network.model.Repo
import com.google.gson.annotations.SerializedName

data class RepoSearchResponse(
    @SerializedName("total_count")
    val total: Int = 0,
    @SerializedName("items")
    val items: List<Repo>
) {
    var nextPage: Int? = null
}
