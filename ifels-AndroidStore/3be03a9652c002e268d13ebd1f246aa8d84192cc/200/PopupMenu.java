package com.nix.ui.widget.popupmenu;

import com.nix.sample.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.MeasureSpec;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;

/**
 * @author niexinxin:
 * @date 2013-5-16
 * @version 1.0
 */
public class PopupMenu implements AdapterView.OnItemClickListener,
		View.OnKeyListener, ViewTreeObserver.OnGlobalLayoutListener,
		PopupWindow.OnDismissListener {
	private Context mContext;
	private BaseAdapter mMenuAdapter;

	private IcsListPopupWindow mPopup;
	private View mAnchorView;
	private ViewGroup mMeasureParent;
	private Drawable mBgDrawable;
	private ViewTreeObserver mTreeObserver;

	private int mPopupMaxWidth;
	private int mScreenWidth;

	private AdapterView.OnItemClickListener mOnItemClickListener;

	public PopupMenu(Context context) {
		this(context, null);
	}

	public PopupMenu(Context context, View anchorView) {
		mContext = context;
		mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
		mPopupMaxWidth = mScreenWidth / 2;
		mAnchorView = anchorView;
		mBgDrawable = context.getResources().getDrawable(
				R.drawable.default_menu_panel_background);
	}

	public void setAnchorView(View anchor) {
		mAnchorView = anchor;
	}

	public void setAdapter(BaseAdapter adapter) {
		mMenuAdapter = adapter;
	}

	public void setBackground(Drawable bgDrawable) {
		if (bgDrawable != null) {
			mBgDrawable = bgDrawable;
		}
	}

	public void setPopupMaxWidth(int maxWidth) {
		if (maxWidth > 0 && maxWidth < mScreenWidth) {
			mPopupMaxWidth = maxWidth;
		}
	}

	public void setOnItemClickListener(
			AdapterView.OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}

	public boolean tryShow() {
		// mPopup = new IcsListPopupWindow(mContext, null,
		// R.attr.popupMenuStyle);
		mPopup = new IcsListPopupWindow(mContext, null, 0);
		mPopup.setOnDismissListener(this);
		mPopup.setOnItemClickListener(this);

		mPopup.setBackgroundDrawable(mBgDrawable);
		mPopup.setAdapter(mMenuAdapter);
		mPopup.setModal(true);

		View anchor = mAnchorView;
		if (anchor != null) {
			final boolean addGlobalListener = mTreeObserver == null;
			mTreeObserver = anchor.getViewTreeObserver(); // Refresh to latest
			if (addGlobalListener) {
				mTreeObserver.addOnGlobalLayoutListener(this);
			}
			mPopup.setAnchorView(anchor);
		} else {
			return false;
		}

		mPopup.setContentWidth(Math.min(measureContentWidth(mMenuAdapter),
				mPopupMaxWidth));
		mPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		mPopup.show();
		mPopup.getListView().setOnKeyListener(this);
		return true;
	}

	private int measureContentWidth(ListAdapter adapter) {
		// Menus don't tend to be long, so this is more sane than it looks.
		int width = 0;
		View itemView = null;
		int itemType = 0;
		final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0,
				MeasureSpec.UNSPECIFIED);
		final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0,
				MeasureSpec.UNSPECIFIED);
		final int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			final int positionType = adapter.getItemViewType(i);
			if (positionType != itemType) {
				itemType = positionType;
				itemView = null;
			}
			if (mMeasureParent == null) {
				mMeasureParent = new FrameLayout(mContext);
			}
			itemView = adapter.getView(i, itemView, mMeasureParent);
			itemView.measure(widthMeasureSpec, heightMeasureSpec);
			width = Math.max(width, itemView.getMeasuredWidth());
		}
		return width;
	}

	public void dismiss() {
		if (isShowing()) {
			mPopup.dismiss();
		}
	}

	@SuppressWarnings("deprecation")
	public void onDismiss() {
		mPopup = null;
		if (mTreeObserver != null) {
			if (!mTreeObserver.isAlive()) {
				mTreeObserver = mAnchorView.getViewTreeObserver();
			}
			mTreeObserver.removeGlobalOnLayoutListener(this);
			mTreeObserver = null;
		}
	}

	public boolean isShowing() {
		return mPopup != null && mPopup.isShowing();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		dismiss();

		if (mOnItemClickListener != null) {
			mOnItemClickListener.onItemClick(parent, view, position, id);
		}
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP
				&& keyCode == KeyEvent.KEYCODE_MENU) {
			dismiss();
			return true;
		}
		return false;
	}

	@Override
	public void onGlobalLayout() {
		if (isShowing()) {
			final View anchor = mAnchorView;
			if (anchor == null || !anchor.isShown()) {
				dismiss();
			} else if (isShowing()) {
				// Recompute window size and position
				mPopup.show();
			}
		}
	}
}
