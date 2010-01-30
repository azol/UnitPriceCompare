package leoliang.unitpricecompare;

import java.util.Locale;

import leoliang.android.lib.crashreport.CrashMonitor;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.flurry.android.FlurryAgent;

public class HelpActivity extends Activity {

    private static final String LOG_TAG = "UnitPriceCompare";

    @Override
    public void onStart() {
        Log.v(LOG_TAG, "onStart");
        super.onStart();
        CrashMonitor.monitor(this);
        FlurryAgent.setCaptureUncaughtExceptions(false);
        FlurryAgent.onStartSession(this, "5Q82B7WVG6DAIHNFF649");
    }

    @Override
    public void onStop() {
        Log.v(LOG_TAG, "onStop");
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        WebView browser = (WebView) findViewById(R.id.webView);
        browser.loadUrl(getHelpUrl());
    }

    private String getHelpUrl() {
        String prefix = "file:///android_asset/";
        String languageCode = Locale.getDefault().getLanguage();
        if (languageCode.equals("zh")) {
            return prefix + "help.zh.html";
        }
        return prefix + "help.html";
    }
}
