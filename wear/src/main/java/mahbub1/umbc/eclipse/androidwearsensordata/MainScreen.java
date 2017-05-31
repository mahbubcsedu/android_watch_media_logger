package mahbub1.umbc.eclipse.androidwearsensordata;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.activity.WearableActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahbub on 4/11/17.
 */

public class MainScreen extends WearableActivity {

    private RecyclerView mRecyclerView;

    private int mEntityCounter = 0;
    private List<ListAdapter.Entity> mData = new ArrayList<ListAdapter.Entity>();
    private ListAdapter mBasicListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRecyclerView.setAdapter(getAdapter());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // This part is just added to show the animations.
        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                swipeView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                        addData(10, 5);
                    }
                }, 3000);
            }
        });

        //getSupportActionBar().setTitle("Basic List");
    }

    private RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this);
    }

    private RecyclerView.Adapter getAdapter() {
        mBasicListAdapter = new ListAdapter(this);
        addData(15, 0);

        mBasicListAdapter.setData(new ArrayList<ListAdapter.Entity>(mData));
        mBasicListAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ListAdapter.Entity entity) {
                System.out.println("BasicListActivity.onItemClick entity : " + entity);
            }
        });
        return mBasicListAdapter;
    }

    private void addData(int add, int del) {
        for (int i = 0; i < del; ++i) {
            int r = (int) (Math.random() * mData.size());
            mData.remove(r);
        }

        for (int i = 0; i < add; ++i) {
            int r = (int) (Math.random() * mData.size());
            mData.add(r, new ListAdapter.Entity("Item " + (++mEntityCounter)));
        }

        mBasicListAdapter.setData(new ArrayList<ListAdapter.Entity>(mData));
    }
}
