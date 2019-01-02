package com.epam.talks.github.search

interface RepositoriesPresenter {

    suspend fun searchRepositories(query: String)

}