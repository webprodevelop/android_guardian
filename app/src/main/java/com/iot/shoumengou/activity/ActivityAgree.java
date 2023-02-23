//@formatter:off
package com.iot.shoumengou.activity;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;

public class ActivityAgree extends ActivityBase {
	private final String TAG = "ActivityAgree";

	private ImageView	mBackImg;

	private boolean		mAgreementOrPolicy = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agree);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		mAgreementOrPolicy = getIntent().getBooleanExtra("agreement_policy", true);

		initControls();
		setEventListener();
	}

	@Override
	protected void initControls() {
		super.initControls();

		mBackImg = findViewById(R.id.ID_IMGVIEW_BACK);
		TextView mTitleView = findViewById(R.id.ID_TXTVIEW_TITLE);
		WebView mWebView = findViewById(R.id.ID_WEBVIEW_CONTENT);
		mWebView.setWebViewClient(new WebViewClient());
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
		mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
		mWebView.setWebChromeClient(new WebChromeClient());
		//mContentView.loadUrl("http://uriminzokkiri.com/index.php?ptype=curitv&mtype=view&no=52351#pos");
//		mWebView.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;}</style>" + itemDiscover.content, "text/html", "UTF-8", null);

		try {
			if (mAgreementOrPolicy) {
				mTitleView.setText(R.string.str_agree_user_agreement);

				String strAgreement = Prefs.Instance().getAgreement();
				mWebView.loadDataWithBaseURL(null, "<style>body{padding:10px;}</style>" + strAgreement, "text/html", "UTF-8", null);
			} else {
				mTitleView.setText(R.string.str_agree_privacy_policy);

				String strPolicy = Prefs.Instance().getPolicy();
				mWebView.loadDataWithBaseURL(null, strPolicy, "text/html", "UTF-8", null);
			}
		}catch (Exception ex){
			System.out.println(ex.getMessage());
		}
	}

	@Override
	protected void setEventListener() {
		mBackImg.setOnClickListener(view -> finish());
	}
}
