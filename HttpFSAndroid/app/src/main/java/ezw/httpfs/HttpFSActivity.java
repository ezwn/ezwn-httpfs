package ezw.httpfs;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.util.ArrayList;
import java.util.List;

import httpfs.HttpFSService;

public class HttpFSActivity extends Activity {

    private final int httpPort = 8085;
    private final int httpsPort = 8084;
    private static final String TAG = HttpFSActivity.class.getSimpleName();
    private final String appUrl = "http://localhost:" + httpPort + "/web/index.html";

    private WebView webView;

    private String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET
    };

    class MyResourceClient extends XWalkResourceClient {
        MyResourceClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onReceivedSslError(XWalkView view, ValueCallback<Boolean> callback, SslError error) {

        }
    }

    class MyUIClient extends XWalkUIClient {
        MyUIClient(XWalkView view) {
            super(view);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> errors = new ArrayList();

        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {

                final String message = "Permission missing : " + permission;
                errors.add(message);
                Log.e(TAG, message);
            }
        }

        if (errors.isEmpty())
            startServer();

        createXWalkView(errors);
        // createWebView(errors);
    }

    private void createXWalkView(List<String> errors) {
        XWalkView xView = new XWalkView(this);

        final String patterns = "https://localhost:8084/*,http://localhost:8085/*";

        xView.setOriginAccessWhitelist(appUrl, patterns.split(","));

        xView.setResourceClient(new MyResourceClient(xView));
        xView.setUIClient(new MyUIClient(xView));
        setContentView(xView);

        if (errors.isEmpty()) {
            xView.loadUrl(appUrl);
        } else
            xView.loadUrl("file:///android_asset/web/error.html");
    }

    private void createWebView(List<String> errors) {

        webView = new WebView(this);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && (url.startsWith("browser:"))) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url.substring("browser:".length()))));
                    return true;
                } else {
                    return false;
                }
            }
        });


        final WebSettings webSettings = webView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        setContentView(webView);

        if (errors.isEmpty()) {
            // startMenuInWebView();
            webView.loadUrl(appUrl);
        } else
            webView.loadUrl("file:///android_asset/web/error.html");
    }

    private void startServer() {

        final String rootPath
                = new java.io.File(
                android.os.Environment.getExternalStorageDirectory(),
                "httpfs/olang"
        ).getAbsolutePath();

        HttpFSService.boot(rootPath, httpPort, false);
        HttpFSService.boot(rootPath, httpsPort, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
