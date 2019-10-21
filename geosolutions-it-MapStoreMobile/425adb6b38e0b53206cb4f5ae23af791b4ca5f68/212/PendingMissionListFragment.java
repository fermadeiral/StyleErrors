/*
 * GeoSolutions - MapstoreMobile - GeoSpatial Framework on Android based devices
 * Copyright (C) 2014  GeoSolutions (www.geo-solutions.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.geocollect.android.core.mission;

import it.geosolutions.android.map.model.query.BBoxQuery;
import it.geosolutions.android.map.model.query.BaseFeatureInfoQuery;
import it.geosolutions.android.map.utils.MapFilesProvider;
import it.geosolutions.android.map.utils.ZipFileManager;
import it.geosolutions.geocollect.android.app.BuildConfig;
import it.geosolutions.geocollect.android.core.GeoCollectApplication;
import it.geosolutions.geocollect.android.app.R;
import it.geosolutions.geocollect.android.core.form.FormEditActivity;
import it.geosolutions.geocollect.android.core.login.utils.NetworkUtil;
import it.geosolutions.geocollect.android.core.mission.utils.MissionUtils;
import it.geosolutions.geocollect.android.core.mission.utils.PersistenceUtils;
import it.geosolutions.geocollect.android.core.mission.utils.SQLiteCascadeFeatureLoader;
import it.geosolutions.geocollect.android.core.widgets.dialog.UploadDialog;
import it.geosolutions.geocollect.model.config.MissionTemplate;
import it.geosolutions.geocollect.model.http.CommitResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jsqlite.Database;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;

/**
 * A list fragment representing a list of Pending Missions. This fragment also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is currently being viewed in a {@link PendingMissionDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks} interface.
 */
