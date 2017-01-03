package com.veyndan.paper.reddit;

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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.veyndan.paper.reddit.api.reddit.Reddit;
import com.veyndan.paper.reddit.api.reddit.RedditObjectDeserializer;
import com.veyndan.paper.reddit.api.reddit.model.RedditObject;
import com.veyndan.paper.reddit.api.reddit.network.AccessToken;
import com.veyndan.paper.reddit.api.reddit.network.AuthenticationService;
import com.veyndan.paper.reddit.api.reddit.network.interceptor.AuthorizationInterceptor;
import com.veyndan.paper.reddit.api.reddit.network.interceptor.UserAgentInterceptor;
import com.veyndan.paper.reddit.authentication.AccountAuthenticatorActivity;
import com.veyndan.paper.reddit.authentication.AuthenticatorActivity;
import com.veyndan.paper.reddit.databinding.AuthenticationActivityBinding;

import org.apache.commons.lang3.RandomStringUtils;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class AuthenticationActivity extends AccountAuthenticatorActivity {

    private static final String ERROR_ACCESS_DENIED = "access_denied";
    private static final String ERROR_UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";
    private static final String ERROR_INVALID_SCOPE = "invalid_scope";
    private static final String ERROR_INVALID_REQUEST = "invalid_request";

    // https://www.reddit.com/api/v1/scopes
    public static final String[] SCOPES = {
            "edit", "flair", "history", "identity", "modconfig", "modflair", "modlog", "modposts",
            "modwiki", "mysubreddits", "privatemessages", "read", "report", "save", "submit",
            "subscribe", "vote", "wikiedit", "wikiread"};

    private AccountManager accountManager;
    private String mAuthTokenType;

    @Override
    protected void onCreateNonNull(@NonNull final Bundle savedInstanceState) {
        final AuthenticationActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.authentication_activity);

        accountManager = AccountManager.get(this);

        mAuthTokenType = getIntent().getStringExtra(AuthenticatorActivity.ARG_AUTH_TYPE);
        if (mAuthTokenType == null) {
            mAuthTokenType = TextUtils.join(",", SCOPES);
        }

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
                        final String code = redirectUrl.queryParameter("code");

                        final Gson gson = new GsonBuilder()
                                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                .registerTypeAdapter(RedditObject.class, new RedditObjectDeserializer())
                                // TODO Make class for adapter
                                .registerTypeAdapter(AccessToken.class, (JsonDeserializer<AccessToken>) (json, typeOfT, context) -> {
                                    final JsonObject jsonObject = json.getAsJsonObject();
                                    final JsonElement errorJsonElement = jsonObject.get("error");
                                    if (errorJsonElement != null) {
                                        final String error1 = errorJsonElement.getAsString();
                                        // TODO Define this in enum, can move ErrorMessageBuilder there
                                        switch (error1) {
                                            case "unsupported_grant_type":
                                                throw new IllegalStateException(new Reddit.ErrorMessageBuilder()
                                                        .cause("`grant_type` parameter was invalid or Http Content type was not set correctly")
                                                        .resolution("Verify that the `grant_type` sent is supported and make sure the content type of the http message is set to `application/x-www-form-urlencoded`")
                                                        .build());
                                            case "invalid_request":
                                                throw new IllegalStateException(new Reddit.ErrorMessageBuilder()
                                                        .cause("You didn't include the `code` parameter")
                                                        .resolution("Include the `code` parameter in the POST data")
                                                        .build());
                                            case "invalid_grant":
                                                throw new IllegalStateException(new Reddit.ErrorMessageBuilder()
                                                        .cause("The `code` has expired or already been used")
                                                        .resolution("Ensure that you are not attempting to re-use old `code`s - they are one time use.")
                                                        .build());
                                            default:
                                                throw new IllegalStateException("Unknown error type: " + error1);
                                        }
                                    }

                                    // TODO Large memory footprint. context.deserialize() causes infinite loop.
                                    final Gson gsonInner = new GsonBuilder()
                                            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                            .create();

                                    return gsonInner.fromJson(json, typeOfT);
                                })
                                .create();

                        final GsonConverterFactory jsonConverterFactory = GsonConverterFactory.create(gson);

                        final RxJava2CallAdapterFactory rxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create();

                        final OkHttpClient client = new OkHttpClient();

                        final OkHttpClient.Builder authenticationClientBuilder = client.newBuilder()
                                .addInterceptor(new UserAgentInterceptor(Config.REDDIT_CREDENTIALS.getUserAgent()))
                                .addInterceptor(new AuthorizationInterceptor(Config.REDDIT_CREDENTIALS))
                                // TODO Move interceptor elsewhere
                                .addInterceptor(chain -> {
                                    final Request request = chain.request();
                                    final okhttp3.Response response = chain.proceed(request);

                                    if (response.code() == 401) {
                                        throw new IllegalStateException(new Reddit.ErrorMessageBuilder()
                                                .cause("Client credentials sent as HTTP Basic Authorization were invalid")
                                                .resolution("Verify that you are properly sending HTTP Basic Authorization headers and that your credentials are correct")
                                                .build());
                                    }

                                    return response;
                                });

                        final Retrofit authenticatorRetrofit = new Retrofit.Builder()
                                .baseUrl("https://www.reddit.com/api/v1/")
                                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                                .addConverterFactory(jsonConverterFactory)
                                .client(authenticationClientBuilder.build())
                                .build();

                        final AuthenticationService authenticationService = authenticatorRetrofit.create(AuthenticationService.class);

                        final String credential = Credentials.basic(Config.REDDIT_CLIENT_ID, "");
                        final Single<AccessToken> single = authenticationService.getAccessToken(
                                credential, "authorization_code", code, Constants.REDDIT_REDIRECT_URI);

                        single
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(accessToken -> {
                                    final Intent res = new Intent();
                                    res.putExtra(AccountManager.KEY_ACCOUNT_NAME, "kingjulien1");
                                    res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, "com.veyndan.paper.reddit");
                                    res.putExtra(AccountManager.KEY_AUTHTOKEN, accessToken.getAccessToken());
                                    res.putExtra(AuthenticatorActivity.PARAM_USER_PASS, "basketball");
                                    finishLogin(res);
                                });
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
        final String accountPassword = intent.getStringExtra(AuthenticatorActivity.PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        if (getIntent().getBooleanExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            final String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            final String authtokenType = mAuthTokenType;
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
