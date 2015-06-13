package com.phongbm.flashlightpro;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements OnClickListener {
	private ImageView btnTurnOn, btnTurnOff;
	private Camera camera;
	private Parameters parameters;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);

		initializeComponent();

		if (MainActivity.this.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_FLASH)) {
			turnOnFlashlight();
			addShortcut();
		} else {
			btnTurnOff.setVisibility(RelativeLayout.GONE);
			btnTurnOn.setVisibility(RelativeLayout.VISIBLE);
		}
	}

	public void addShortcut() {
		boolean shortcutReinstall = false;
		SharedPreferences appPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean isAppInstalled = appPreferences.getBoolean("isAppInstalled",
				false);
		String currentLanguage = Locale.getDefault().getDisplayLanguage();
		String previousSetLanguage = appPreferences.getString("phoneLanguage",
				Locale.getDefault().getDisplayLanguage());
		if (!previousSetLanguage.equals(currentLanguage)) {
			shortcutReinstall = true;
		}
		if (!isAppInstalled || shortcutReinstall) {
			Intent HomeScreenShortCut = new Intent(getApplicationContext(),
					MainActivity.class);
			HomeScreenShortCut.setAction(Intent.ACTION_MAIN);
			HomeScreenShortCut.putExtra("duplicate", false);
			if (shortcutReinstall) {
				Intent removeIntent = new Intent();
				removeIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
						HomeScreenShortCut);
				String prevAppName = appPreferences.getString("appName",
						getString(R.string.app_name));
				removeIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, prevAppName);
				removeIntent
						.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
				getApplicationContext().sendBroadcast(removeIntent);
			}
			Intent addIntent = new Intent();
			addIntent
					.putExtra(Intent.EXTRA_SHORTCUT_INTENT, HomeScreenShortCut);
			addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
					getString(R.string.app_name));
			addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
					Intent.ShortcutIconResource.fromContext(
							getApplicationContext(), R.drawable.ic_logo));
			addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			getApplicationContext().sendBroadcast(addIntent);
			SharedPreferences.Editor editor = appPreferences.edit();
			editor.putBoolean("isAppInstalled", true);
			editor.putString("phoneLanguage", currentLanguage);
			editor.putString("appName", getString(R.string.app_name));
			editor.commit();
		}

		// Intent shortcutIntent = new Intent(
		// MainActivity.this.getApplicationContext(), MainActivity.class);
		// shortcutIntent.setAction(Intent.ACTION_MAIN);
		// Intent addIntent = new Intent();
		// addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		// addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Flashlight Pro");
		// addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
		// Intent.ShortcutIconResource.fromContext(
		// getApplicationContext(), R.drawable.ic_logo));
		// addIntent.putExtra("duplicate", false);
		// addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// MainActivity.this.getApplicationContext().sendBroadcast(addIntent);
	}

	private void initializeComponent() {
		btnTurnOn = (ImageView) findViewById(R.id.btnTurnOn);
		btnTurnOff = (ImageView) findViewById(R.id.btnTurnOff);
		btnTurnOn.setOnClickListener(MainActivity.this);
		btnTurnOff.setOnClickListener(MainActivity.this);
	}

	private void turnOnFlashlight() {
		camera = Camera.open();
		parameters = camera.getParameters();
		parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		camera.setParameters(parameters);
		camera.startPreview();
	}

	private void turnOffFlashLigh() {
		camera.stopPreview();
		camera.release();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnTurnOn:
			turnOnFlashlight();
			btnTurnOn.setVisibility(RelativeLayout.GONE);
			btnTurnOff.setVisibility(RelativeLayout.VISIBLE);
			break;
		case R.id.btnTurnOff:
			turnOffFlashLigh();
			btnTurnOff.setVisibility(RelativeLayout.GONE);
			btnTurnOn.setVisibility(RelativeLayout.VISIBLE);
			break;
		}
	}

}