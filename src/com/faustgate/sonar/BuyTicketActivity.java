package com.faustgate.sonar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BuyTicketActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, TicketFinderService.class);
        stopService(intent);
        SoundNotifier.getInstance(getApplicationContext()).stopSound();

        String sesCookie = UZRequests.getInstance().getAuthCookie();

        String[] cookies = sesCookie.split(";");
        for (String cookie : cookies) {
            if (cookie.replace(" ", "").startsWith("_gv_sessid")) {
                sesCookie = cookie.replace(" ", "");
            }
        }

        setContentView(R.layout.buy_ticket_layout);

        String url = "http://booking.uz.gov.ua";

        WebView webView = (WebView) this.findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // view.setInitialScale(0);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                String sadf = "safa";
            }

        });

        CookieSyncManager.createInstance(BuyTicketActivity.this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        cookieManager.setCookie(url, sesCookie);
        cookieManager.setAcceptCookie(true);
        CookieSyncManager.getInstance().sync();

        webView.loadUrl(url + getString(R.string.cart_address));
        // webView.setInitialScale(70);
    }
}