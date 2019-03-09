package com.services.airx.airexpresslk;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public WebView myWebView;
    private ProgressBar progressBar;
    private FrameLayout frameLayout;
    Connection_Detector connection_detector;
    SwipeRefreshLayout swipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {




        connection_detector=new Connection_Detector(this);


        if (connection_detector.isConnected())
        {
            Toast.makeText(this, "Connected ", Toast.LENGTH_LONG).show();

        }else
        {
            Toast.makeText(this, "NOT Connected ", Toast.LENGTH_LONG).show();
            //buildDialog(MainActivity.this).show();
        }

        //............................................
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        swipe=(SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadWeb();
            }
        });

        loadWeb();


    }

    public void loadWeb() {
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setMax(100);


        myWebView = (WebView) findViewById(R.id.webview);

        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                frameLayout.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);

                //setTitle("Loading.....");

                if (progress == 100) {
                    frameLayout.setVisibility(WebView.GONE);
                    //setTitle(view.getTitle());
                }
                super.onProgressChanged(view, progress);

            }



        });


        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setVerticalScrollBarEnabled(false);
        myWebView.getSettings().setAppCacheEnabled(true);
        myWebView.loadUrl("http://www.airexpress.lk");
        progressBar.setProgress(0);

        myWebView.setWebViewClient(new WebViewClient(){

            //call function
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith("tel:")) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }



            public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
                try {
                    webView.stopLoading();
                } catch (Exception e) {
                }

                if (webView.canGoBack()) {
                    webView.goBack();
                }

                webView.loadUrl("about:blank");
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Check your internet connection and try again.");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }
                });

                alertDialog.show();
                super.onReceivedError(webView, errorCode, description, failingUrl);
            }



            public void onPageFinished(WebView view, String url){
                //Hide the Swipe refresh

                swipe.setRefreshing(false);

            }



        });


    }

    private class HelpClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            frameLayout.setVisibility(View.VISIBLE);
            return true;
        }
    }
    //........................................................................


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(myWebView.canGoBack()){
                myWebView.goBack();
                return true;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            MainActivity.super.onBackPressed();
                        }
                    }).create().show();

        }

        return super.onKeyDown(keyCode, event);
    }




}
