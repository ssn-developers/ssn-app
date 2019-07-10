package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class LibraryActivity extends AppCompatActivity {


    WebView webView;
    ProgressBar pb_browser;
    ImageView iv_icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        initUI();

    }



    public void initUI(){
        webView=findViewById(R.id.wv_library);
        pb_browser=findViewById(R.id.pb_browser);
        iv_icon=findViewById(R.id.iv_browserIcon);
        pb_browser.setMax(100);
    }


    @Override
    protected void onResume() {
        super.onResume();

        webView.loadUrl("http://opac.ssn.net:8081/");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                pb_browser.setProgress(newProgress);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                iv_icon.setImageBitmap(icon);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
    }
}
