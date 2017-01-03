package com.veyndan.paper.reddit.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.veyndan.paper.reddit.Config;
import com.veyndan.paper.reddit.Constants;
import com.veyndan.paper.reddit.R;
import com.veyndan.paper.reddit.databinding.ActivityAuthenticatorBinding;

import org.apache.commons.lang3.RandomStringUtils;

import okhttp3.HttpUrl;
import timber.log.Timber;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    public static final String ARG_ACCOUNT_TYPE = "arg_account_type";
    public static final String ARG_AUTH_TYPE = "arg_auth_type";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "arg_is_adding_new_account";

    public static final String PARAM_USER_PASS = "USER_PASS";

    private static final String ERROR_ACCESS_DENIED = "access_denied";
    private static final String ERROR_UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";
    private static final String ERROR_INVALID_SCOPE = "invalid_scope";
    private static final String ERROR_INVALID_REQUEST = "invalid_request";

    // https://www.reddit.com/api/v1/scopes
    private static final String[] SCOPES = {
            "edit", "flair", "history", "identity", "modconfig", "modflair", "modlog", "modposts",
            "modwiki", "mysubreddits", "privatemessages", "read", "report", "save", "submit",
            "subscribe", "vote", "wikiedit", "wikiread"};

    private AccountManager accountManager;
    private String authTokenType;

    @Override
    protected void onCreateNonNull(@NonNull final Bundle savedInstanceState) {
        final ActivityAuthenticatorBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_authenticator);

        accountManager = AccountManager.get(this);

        // If previously logged in from another account, clears cookies so account is logged out.
        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);

        final String state = RandomStringUtils.randomAlphanumeric(16);

        authTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (authTokenType == null) {
            authTokenType = TextUtils.join(",", SCOPES);
        }

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

                        final Intent res = new Intent();
                        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, "kingjulien1");
                        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, getIntent().getStringExtra(ARG_ACCOUNT_TYPE));
                        res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);

                        finishLogin(res);
                    }
                    return true;
                }
                return false;
            }
        });

        final HttpUrl url = HttpUrl.parse("https://www.reddit.com/api/v1/authorize.compact")
                .newBuilder()
                .addQueryParameter("client_id", Config.REDDIT_CLIENT_ID)
                .addQueryParameter("response_type", "code")
                .addQueryParameter("state", state)
                .addQueryParameter("redirect_uri", Constants.REDDIT_REDIRECT_URI)
                .addQueryParameter("duration", "permanent")
                .addQueryParameter("scope", TextUtils.join(",", SCOPES))
                .build();

        binding.webView.loadUrl(url.toString());
    }

    private void finishLogin(final Intent intent) {
        final String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        final String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            final String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            final String authtokenType = authTokenType;
            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            accountManager.addAccountExplicitly(account, accountPassword, null);
            accountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            accountManager.setPassword(account, accountPassword);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }
}
