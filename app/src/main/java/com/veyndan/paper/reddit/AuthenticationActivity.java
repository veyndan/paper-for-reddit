package com.veyndan.paper.reddit;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.trello.navi2.Event;
import com.trello.navi2.rx.RxNavi;
import com.veyndan.paper.reddit.databinding.AuthenticationActivityBinding;

import org.apache.commons.lang3.RandomStringUtils;

import okhttp3.HttpUrl;
import timber.log.Timber;

public class AuthenticationActivity extends BaseActivity {

    private static final String ERROR_ACCESS_DENIED = "access_denied";
    private static final String ERROR_UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";
    private static final String ERROR_INVALID_SCOPE = "invalid_scope";
    private static final String ERROR_INVALID_REQUEST = "invalid_request";

    // https://www.reddit.com/api/v1/scopes
    private static final String[] SCOPES = {
            "edit", "flair", "history", "identity", "modconfig", "modflair", "modlog", "modposts",
            "modwiki", "mysubreddits", "privatemessages", "read", "report", "save", "submit",
            "subscribe", "vote", "wikiedit", "wikiread"};

    public AuthenticationActivity() {
        RxNavi.observe(this, Event.CREATE)
                .takeUntil(RxNavi.observe(this, Event.DESTROY))
                .subscribe(savedInstanceState -> {
                    final AuthenticationActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.authentication_activity);

                    // If previously logged in from another account, clears cookies so account is logged out.
                    final CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeAllCookies(null);

                    final String state = RandomStringUtils.randomAlphanumeric(16);

                    binding.webView.setWebViewClient(new WebViewClient() {
                        @SuppressWarnings("deprecation")
                        @Override
                        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                            if (url.startsWith(Constants.REDDIT_REDIRECT_URI)) {
                                final HttpUrl redirectUrl = HttpUrl.parse(url);

                                if (!state.equals(redirectUrl.queryParameter("state"))) {
                                    Timber.e("This app didn't initiate the authorization request. " +
                                            "Authorization request will not be carried out.");
                                    return false;
                                }

                                final String error = redirectUrl.queryParameter("error");
                                if (error != null) {
                                    switch (error) {
                                        case ERROR_ACCESS_DENIED:
                                            Toast.makeText(view.getContext(), R.string.login_aborted, Toast.LENGTH_LONG).show();
                                            break;
                                        case ERROR_UNSUPPORTED_RESPONSE_TYPE:
                                            throw new IllegalStateException("Invalid response_type: " +
                                                    "Ensure that the response_type parameter is one of the " +
                                                    "allowed values");
                                        case ERROR_INVALID_SCOPE:
                                            throw new IllegalStateException("Invalid scope parameter: " +
                                                    "Ensure that the scope parameter is a space-separated " +
                                                    "list of valid scopes");
                                        case ERROR_INVALID_REQUEST:
                                            throw new IllegalStateException("Invalid request: " +
                                                    "Double check url parameters");
                                        default:
                                            throw new IllegalStateException("Unknown error type");
                                    }

                                    setResult(RESULT_CANCELED);
                                } else {
                                    final Intent data = new Intent();
                                    data.putExtra("code", redirectUrl.queryParameter("code"));
                                    setResult(RESULT_OK, data);
                                }

                                finish();
                                return true;
                            }
                            return false;
                        }
                    });

                    final HttpUrl url = HttpUrl.parse("https://www.reddit.com/api/v1/authorize.compact")
                            .newBuilder()
                            .addQueryParameter("client_id", BuildConfig.REDDIT_API_KEY)
                            .addQueryParameter("response_type", "code")
                            .addQueryParameter("state", state)
                            .addQueryParameter("redirect_uri", Constants.REDDIT_REDIRECT_URI)
                            .addQueryParameter("duration", "permanent")
                            .addQueryParameter("scope", TextUtils.join(",", SCOPES))
                            .build();

                    binding.webView.loadUrl(url.toString());
                });
    }
}
