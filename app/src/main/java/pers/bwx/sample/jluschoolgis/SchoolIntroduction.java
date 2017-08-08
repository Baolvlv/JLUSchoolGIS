package pers.bwx.sample.jluschoolgis;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

public class SchoolIntroduction extends AppCompatActivity {

    private Toolbar myToolBar;

    //展示学校简介的webView
    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_introduction);
        //初始化toolbar
        initToolBar();

        myWebView = (WebView) findViewById(R.id.introduction_web);
       //myWebView.loadUrl("file:///android_asset/school.html");
        myWebView.loadUrl("http://www.jlu.edu.cn/xxgk/jdjj.htm");
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setSaveFormData(true);
        myWebView.getSettings().setBuiltInZoomControls(true);




    }

    /***
     * 初始化toolBar标题与返回按钮
     */
    public void initToolBar(){
        //初始化toolbar,设置标题
        myToolBar = (Toolbar) findViewById(R.id.sIToolbar);
        myToolBar.setTitle("");
        setSupportActionBar(myToolBar);

        //返回上级actionbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
