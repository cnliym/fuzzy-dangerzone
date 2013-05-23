package com.biziit.taxi;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class LoginActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_psger);
		Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.login_activity_bkcolor);
        this.getWindow().setBackgroundDrawable(drawable);
	}
	
}
