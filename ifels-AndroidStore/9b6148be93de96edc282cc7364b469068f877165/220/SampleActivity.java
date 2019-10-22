package com.nix.sample;

import java.util.ArrayList;
import java.util.List;

import com.nix.ui.widget.popupmenu.PopupMenu;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class SampleActivity extends ListActivity{

    private final static int PLAY_SELECTION = 0;
    private final static int ADD_TO_PLAYLIST = 1;
    private final static int SEARCH = 2;
    
    private MenuItemAdapter mAdapter;
    private List<MenuItem> mItems = new ArrayList<MenuItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] array = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i","j","k","l","m", "o","p","q","r","s","t","u","v","w","x","y","z"};
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, array));
        
        addMenuItem(PLAY_SELECTION, R.string.play, R.drawable.ic_context_menu_play_normal);
        addMenuItem(ADD_TO_PLAYLIST, R.string.add_to_playlist,R.drawable.ic_context_menu_add_to_playlist_normal);
        addMenuItem(SEARCH, R.string.search,R.drawable.ic_context_menu_search_normal);
        mAdapter = new MenuItemAdapter(this, mItems);
        
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // Create Instance
        //PopupMenu menu = new PopupMenu(this);
    	PopupMenu menu = new PopupMenu(this);
    	menu.setAnchorView(v);
    	menu.setAdapter(mAdapter);
    	menu.setForceBelowAnchor(position%2==0);
    	menu.tryShow();
    }

    public MenuItem addMenuItem(int itemId, int titleRes, int iconRes) {
        MenuItem item = new MenuItem();
        item.setItemId(itemId);
        item.setTitle(getString(titleRes));
        item.setIcon(getResources().getDrawable(iconRes));
        mItems.add(item);
        return item;
    }
}
