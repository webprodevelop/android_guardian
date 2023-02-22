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
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemDiscover;
import com.iot.shoumengou.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityDiscoverDetail extends ActivityBase {
	private final String TAG = "ActivityDiscoverDetail";

	private ImageView		mBackImg;
	private TextView		mTitleView;
	private TextView		mKindView;
	private TextView		mDateView;
	private WebView			mContentView;

	private ItemDiscover	itemDiscover = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discover_detail);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		itemDiscover = (ItemDiscover) getIntent().getSerializableExtra("discover_data");
		if (itemDiscover == null) {
			finish();
		}

		initControls();
		setEventListener();

		getNewsInfo();
	}

	@Override
	protected void initControls() {
		super.initControls();

		mBackImg = findViewById(R.id.ID_IMGVIEW_BACK);
		mTitleView = findViewById(R.id.ID_TXTVIEW_TITLE);
		mKindView = findViewById(R.id.ID_TXTVIEW_KIND);
		mDateView = findViewById(R.id.ID_TXTVIEW_DATE);
		mContentView = findViewById(R.id.ID_WEBVIEW_CONTENT);
	}

	@Override
	protected void setEventListener() {
		mBackImg.setOnClickListener(view -> finish());
	}

	private void getNewsInfo() {
		if (m_dlgProgress != null) {
			m_dlgProgress.show();
		}
		HttpAPI.getNewsInfo(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemDiscover.id, new VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				if (m_dlgProgress != null) m_dlgProgress.dismiss();
				try {
					JSONObject jsonObject = new JSONObject(result);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg);
						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");
					itemDiscover.content = dataObject.optString("content");
					mTitleView.setText(itemDiscover.title);
					mKindView.setText(itemDiscover.newsType);
					mDateView.setText(itemDiscover.updatedDateStr);
					mContentView.setWebViewClient(new WebViewClient());
					mContentView.getSettings().setBuiltInZoomControls(true);
					mContentView.getSettings().setUseWideViewPort(true);
					mContentView.getSettings().setJavaScriptEnabled(true);
					mContentView.getSettings().setLoadWithOverviewMode(true);
					mContentView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
					mContentView.getSettings().setPluginState(WebSettings.PluginState.ON);
					mContentView.getSettings().setMediaPlaybackRequiresUserGesture(false);
					mContentView.setWebChromeClient(new WebChromeClient());
					//mContentView.loadUrl("http://uriminzokkiri.com/index.php?ptype=curitv&mtype=view&no=52351#pos");
					mContentView.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;}</style>" + itemDiscover.content, "text/html", "UTF-8", null);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Object error) {
				if (m_dlgProgress != null) m_dlgProgress.dismiss();
			}
		}, ActivityDiscoverDetail.this.TAG);
	}
}
