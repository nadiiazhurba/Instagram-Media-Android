package com.example.instagrammedia

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient

class AuthenticationDialog(context: Context) : Dialog(context) {
    private var request_url: String? = null
    private var redirect_url: String? = null
    private var listener: AuthenticationListener? = null

    fun AuthenticationDialog(
        context: Context,
        listener: AuthenticationListener?
    ) {
        this.listener = listener
        redirect_url = context.resources.getString(R.string.redirect_url)
        request_url = context.resources.getString(R.string.base_url) +
                "oauth/authorize/?client_id=" +
                context.resources.getString(R.string.client_id) +
                "&redirect_uri=" + redirect_url +
                "&response_type=token&display=touch&scope=public_content"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.auth_dialog)
        initializeWebView()
    }

    private fun initializeWebView() {
        val webView: WebView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(request_url!!)
        webView.webViewClient = webViewClient
    }

    var webViewClient: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith(redirect_url!!)) {
                dismiss()
                return true
            }
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (url.contains("access_token=")) {
                val uri: Uri = Uri.parse(url)
                var accessToken: String? = uri.encodedFragment
                accessToken = accessToken?.substring(accessToken.lastIndexOf("=") + 1)
                Log.e("access_token", accessToken.orEmpty())
                listener!!.onTokenReceived(accessToken)
                dismiss()
            } else if (url.contains("?error")) {
                Log.e("access_token", "getting error fetching access token")
                dismiss()
            }
        }
    }
}