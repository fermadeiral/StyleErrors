package com.nix.popupmenu.sample;

import org.openintents.shopping.ui.widget.backport.PopupMenu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

	private Button mBtn_1, mBtn_2;
	
	private PopupMenu mPopupMenu;
	private Menu mMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mBtn_1 = (Button) findViewById(R.id.btn1);
		mBtn_2 = (Button)findViewById(R.id.btn2);

		mBtn_1.setOnClickListener(this);
		mBtn_2.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn1:
			mPopupMenu = new PopupMenu(this, v);
			mMenu = mPopupMenu.getMenu();
			mMenu.addSubMenu("111111111111");
			mMenu.addSubMenu("2222222222");
			mMenu.addSubMenu("2222222222");
			mPopupMenu.show();
			break;
			
		case R.id.btn2:
			mPopupMenu = new PopupMenu(this, v);
			mPopupMenu.getMenuInflater();
			mMenu = mPopupMenu.getMenu();
			mMenu.addSubMenu("111");
			mPopupMenu.show();
			break;
		}
	}

}
