package com.epam.talks.github.search

import com.epam.talks.github.model.ApiClient

class SuspendingRepositoriesPresenter(private val view: RepositoriesView, val apiClient: ApiClient) : RepositoriesPresenter {

    override suspend fun searchRepositories(query: String) {
        val foundRepositories = apiClient.searchRepositories(query).await()
        val list = foundRepositories.map { it.full_name }
        view.showRepositoryList(list)
    }
}