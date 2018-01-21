package com.wverlaek.cambridgehack.database.models.github

/**
 * Created by WVerl on 21-1-2018.
 */
data class User(val login: String, val avatar_url: String, val repos_url: String, val public_repos: Int, val html_url: String, val bio: String?)