package com.iot.shoumengou.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
				break;
			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
				break;
			case ConstantsAPI.COMMAND_LAUNCH_BY_WX:
				break;
			default:
				break;
		}
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
		}

		int result = 0;
		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				break;
			default:
				break;
		}
	}
}