public class PendingMissionListFragment extends SherlockListFragment implements LoaderCallbacks<List<MissionFeature>>,
        OnScrollListener, OnRefreshListener, OnQueryTextListener {

    /**
     * The serialization (saved instance state) Bundle key representing the activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * Fragment upload
     */
    private static final String FRAGMENT_UPLOAD_DIALOG = "FRAGMENT_UPLOAD_DIALOG";

    private static int CURRENT_LOADER_INDEX = 0;

    private static final String TAG = "MISSION_LIST";

    public static final String INFINITE_SCROLL = "INFINITE_SCROLL";

    public static int ARG_ENABLE_GPS = 43231;

    public static int RESET_MISSION_FEATURE_ID = 12345;

    /**
     * mode of this fragment
     */
    public enum FragmentMode {
        PENDING, CREATION
    }

    private FragmentMode mMode = FragmentMode.PENDING;

    /**
     * The fragment's current callback object, which is notified of list item clicks.
     */
    private Callbacks listSelectionCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * The adapter for the Feature
     */
    private FeatureAdapter adapter;

//    private FeatureAdapter missionAdapter;

    /**
     * Callback for the Loader
     */
    private LoaderCallbacks<List<MissionFeature>> mCallbacks;

    /**
     * SwipeRefreshLayout, use by the swipeDown gesture
     */
    ListFragmentSwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * Boolean value that allow to start loading only once at time
     */
    private boolean isLoading;

    /**
     * The template that provides the form and the
     */
    private MissionTemplate missionTemplate;

    /**
     * page number for remote queries
     */
    private int page = 0;

    /**
     * page size for remote queries
     */
    private int pagesize = 250;

    private View footer;

    /**
     * Main Activity's jsqlite Database instance reference
     */
    private Database db;

    private Button clearFilterBtn;

    private SearchView searchView;

    /**
     * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(Object object);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Object object) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
     */
    public PendingMissionListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MISSION_LIST_FRAGMENT", "onCreate()");

        setRetainInstance(true);

        PendingMissionListActivity activity = (PendingMissionListActivity) getSherlockActivity();

        // setup the listView
        missionTemplate = MissionUtils.getDefaultTemplate(activity);

        ((GeoCollectApplication) activity.getApplication()).setTemplate(missionTemplate);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean usesDownloaded = prefs.getBoolean(PendingMissionListActivity.PREFS_USES_DOWNLOADED_TEMPLATE, false);
        if (usesDownloaded) {
            //int index = prefs.getInt(PendingMissionListActivity.PREFS_DOWNLOADED_TEMPLATE_INDEX, 0) + 1;
            CURRENT_LOADER_INDEX = missionTemplate.getLoaderIndex();
        }

        adapter = new FeatureAdapter(activity, R.layout.mission_resource_row, missionTemplate);
        setListAdapter(adapter);
        // option menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        footer = View.inflate(getActivity(), R.layout.loading_footer, null);

        // Get the list fragment's content view
        final View listFragmentView = inflater.inflate(R.layout.mission_resource_list, container, false);

        clearFilterBtn = (Button) listFragmentView.findViewById(R.id.clearFilterBtn);
        if (clearFilterBtn != null) {
            clearFilterBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (adapter != null) {
                        adapter.getFilter().filter("");
                        v.setVisibility(View.GONE);
                    }
                    if (searchView != null) {
                        searchView.setQuery("", false);
                    }
                    clearSpatialFilter();
                    getSherlockActivity().invalidateOptionsMenu();
                }
            });
        }

        // Now create a SwipeRefreshLayout to wrap the fragment's content view
        mSwipeRefreshLayout = new ListFragmentSwipeRefreshLayout(getSherlockActivity());

        // Add the list fragment's content view to the SwipeRefreshLayout, making sure that it fills
        // the SwipeRefreshLayout
        mSwipeRefreshLayout.addView(listFragmentView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        // Make sure that the SwipeRefreshLayout will fill the fragment
        mSwipeRefreshLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mSwipeRefreshLayout.setColorScheme(R.color.geosol_1, R.color.geosol_2, R.color.geosol_3, R.color.geosol_4);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        // Now return the SwipeRefreshLayout as this fragment's content view
        return mSwipeRefreshLayout;

        // return inflater.inflate(R.layout.mission_resource_list, container, false);
    }

    /**
     * hide loading bar and set loading task
     */
    private void stopLoadingGUI() {
        if (getSherlockActivity() != null) {
            getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
            getSherlockActivity().setSupportProgressBarVisibility(false);
            // getListView().removeFooterView(footer);
            // Log.v(TAG, "task terminated");

        }
        adapter.notifyDataSetChanged();
        isLoading = false;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    /**
     * Sets the view to show that no data are available
     */
    private void setNoData() {
        ((TextView) getView().findViewById(R.id.empty_text)).setText(R.string.no_reporting_found);
        getView().findViewById(R.id.progress_bar).setVisibility(TextView.GONE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity().getIntent().getBooleanExtra(INFINITE_SCROLL, false)) {
            getListView().setOnScrollListener(this);
        } else {
            getListView().setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    // Log.v("PMLF", "First: "+ firstVisibleItem + ", count: "+visibleItemCount+ ", total: "+totalItemCount);
                    if (firstVisibleItem == 0 || visibleItemCount == 0) {
                        mSwipeRefreshLayout.setEnabled(true);
                    } else {
                        mSwipeRefreshLayout.setEnabled(false);
                    }

                }
            });
        }

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

        MissionUtils.checkMapStyles(getResources(), missionTemplate);

        startDataLoading(missionTemplate, CURRENT_LOADER_INDEX);

        registerForContextMenu(getListView());

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Log.v("MISSION_LIST_FRAGMENT", "onAttach()");
        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        // If a previous instance of the database was attached, the loader must be restarted
        boolean needReload = false;
        if (db != null && db.dbversion().equals("unknown")) {
            needReload = true;
        }

        // Check for a database
        if (getSherlockActivity() instanceof PendingMissionListActivity) {
            Log.v(TAG, "Loader: Connecting to Activity database");
            db = ((PendingMissionListActivity) getSherlockActivity()).spatialiteDatabase;
            // restart the loader if needed
            if (needReload) {
                LoaderManager lm = getSherlockActivity().getSupportLoaderManager();
                if (lm.getLoader(CURRENT_LOADER_INDEX) != null) {
                    lm.restartLoader(CURRENT_LOADER_INDEX, null, this);
                }
            }
        } else {
            Log.w(TAG, "Loader: Could not connect to Activity database");
        }

        listSelectionCallbacks = (Callbacks) activity;

    }

    /**
     * Create the Menu visible on longpress on items
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

        // if this item is editable or uploadable, offer the possibility to "reset" the state -> delete its "sop" entry
        if (v.getId() == getListView().getId()) {
            ListView lv = (ListView) v;

            final MissionTemplate template = MissionUtils.getDefaultTemplate(getActivity());
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            MissionFeature feature = (MissionFeature) lv.getItemAtPosition(info.position);

            // identify edited/uploadable
            if (feature.editing || (feature.typeName != null && feature.typeName.endsWith(MissionTemplate.NEW_NOTICE_SUFFIX))) {
                // create a dialog to let the user clear this surveys data
                String title = getString(R.string.survey);
                if (feature.properties != null && feature.properties.get(template.nameField) != null) {
                    title = (String) feature.properties.get(template.nameField);
                }
                if (title != null) {
                    menu.setHeaderTitle(title);
                }

                menu.add(0, RESET_MISSION_FEATURE_ID, 0, getString(R.string.menu_clear_survey));
            
            }
        }
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {

        // user selected the option to reset, delete the edited item
        if (item.getItemId() == RESET_MISSION_FEATURE_ID) {
            
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            final MissionFeature feature = (MissionFeature) getListView().getItemAtPosition(info.position);
            final MissionTemplate template = MissionUtils.getDefaultTemplate(getActivity());

            String title = (String) feature.properties.get(template.nameField);

            Log.d(TAG, "missionfeature " + title + " selected to reset");
            final String tableName;
            final String fid = MissionUtils.getFeatureGCID(feature);
            if(feature.typeName != null && feature.typeName.endsWith(MissionTemplate.NEW_NOTICE_SUFFIX)){
                
                tableName = missionTemplate.schema_seg.localSourceStore + MissionTemplate.NEW_NOTICE_SUFFIX;

            }else{
                tableName = template.schema_sop.localFormStore;
            }
            
            new AlertDialog.Builder(getSherlockActivity()).setTitle(R.string.item_delete_title)
            .setMessage(R.string.item_delete_message)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    
                    // delete this new entry
                    PersistenceUtils.deleteMissionFeature(db, tableName,fid);

                    // if this entry was uploadable remove it from the list of uploadables
                    HashMap<String, ArrayList<String>> uploadables = PersistenceUtils.loadUploadables(getSherlockActivity());
                    if (uploadables.containsKey(tableName) && uploadables.get(tableName).contains(fid)) {
                        uploadables.get(tableName).remove(fid);
                        PersistenceUtils.saveUploadables(getSherlockActivity(), uploadables);
                    }
                    
                    forceLoad();
                    getSherlockActivity().supportInvalidateOptionsMenu();

                }
                
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // nothing
                    dialog.dismiss();
                }
            }).show();

            return true;
        }
        return super.onContextItemSelected(item);

    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        listSelectionCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        listSelectionCallbacks.onItemSelected(getListAdapter().getItem(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    /**
     * Creates the actionBar buttons
     */
    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {

        PendingMissionListActivity activity = (PendingMissionListActivity) getSherlockActivity();
        // Are we on a tablet?
        boolean mTwoPane = activity.findViewById(R.id.pendingmission_detail_container) != null;

        // upload
        if (missionTemplate != null && missionTemplate.schema_sop != null
                && missionTemplate.schema_sop.localFormStore != null) {

            String newFeaturesTableName =  missionTemplate.schema_seg.localSourceStore + MissionTemplate.NEW_NOTICE_SUFFIX ;
            String surveysTableName = missionTemplate.schema_sop.localFormStore;
            
            HashMap<String, ArrayList<String>> uploadables = PersistenceUtils.loadUploadables(activity);
            if (
                  (uploadables.containsKey(newFeaturesTableName) && uploadables.get(newFeaturesTableName).size() > 0)
               || (uploadables.containsKey(surveysTableName) && uploadables.get(surveysTableName).size() > 0)) {
                // there are uploadable entries, add a menu item
                inflater.inflate(R.menu.uploadable, menu);
            }
        }
            
        // Display the Search only if it is not already filtered
        inflater.inflate(R.menu.searchable, menu);

        // get searchview and add querylistener
        // menu.findItem(R.id.search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(getString(R.string.search_missions));
        searchView.setOnQueryTextListener(this);
        searchView.setOnQueryTextFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // keyboard was closed, collapse search action view
                    menu.findItem(R.id.search).collapseActionView();
                }
            }
        });

        // If SRID is set, a filter exists
        SharedPreferences sp = activity.getSharedPreferences(
                SQLiteCascadeFeatureLoader.PREF_NAME,
                Context.MODE_PRIVATE);
        
        if (sp.contains(SQLiteCascadeFeatureLoader.FILTER_SRID)) {
            inflater.inflate(R.menu.filterable, menu);
        }

        // TODO use the FAB on the main layout instead of inside the list fragment
        if (mTwoPane && missionTemplate != null) {
            if (missionTemplate.schema_seg != null) {
                inflater.inflate(R.menu.creating, menu);
            }
        }

        inflater.inflate(R.menu.map_full, menu);

        if (missionTemplate != null) {
            if (missionTemplate.schema_sop != null
                    && (missionTemplate.schema_sop.orderingField != null || missionTemplate.orderingField != null)) {
                inflater.inflate(R.menu.orderable, menu);
                if (sp.getBoolean(SQLiteCascadeFeatureLoader.REVERSE_ORDER_PREF, false)) {
                    MenuItem orderButton = menu.findItem(R.id.order);
                    orderButton.setIcon(R.drawable.ic_action_sort_by_size);
                }
            }

        }

        // Creating the Overflow Menu
        SubMenu subMenu1 = menu.addSubMenu(0, R.id.overflow_menu, 90, "Overflow Menu");
        SubMenu subMenu2 = subMenu1.addSubMenu(0, R.id.overflow_order, Menu.NONE, R.string.order_by_ellipsis);
        subMenu2.setGroupCheckable(1, true, true);

        subMenu2.add(1, R.id.overflow_order_az, Menu.NONE, R.string.ordering_az).setChecked(
                !sp.getBoolean(SQLiteCascadeFeatureLoader.ORDER_BY_DISTANCE, false));
        subMenu2.add(1, R.id.overflow_order_distance, Menu.NONE, R.string.ordering_distance).setChecked(
                sp.getBoolean(SQLiteCascadeFeatureLoader.ORDER_BY_DISTANCE, false));

        subMenu2.setGroupCheckable(1, true, true);

        subMenu1.add(0, R.id.overflow_refresh, Menu.NONE, R.string.update);

        MenuItem subMenu1Item = subMenu1.getItem();
        subMenu1Item.setIcon(R.drawable.ic_action_overflow);
        subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return query.length() > 0;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (adapter != null) {
            // filters the adapters entries using its overridden filter
            adapter.getFilter().filter(newText);
        }
        if (adapter.isEmpty()) {
            setNoData();
            if (clearFilterBtn != null) {
                clearFilterBtn.setVisibility(View.VISIBLE);
            }
        } else {
            if (clearFilterBtn != null) {
                clearFilterBtn.setVisibility(View.GONE);
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com.actionbarsherlock.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.create_new) {

            PendingMissionListActivity.checkGPSandStartCreation(getSherlockActivity());

            return true;

        } else if (id == R.id.order) {

            orderButtonCallback();
            SharedPreferences sp = getActivity().getSharedPreferences(SQLiteCascadeFeatureLoader.PREF_NAME,
                    Context.MODE_PRIVATE);
            if (sp.getBoolean(SQLiteCascadeFeatureLoader.REVERSE_ORDER_PREF, false)) {
                item.setIcon(R.drawable.ic_action_sort_by_size);
            } else {
                item.setIcon(R.drawable.ic_action_reverse_sort);
            }
            return true;

        } else if (id == R.id.overflow_order_az) {

            setOrdering(false);
            item.setChecked(true);
            return true;

        } else if (id == R.id.overflow_order_distance) {

            setOrdering(true);
            item.setChecked(true);
            return true;

        } else if (id == R.id.filter) {

            // Clear the Spatial Filter
            if (getSherlockActivity().getSupportLoaderManager().getLoader(CURRENT_LOADER_INDEX) != null) {

                clearSpatialFilter();
                item.setVisible(false);
            }
            return true;
        } else if (id == R.id.upload) {

            if (!NetworkUtil.isOnline(getSherlockActivity())) {
                Toast.makeText(getSherlockActivity(), getString(R.string.login_not_online), Toast.LENGTH_LONG).show();
                return true;
            }
            // upload
            if (missionTemplate != null) {

                String title = null;
                String uploadList = null;
                String itemList = "";
                HashMap<String, ArrayList<String>> uploadables = PersistenceUtils
                        .loadUploadables(getSherlockActivity());
                ArrayList<MissionFeature> uploads = new ArrayList<MissionFeature>();

                final String newFeaturesTableName =  missionTemplate.schema_seg.localSourceStore + MissionTemplate.NEW_NOTICE_SUFFIX ;
                final String surveysTableName = missionTemplate.schema_sop.localFormStore;
                
                // Check if there are new items to upload
                itemList = updateFeatureUploadList(itemList, uploadables, uploads, newFeaturesTableName);

                // Check if there are surveys to upload
                itemList = updateFeatureUploadList(itemList, uploadables, uploads, surveysTableName);


                uploadList = getResources().getQuantityString(R.plurals.upload_intro, uploads.size()) + ":\n" + itemList;
                title = getString(R.string.upload_title);

                final ArrayList<MissionFeature> finalUploads = uploads;
                // show the dialog to confirm the upload
                new AlertDialog.Builder(getSherlockActivity()).setTitle(title).setMessage(uploadList)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do the upload

                                // get urls
                                final String newFeaturesUrl = missionTemplate.seg_form.url;
                                final String surveysUrl = missionTemplate.sop_form.url;
                                final String newFeaturesMediaUrl = missionTemplate.seg_form.mediaurl;
                                final String surveysMediaUrl = missionTemplate.sop_form.mediaurl;

                                // check urls
                                if ( newFeaturesUrl == null
                                  || newFeaturesMediaUrl == null
                                  || surveysUrl == null
                                  || surveysMediaUrl == null) {
                                    
                                    Log.e(UploadDialog.class.getSimpleName(), "no url or mediaurl available for upload, cannot continue");
                                    Toast.makeText(getSherlockActivity(), R.string.error_sending_data, Toast.LENGTH_LONG).show();
                                    return;
                                }

                                // as done before in "SendAction" ....
                                android.support.v4.app.FragmentManager fm = getSherlockActivity()
                                        .getSupportFragmentManager();
                                Fragment mTaskFragment = fm.findFragmentByTag(FRAGMENT_UPLOAD_DIALOG);
                                if (mTaskFragment == null) {
                                    FragmentTransaction ft = fm.beginTransaction();

                                    mTaskFragment = new UploadDialog() {
                                        @Override
                                        public void onFinish(Activity ctx, CommitResponse result) {
                                            if (result != null && result.isSuccess()) {

                                                Toast.makeText(getSherlockActivity(),
                                                        getResources().getString(R.string.data_send_success),
                                                        Toast.LENGTH_LONG).show();

                                                // update adapter and menu
                                                /*if (mMode == FragmentMode.CREATION) {
                                                    fillCreatedMissionFeatureAdapter();
                                                } else {
                                                */    forceLoad();
                                                //}
                                                getSherlockActivity().supportInvalidateOptionsMenu();

                                                super.onFinish(ctx, result);
                                            } else {

                                                Toast.makeText(getSherlockActivity(), R.string.error_sending_data,
                                                        Toast.LENGTH_LONG).show();

                                                super.onFinish(ctx, result);
                                            }
                                        }
                                    };

                                    // fill up the args for the upload dialog
                                    Bundle arguments = new Bundle();

                                    arguments.putSerializable(UploadDialog.PARAMS.MISSION_TEMPLATE, missionTemplate);
                                    arguments.putSerializable(UploadDialog.PARAMS.FEATURES, finalUploads);
                                    
                                    mTaskFragment.setArguments(arguments);

                                    ((DialogFragment) mTaskFragment).setCancelable(false);
                                    ft.add(mTaskFragment, FRAGMENT_UPLOAD_DIALOG);
                                    ft.commit();

                                }
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // nothing, close dialog
                            }
                        }).show();
            }
        } else if (id == R.id.full_map) {

            // Open the Map
            if (getSherlockActivity() instanceof PendingMissionListActivity) {
                ((PendingMissionListActivity) getSherlockActivity()).launchFullMap();
            }

            return true;

        } else if (id == R.id.overflow_refresh) {

            onRefresh();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 
     */
    public void clearSpatialFilter() {
        SharedPreferences sp = getSherlockActivity().getSharedPreferences(SQLiteCascadeFeatureLoader.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        // Change the ordering
        editor.remove(SQLiteCascadeFeatureLoader.FILTER_N);
        editor.remove(SQLiteCascadeFeatureLoader.FILTER_S);
        editor.remove(SQLiteCascadeFeatureLoader.FILTER_W);
        editor.remove(SQLiteCascadeFeatureLoader.FILTER_E);
        editor.remove(SQLiteCascadeFeatureLoader.FILTER_SRID);
        editor.commit();

        adapter.clear();
        if(getSherlockActivity().getSupportLoaderManager().getLoader(CURRENT_LOADER_INDEX) != null){
            getSherlockActivity().getSupportLoaderManager().getLoader(CURRENT_LOADER_INDEX).forceLoad();
        }
    }

    /**
     * @param itemList
     * @param uploadables
     * @param uploads
     * @param newFeaturesTableName
     * @return
     */
    public String updateFeatureUploadList(String itemList, HashMap<String, ArrayList<String>> uploadables,
            ArrayList<MissionFeature> uploads, final String newFeaturesTableName) {
        
        List<String> uploadIDs = uploadables.get(newFeaturesTableName);
        ArrayList<MissionFeature> features;
        if(uploadIDs!= null && uploadIDs.size() > 0){
            features = MissionUtils.getMissionFeatures(newFeaturesTableName, db);
            for (MissionFeature f : features) {
                String featureID = MissionUtils.getFeatureGCID(f);
                if (uploadIDs.contains(featureID)) {
                    
                    // this new entry is "uploadable" , add it
                    uploads.add(f);
                    
                    // Fill the Text for the user
                    itemList = updateListText(itemList, f, featureID);
                }
            }
        }
        return itemList;
    }

    /**
     * @param itemList
     * @param f
     * @param featureID
     * @return
     */
    public String updateListText(String itemList, MissionFeature f, String featureID) {
        if ( f.properties != null 
          && f.properties.containsKey(missionTemplate.nameField)
          && f.properties.get(missionTemplate.nameField) != null) {
            
            itemList += (String) f.properties.get(missionTemplate.nameField) + "\n";
            
        } else {
            
            // survey feature do not contain the "nameField", get it from the according mission
            for (int i = 0; i < adapter.getCount(); i++) {
                MissionFeature mf = adapter.getItem(i);
                if (MissionUtils.getFeatureGCID(mf).equals(featureID)) {
                    if ( mf.properties != null 
                      && mf.properties.containsKey(missionTemplate.nameField)) {
                        
                        itemList += (String) mf.properties.get(missionTemplate.nameField) + "\n";
                        break;
                    }
                }
            }
        }
        return itemList;
    }

    /**
     * 
     */
    private void orderButtonCallback() {
        // Get it from the mission template

        SharedPreferences sp = getSherlockActivity().getSharedPreferences(SQLiteCascadeFeatureLoader.PREF_NAME,
                Context.MODE_PRIVATE);
        boolean reverse = sp.getBoolean(SQLiteCascadeFeatureLoader.REVERSE_ORDER_PREF, false);
        SharedPreferences.Editor editor = sp.edit();

        // Change the ordering
        Log.v(TAG, "Changing to " + reverse);
        editor.putBoolean(SQLiteCascadeFeatureLoader.REVERSE_ORDER_PREF, !reverse);
        editor.commit();

        forceLoad();
    }

    /**
     * 
     */
    private void setOrdering(boolean useDistance) {

        SharedPreferences sp = getSherlockActivity().getSharedPreferences(SQLiteCascadeFeatureLoader.PREF_NAME,
                Context.MODE_PRIVATE);

        // This check pass only if the value is not already set to the right value.
        if (sp.getBoolean(SQLiteCascadeFeatureLoader.ORDER_BY_DISTANCE, !useDistance) != useDistance) {

            SharedPreferences.Editor editor = sp.edit();

            // Set the ordering
            Log.v(TAG, "useDistance: " + useDistance);
            editor.putBoolean(SQLiteCascadeFeatureLoader.ORDER_BY_DISTANCE, useDistance);
            editor.commit();

            forceLoad();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.AbsListView.OnScrollListener#onScrollStateChanged(android.widget.AbsListView, int)
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView, int, int, int)
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        Log.v("PMLF", "First: " + firstVisibleItem + ", count: " + visibleItemCount + ", total: " + totalItemCount);
        if (firstVisibleItem == 0 || visibleItemCount == 0) {
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }

        // check if applicable
        if (adapter == null) {
            return;
        }
        if (adapter.getCount() == 0) {
            return;
        }
        if (getSherlockActivity().getSupportLoaderManager().getLoader(CURRENT_LOADER_INDEX) == null) {
            return;
        }

        // if the last item is visible and can load more resources
        // load more resources
        int l = visibleItemCount + firstVisibleItem;
        if (l >= totalItemCount && !isLoading) {
            // It is time to add new data. We call the listener
            // getListView().addFooterView(footer);

            isLoading = true;
            loadMore();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
     */
    @Override
    public Loader<List<MissionFeature>> onCreateLoader(int id, Bundle arg1) {
        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
        // getSherlockActivity().getSupportActionBar();

        Log.d("MISSION_LIST", "onCreateLoader() for id " + id);

        return MissionUtils.createMissionLoader(missionTemplate, getSherlockActivity(), page, pagesize, db);
    }

    /**
	 * 
	 */
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object)
     */
    @Override
    public void onLoadFinished(Loader<List<MissionFeature>> loader, List<MissionFeature> results) {
        if (results == null) {
            Toast.makeText(getSherlockActivity(), R.string.error_connectivity_problem, Toast.LENGTH_SHORT).show();
            setNoData();
        } else {
            Log.d(TAG, "loader returned " + results.size());
            // add loaded resources to the listView
            adapter = new FeatureAdapter(getSherlockActivity(), R.layout.mission_resource_row, missionTemplate);
            adapter.setItems(results);
            setListAdapter(adapter);

            if (adapter.isEmpty()) {
                setNoData();
            } else {

            }
        }
        stopLoadingGUI();

        // if this fragment is visible to the user, check if the background data for the current template is available
        if (getUserVisibleHint()) {
            checkIfBackgroundIsAvailableForTemplate();
        }
    }

    /**
     * checks if background data for the current template is available if not, the user is asked to download
     */
    private void checkIfBackgroundIsAvailableForTemplate() {

        boolean exists = MissionUtils.checkTemplateForBackgroundData(getActivity(), missionTemplate);

        if (!exists) {

            final HashMap<String, Integer> urls = MissionUtils.getContentUrlsAndFileAmountForTemplate(missionTemplate);

            if (BuildConfig.DEBUG) {
                Log.i(TAG, "downloading " + urls.toString());
            }
            
            Resources res = getResources();
            for (String url : urls.keySet()) {

                final String mount = MapFilesProvider.getEnvironmentDirPath(getActivity());

                String dialogMessage = res.getQuantityString(R.plurals.dialog_message_with_amount, urls.get(url),
                        urls.get(url));
                new ZipFileManager(getActivity(), mount, MapFilesProvider.getBaseDir(), url, null, dialogMessage) {
                    @Override
                    public void launchMainActivity(final boolean success) {

                        // TODO apply ? this was earlier in StartupActivity
                        // if (getActivity().getApplication() instanceof GeoCollectApplication) {
                        // ((GeoCollectApplication) getActivity().getApplication()).setupMBTilesBackgroundConfiguration();
                        // }
                        // launch.putExtra(PendingMissionListFragment.INFINITE_SCROLL, false);
                        if (success) {

                            Toast.makeText(getActivity(), getString(R.string.download_successfull), Toast.LENGTH_SHORT)
                                    .show();
                        }

                    }
                };
            }

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
     */
    @Override
    public void onLoaderReset(Loader<List<MissionFeature>> arg0) {
        adapter.clear();
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);

    }

    /**
     * Loads more data from the Loader
     */
    protected void loadMore() {

        if (getSherlockActivity().getSupportLoaderManager().getLoader(CURRENT_LOADER_INDEX) != null) {

            page++;
            getSherlockActivity().getSupportLoaderManager().restartLoader(CURRENT_LOADER_INDEX, null, this);

        }
    }

    /**
     * Create the data loader and bind the loader to the parent callbacks
     * 
     * @param URL (not used for now)
     * @param loaderIndex a unique id for query loader
     */
    private void startDataLoading(MissionTemplate t, int loaderIndex) {

        // initialize Load Manager
        mCallbacks = this;
        // reset page
        LoaderManager lm = getSherlockActivity().getSupportLoaderManager();
        adapter.clear();
        page = 0;
        lm.initLoader(loaderIndex, null, this);
    }

    /**
     * Refresh the list
     */
    @Override
    public void onResume() {
        super.onResume();
        
        // Start data loading
        if (getSherlockActivity().getSupportLoaderManager().getLoader(CURRENT_LOADER_INDEX) != null) {
            getSherlockActivity().getSupportLoaderManager().getLoader(CURRENT_LOADER_INDEX).forceLoad();
        }

        getSherlockActivity().supportInvalidateOptionsMenu();

    }

    /**
     * Handle the results
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PendingMissionListActivity.SPATIAL_QUERY) {

            if (data != null && data.hasExtra("query")) {
                BaseFeatureInfoQuery query = data.getParcelableExtra("query");

                // Create task query
                if (query instanceof BBoxQuery) {
                    BBoxQuery bbox = (BBoxQuery) query;

                    // TODO: Refactor the Preferences Double Storage
                    // This is just a fast hack to store Doubles in Long but It should be handled by a more clean utility class
                    // Maybe extend the Preference Editor?

                    Log.v(TAG, "Received:\nN: " + bbox.getN() + "\nN: " + bbox.getS() + "\nE: " + bbox.getE() + "\nW: "
                            + bbox.getW() + "\nSRID: " + bbox.getSrid());
                    SharedPreferences sp = getSherlockActivity().getSharedPreferences(
                            SQLiteCascadeFeatureLoader.PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putLong(SQLiteCascadeFeatureLoader.FILTER_N, Double.doubleToRawLongBits(bbox.getN()));
                    editor.putLong(SQLiteCascadeFeatureLoader.FILTER_S, Double.doubleToRawLongBits(bbox.getS()));
                    editor.putLong(SQLiteCascadeFeatureLoader.FILTER_W, Double.doubleToRawLongBits(bbox.getW()));
                    editor.putLong(SQLiteCascadeFeatureLoader.FILTER_E, Double.doubleToRawLongBits(bbox.getE()));
                    editor.putInt(SQLiteCascadeFeatureLoader.FILTER_SRID, Integer.parseInt(bbox.getSrid()));
                    editor.commit();

                    forceLoad();
                    Toast.makeText(getActivity(), getString(R.string.selection_filtered), Toast.LENGTH_SHORT).show();
                }

                // else if(query instanceof CircleQuery){ }

            } else {

                // No result, clean the filter
                SharedPreferences sp = getSherlockActivity().getSharedPreferences(SQLiteCascadeFeatureLoader.PREF_NAME,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.remove(SQLiteCascadeFeatureLoader.FILTER_N);
                editor.remove(SQLiteCascadeFeatureLoader.FILTER_S);
                editor.remove(SQLiteCascadeFeatureLoader.FILTER_W);
                editor.remove(SQLiteCascadeFeatureLoader.FILTER_E);
                editor.remove(SQLiteCascadeFeatureLoader.FILTER_SRID);
                editor.commit();

                forceLoad();

            }

        } else if (requestCode == ARG_ENABLE_GPS) {
            Log.d(FormEditActivity.class.getSimpleName(), "back from GPS settings");

            if (!PendingMissionListActivity.isGPSAvailable(getSherlockActivity())) {
                Toast.makeText(getActivity(), R.string.gps_still_not_enabled, Toast.LENGTH_LONG).show();
            } else {
                PendingMissionListActivity.startMissionFeatureCreation(getSherlockActivity());
            }
        } else {

            missionTemplate = ((GeoCollectApplication) getActivity().getApplication()).getTemplate();
            CURRENT_LOADER_INDEX = missionTemplate.getLoaderIndex();
            adapter.setTemplate(missionTemplate);
            forceLoad();
        }
    }

    /**
     * Callback for the {@link SwipeRefreshLayout}
     */
    @Override
    public void onRefresh() {

        SharedPreferences sp = getSherlockActivity().getSharedPreferences(SQLiteCascadeFeatureLoader.PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        // Reset the preference to force update
        editor.putLong(SQLiteCascadeFeatureLoader.LAST_UPDATE_PREF, 0);
        editor.commit();

        forceLoad();
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(true);

    }

    /**
     * Trigger a reload of the current loader
     */
    private void forceLoad() {
        adapter.clear();
        Loader<Object> l = getSherlockActivity().getSupportLoaderManager().getLoader(CURRENT_LOADER_INDEX);
        if (l != null) {
            l.forceLoad();
        }
    }

    /**
     * Utility method to check whether a {@link ListView} can scroll up from it's current position. Handles platform version differences, providing
     * backwards compatible functionality where needed.
     */
    private static boolean canListViewScrollUp(ListView listView) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            // For ICS and above we can call canScrollVertically() to determine this
            return ViewCompat.canScrollVertically(listView, -1);
        } else {
            // Pre-ICS we need to manually check the first visible item and the child view's top
            // value
            return listView.getChildCount() > 0
                    && (listView.getFirstVisiblePosition() > 0 || listView.getChildAt(0).getTop() < listView
                            .getPaddingTop());
        }
    }

    /**
     * Sub-class of {@link android.support.v4.widget.SwipeRefreshLayout} for use in this {@link android.support.v4.app.ListFragment}. The reason that
     * this is needed is because {@link android.support.v4.widget.SwipeRefreshLayout} only supports a single child, which it expects to be the one
     * which triggers refreshes. In our case the layout's child is the content view returned from
     * {@link android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)} which is a
     * {@link android.view.ViewGroup}.
     * 
     * <p>
     * To enable 'swipe-to-refresh' support via the {@link android.widget.ListView} we need to override the default behavior and properly signal when
     * a gesture is possible. This is done by overriding {@link #canChildScrollUp()}.
     */
    private class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout {

        public ListFragmentSwipeRefreshLayout(Context context) {
            super(context);
        }

        /**
         * As mentioned above, we need to override this method to properly signal when a 'swipe-to-refresh' is possible.
         * 
         * @return true if the {@link android.widget.ListView} is visible and can scroll up.
         */
        @Override
        public boolean canChildScrollUp() {
            final ListView listView = getListView();
            if (listView.getVisibility() == View.VISIBLE) {
                return canListViewScrollUp(listView);
            } else {
                return false;
            }
        }

    }

    /**
     * switches between pending mission mode and created mission mode
     * 
     * @param FragmentMode the new mode
     */
    public void switchAdapter() {

        // remove longclicklistener
        getListView().setOnItemLongClickListener(null);

        adapter.setTemplate(missionTemplate);
        setListAdapter(adapter);

        // invalidate actionbar
        getSherlockActivity().supportInvalidateOptionsMenu();

    }

    /**
     * sets the current missionTemplate of this fragments list
     * 
     * @param pMissionTemplate the template to apply
     */
    public void setTemplate(final MissionTemplate pMissionTemplate) {

        missionTemplate = pMissionTemplate;

    }

    /**
     * @param restarts the loader with a certain index
     */
    public void restartLoader(final int index) {

        startDataLoading(missionTemplate, index);

        CURRENT_LOADER_INDEX = index;
    }

    public MissionTemplate getCurrentMissionTemplate() {
        return missionTemplate;
    }

}
