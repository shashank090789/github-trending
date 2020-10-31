package com.androidiots.nayan_assignment.network.model

import androidx.room.Embedded
import com.google.gson.annotations.SerializedName

data class Repo(
    val id: Int,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("full_name")
    val fullName: String,
    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("owner")
    @field:Embedded(prefix = "owner_")
    val owner: Owner,
    @field:SerializedName("stargazers_count")
    val stars: Int,
    @field:SerializedName("language")
    val language: String?
) {

    data class Owner(
        @field:SerializedName("login")
        val login: String,
        @field:SerializedName("url")
        val url: String?,
        @field:SerializedName("avatar_url")
        val avatarUrl: String?
    )

    companion object {
        const val UNKNOWN_ID = -1
    }
}