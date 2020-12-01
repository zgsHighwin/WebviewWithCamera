package com.zgsHighwin.webviewplayvideo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zgsHighwin.webviewplayvideo.Config;
import com.zgsHighwin.webviewplayvideo.R;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivityWebView";

    private WebView webView;
    private PermissionRequest permissionRequest;
    private EditText et;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initView();
    }

    public static String getDeviceVersionCode() {
        return Build.BRAND + " " + Build.MODEL + " " + Build.VERSION.RELEASE;
    }

    public void bringToTop() {
        findViewById(R.id.tv).bringToFront();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        webView = ((WebView) findViewById(R.id.web_view));
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        et = ((EditText) findViewById(R.id.et));
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_SHORT).show();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setDomStorageEnabled(true); //设置支持localstorage
        settings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        settings.setBuiltInZoomControls(false); //设置内置的缩放控件。若为false，则该WebView不可缩放
        settings.setDisplayZoomControls(true); //隐藏原生的缩放控件
        settings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式
        settings.setAllowFileAccess(true);


        webView.setBackgroundColor(0x00000000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        String userAgentString = settings.getUserAgentString();
        String deviceInfo = getDeviceVersionCode();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                Log.i("MainActivity", "MainActivity Method shouldOverrideUrlLoading url:" + url);
                if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.i("MainActivity", "MainActivity Method onReceivedError");
            }

            @Override
            public void onReceivedError(final WebView view, final WebResourceRequest request, final WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.i("MainActivity", "MainActivity Method onReceivedError");
            }

            @Override
            public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        webView.loadUrl("https://justadudewhohacks.github.io/face-api.js/webcam_face_tracking/");

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Log.i(TAG, url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            private View mCustomView;
            private WebChromeClient.CustomViewCallback mCustomViewCallback;
            protected FrameLayout mFullscreenContainer;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;

            public Bitmap getDefaultVideoPoster() {
                Log.i(TAG, "getDefaultVideoPoster");
                if (mCustomView == null) {
                    return null;
                }
                return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
            }

            public void onHideCustomView() {
                Log.i(TAG, "onHideCustomView");
                ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
                this.mCustomView = null;
                getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                setRequestedOrientation(this.mOriginalOrientation);
                this.mCustomViewCallback.onCustomViewHidden();
                this.mCustomViewCallback = null;
            }

            @Override
            public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
                Log.i(TAG, "onShowCustomView");
                bringToTop();
                if (this.mCustomView != null) {
                    onHideCustomView();
                    return;
                }
                this.mCustomView = paramView;
                this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                this.mOriginalOrientation = getRequestedOrientation();
                this.mCustomViewCallback = paramCustomViewCallback;
                mCustomView.setBackgroundColor(Color.BLACK);
                ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
                getWindow().getDecorView().setSystemUiVisibility(3846);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }


            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) //支持5.0以上
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                permissionRequest = request;
                permissionRequest.grant(request.getResources());
//                String[] resources = request.getResources();
//                for (String permission : resources) {
//                    Log.i("MainActivity", "MainActivity onPermissionRequest\tresource:" + permission);
//                    switch (permission) {
//                        case "android.webkit.resource.VIDEO_CAPTURE":
//                            askForPermission(request.getOrigin().toString(), Manifest.permission.CAMERA, Config.PERMISSION_REQUEST_CODE_CAMERA);
//                            break;
//                        case "resource:android.webkit.resource.AUDIO_CAPTURE":
//                            askForPermission(request.getOrigin().toString(), Manifest.permission.RECORD_AUDIO, Config.PERMISSION_REQUEST_CODE_AUDIO_RECORD);
//                            break;
//                    }
//                }
//
//
////                super.onPermissionRequest(request);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void askForPermission(String origin, String permission, int requestCode) {
        Log.i("MainActivity", "inside askForPermission for" + origin + "with" + permission);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i("", "MainActivity Method askForPermission");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    permission)) {
                Log.i(TAG, "MainActivity askForPermission");
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{permission},
                        requestCode);
            }
        } else {
            permissionRequest.grant(permissionRequest.getResources());
        }
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else
            super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case Config.PERMISSION_REQUEST_CODE_AUDIO_RECORD:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionRequest.grant(permissionRequest.getResources());
//                    webView.reload();//
                }
                break;
            case Config.PERMISSION_REQUEST_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionRequest.grant(permissionRequest.getResources());
//                    webView.reload();//
                }
                break;
        }

//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
