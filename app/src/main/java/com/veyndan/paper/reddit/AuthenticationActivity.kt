package com.veyndan.paper.reddit

import android.content.Intent
import android.databinding.DataBindingUtil
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.trello.navi2.Event
import com.trello.navi2.rx.RxNavi
import com.veyndan.paper.reddit.databinding.AuthenticationActivityBinding
import okhttp3.HttpUrl
import org.apache.commons.text.CharacterPredicates
import org.apache.commons.text.RandomStringGenerator
import timber.log.Timber

class AuthenticationActivity : BaseActivity() {

    companion object {

        private val ERROR_ACCESS_DENIED: String = "access_denied"
        private val ERROR_UNSUPPORTED_RESPONSE_TYPE: String = "unsupported_response_type"
        private val ERROR_INVALID_SCOPE: String = "invalid_scope"
        private val ERROR_INVALID_REQUEST: String = "invalid_request"

        // https://www.reddit.com/api/v1/scopes
        private val SCOPES: Array<String> = arrayOf(
                "edit", "flair", "history", "identity", "modconfig", "modflair", "modlog", "modposts",
                "modwiki", "mysubreddits", "privatemessages", "read", "report", "save", "submit",
                "subscribe", "vote", "wikiedit", "wikiread")
    }

    init {
        RxNavi.observe(this, Event.CREATE)
                .takeUntil(RxNavi.observe(this, Event.DESTROY))
                .subscribe {
                    val binding: AuthenticationActivityBinding = DataBindingUtil.setContentView(this, R.layout.authentication_activity)

                    // If previously logged in from another account, clears cookies so account is logged out.
                    val cookieManager: CookieManager = CookieManager.getInstance()
                    cookieManager.removeAllCookies(null)

                    val state: String = RandomStringGenerator.Builder()
                            .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                            .build()
                            .generate(16)

                    binding.webView.setWebViewClient(object : WebViewClient() {
                        @Suppress("OverridingDeprecatedMember")
                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                            if (url!!.startsWith(Constants.REDDIT_REDIRECT_URI)) {
                                val redirectUrl: HttpUrl = HttpUrl.parse(url)

                                if (state != redirectUrl.queryParameter("state")) {
                                    Timber.e("This app didn't initiate the authorization request. " +
                                            "Authorization request will not be carried out.")
                                    return false
                                }

                                val error: String? = redirectUrl.queryParameter("error")
                                if (error != null) {
                                    when (error) {
                                        ERROR_ACCESS_DENIED -> Toast.makeText(view!!.context, R.string.login_aborted, Toast.LENGTH_LONG).show()
                                        ERROR_UNSUPPORTED_RESPONSE_TYPE -> throw IllegalStateException("Invalid response_type: Ensure that the response_type parameter is one of the allowed values")
                                        ERROR_INVALID_SCOPE -> throw IllegalStateException ("Invalid scope parameter: Ensure that the scope parameter is a space-separated list of valid scopes")
                                        ERROR_INVALID_REQUEST -> throw IllegalStateException ("Invalid request: Double check url parameters")
                                        else -> throw IllegalStateException ("Unknown error type")
                                    }

                                    setResult(RESULT_CANCELED)
                                } else {
                                    val data: Intent = Intent()
                                    data.putExtra("code", redirectUrl.queryParameter("code"))
                                    setResult(RESULT_OK, data)
                                }

                                finish()
                                return true
                            }
                            return false
                        }
                    })

                    val url: HttpUrl = HttpUrl.parse("https://www.reddit.com/api/v1/authorize.compact")
                            .newBuilder()
                            .addQueryParameter("client_id", BuildConfig.REDDIT_API_KEY)
                            .addQueryParameter("response_type", "code")
                            .addQueryParameter("state", state)
                            .addQueryParameter("redirect_uri", Constants.REDDIT_REDIRECT_URI)
                            .addQueryParameter("duration", "permanent")
                            .addQueryParameter("scope", SCOPES.joinToString(","))
                            .build()

                    binding.webView.loadUrl(url.toString())
                }
    }
}
