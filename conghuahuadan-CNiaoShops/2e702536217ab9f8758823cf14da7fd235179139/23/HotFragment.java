package com.chhd.cniaoshops.ui.fragment;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.chhd.cniaoshops.R;
import com.chhd.cniaoshops.bean.Page;
import com.chhd.cniaoshops.bean.Wares;
import com.chhd.cniaoshops.global.Constant;
import com.chhd.cniaoshops.http.OnResponse;
import com.chhd.cniaoshops.ui.StatusEnum;
import com.chhd.cniaoshops.ui.base.fragment.BaseFragment;
import com.chhd.cniaoshops.ui.decoration.SpaceItemDecoration;
import com.chhd.cniaoshops.ui.adapter.HotWaresAdapter;
import com.chhd.cniaoshops.ui.items.HotWaresItem;
import com.chhd.cniaoshops.ui.items.ProgressItem;
import com.chhd.cniaoshops.ui.widget.EmptyView;
import com.chhd.cniaoshops.util.LoggerUtils;
import com.chhd.per_library.util.UiUtils;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

/**
 * Created by CWQ on 2016/10/24.
 */
public class HotFragment extends BaseFragment implements Constant {

    @BindView(R.id.refresh_layout)
    MaterialRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fast_scroller)
    FastScroller fastScroller;
    @BindView(R.id.empty_view)
    EmptyView emptyView;

    private List<AbstractFlexibleItem> items = new ArrayList<>();
    private HotWaresAdapter adatper;
    private int curPage = 1;
    private int totalPage = 1;
    private int pageSize = 10;
    private StatusEnum state = StatusEnum.ON_NORMAL;
    private ProgressItem progressItem;


    @Override
    public int getLayoutResID() {
        return R.layout.fragment_hot;
    }

    @Override
    protected void createView() {
        super.createView();
        initView();
        refresh();
    }

    private void initView() {
        refreshLayout.setMaterialRefreshListener(materialRefreshListener);
        refreshLayout.setProgressColors(getProgressColors());

        adatper = new HotWaresAdapter(items);
        progressItem = new ProgressItem(adatper, onClickListener);
        adatper.setEndlessScrollListener(endlessScrollListener, progressItem);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adatper);
        recyclerView.addItemDecoration(new SpaceItemDecoration(UiUtils.dp2px(WARES_DIMEN_NORMAL), true));

        adatper.setFastScroller(fastScroller, UiUtils.getColor(R.color.colorAccent));//Setup FastScroller after the Adapter has been added to the RecyclerView.
    }

    private void refresh() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.autoRefresh();
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_error:
                    progressItem.setStatus(StatusEnum.ON_LOAD_MORE);
                    loadMoreData();
                    break;
            }
        }
    };


    private int[] getProgressColors() {

        int[] colors = new int[SWIPE_REFRESH_LAYOUT_COLORS.length];

        for (int i = 0; i < SWIPE_REFRESH_LAYOUT_COLORS.length; i++) {
            colors[i] = UiUtils.getColor(SWIPE_REFRESH_LAYOUT_COLORS[i]);
        }

        return colors;
    }

    FlexibleAdapter.EndlessScrollListener endlessScrollListener = new FlexibleAdapter.EndlessScrollListener() {

        @Override
        public void noMoreLoad(int newItemsSize) {
        }

        @Override
        public void onLoadMore(int lastPosition, int currentPage) {
            progressItem.setStatus(StatusEnum.ON_LOAD_MORE);
            loadMoreData();
        }
    };


    private MaterialRefreshListener materialRefreshListener = new MaterialRefreshListener() {

        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            refreshData();
        }

        @Override
        public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            super.onRefreshLoadMore(materialRefreshLayout);
            if (curPage <= totalPage) {
                loadMoreData();
            } else {
                refreshLayout.finishRefreshLoadMore();
            }
        }
    };

    private void refreshData() {
        curPage = 1;
        state = StatusEnum.ON_NORMAL;
        requestData();
    }

    private void loadMoreData() {
        state = StatusEnum.ON_LOAD_MORE;
        requestData();
    }

    private void requestData() {

        String url = SERVER_URL + "wares/hot";

        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
        request.add("curPage", curPage);
        request.add("pageSize", pageSize);

        RequestQueue queue = NoHttp.newRequestQueue();
        queue.add(0, request, new OnResponse<String>() {

            @Override
            public void succeed(int what, Response<String> response) {
                try {
                    curPage = ++curPage;
                    Type type = new TypeToken<Page<Wares>>() {
                    }.getType();
                    Page<Wares> page = new Gson().fromJson(response.get(), type);
                    totalPage = page.getTotalPage();
                    showData(page);
                    emptyView.setEmptyView(items);
                } catch (Exception e) {
                    LoggerUtils.e(e, response);
                }
            }

            @Override
            public void failed(int what, Response<String> response) {
                super.failed(what, response);
                fail();
                emptyView.setEmptyView(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestData();
                    }
                });
            }

            @Override
            public void finish(int what) {
                finishRefresh();
            }
        });
    }

    private void finishRefresh() {
        switch (state) {
            case ON_NORMAL:
                refreshLayout.finishRefresh();
                break;
            case ON_LOAD_MORE:
                refreshLayout.finishRefreshLoadMore();
                break;
        }
    }

    private void fail() {
        switch (state) {
            case ON_NORMAL:
                break;
            case ON_LOAD_MORE:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressItem.setStatus(StatusEnum.ON_ERROR);
                    }
                }, 500);
                break;
        }

    }

    private void showData(Page<Wares> page) {
        switch (state) {
            case ON_NORMAL: {
                items.clear();
                for (Wares wares : page.getList()) {
                    HotWaresItem item = new HotWaresItem(getActivity(), wares);
                    items.add(item);
                }
                adatper.notifyDataSetChanged();
            }
            break;
            case ON_LOAD_MORE: {
                final List<AbstractFlexibleItem> newItems = new ArrayList<>();
                for (Wares wares : page.getList()) {
                    HotWaresItem item = new HotWaresItem(getActivity(), wares);
                    newItems.add(item);
                }

                if (newItems.size() == 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressItem.setStatus(StatusEnum.ON_EMPTY);
                            adatper.onLoadMoreComplete(newItems, 2000);
                        }
                    }, 500);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adatper.onLoadMoreComplete(newItems);
                        }
                    }, 500);
                }

            }
            break;
        }

    }

}
