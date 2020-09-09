package com.example.instagrammedia

interface AuthenticationListener {
    fun onTokenReceived(auth_token: String?)
}