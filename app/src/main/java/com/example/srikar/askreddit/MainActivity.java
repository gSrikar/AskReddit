package com.example.srikar.askreddit;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Logcat tag
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // JSON strings
    private static final String DATA_OBJ = "data";
    private static final String CHILDREN_ARR = "children";
    private static final String DATA_CHILDREN_OBJ = "data";
    private static final String THREAD_TITLE = "title";
    private static final String THREAD_AUTHOR = "author";
    private static final String THREAD_CREATED = "created";
    private static final String THREAD_COMMENTS_NUMBER = "num_comments";
    private static final String THREAD_URL = "permalink";
    private static final String THREAD_UPS = "ups";
    private static final String THREAD_AFTER = "after";

    // JSON String that helps to load next url pages
    private String after = null;
    // Count that helps to load next url pages
    private int count = 0;

    private ArrayList<CustomDataSet> customDataSet;
    private RecyclerView.Adapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;
    private Button retryButton;

    // The total number of items in the data set after the last load
    private int previousTotal = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 5;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        retryButton = (Button) findViewById(R.id.retry_button);

        customDataSet = new ArrayList<>();

        requestJSON();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter
        mAdapter = new RecyclerViewAdapter(this, customDataSet);
        mRecyclerView.setAdapter(mAdapter);

        /**
         * Implements Endless scrolling of RecyclerView data
         */
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                Log.v(LOG_TAG, "Visible Item Count: " + visibleItemCount);
                totalItemCount = mLayoutManager.getItemCount();
                Log.v(LOG_TAG, "Total Item Count: " + totalItemCount);
                firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).
                        findFirstVisibleItemPosition();
                Log.v(LOG_TAG, "First Visible Item: " + firstVisibleItem);
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // Load more data
                    requestJSON();
                    loading = true;
                }
            }
        });

        /**
         *  Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         *  performs a swipe-to-refresh gesture.
         */
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        requestJSON();
                    }
                }
        );
    }

    private void requestJSON() {
        final String BASE_URL = "https://www.reddit.com/r/AskReddit/.json";
        final String COUNT_PARAM = "count";
        final String AFTER_PARAM = "after";
        final String url;
        if (after != null) {
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(COUNT_PARAM, Integer.toString(count))
                    .appendQueryParameter(AFTER_PARAM, after)
                    .build();
            url = builtUri.toString();
            Log.v(LOG_TAG, "Url inside If: " + url);
        } else {
            url = BASE_URL;
            Log.v(LOG_TAG, "Url inside else: " + url);
        }
        count = count + 25;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (response != null && response.length > 0) {
                            try {
                                parseJSONData(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Snackbar.make(coordinatorLayout, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                Log.e(LOG_TAG, "JSON Exception: ", e);
                            }
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        retryButton.setVisibility(View.VISIBLE);
                        mSwipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(coordinatorLayout, error.getMessage(), Snackbar.LENGTH_LONG).show();
                        Log.e(LOG_TAG, "Volley Error: " + error);
                    }
                });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    private void parseJSONData(JSONObject response) throws JSONException {
        JSONObject jSONObject = new JSONObject(String.valueOf(response));
        JSONObject dataObject = jSONObject.getJSONObject(DATA_OBJ);
        // Getting the JSON array node
        JSONArray childrenArray = dataObject.getJSONArray(CHILDREN_ARR);
        // Looping through the children array
        for (int i = 0; i < childrenArray.length(); i++) {
            JSONObject childrenObject = childrenArray.getJSONObject(i);
            // Data node is JSON Object
            JSONObject dataChildrenObject = childrenObject.getJSONObject(DATA_CHILDREN_OBJ);
            // Get the String values
            String title = dataChildrenObject.getString(THREAD_TITLE);
            String author = dataChildrenObject.getString(THREAD_AUTHOR);
            long created = dataChildrenObject.getInt(THREAD_CREATED);
            int commentsNumber = dataChildrenObject.getInt(THREAD_COMMENTS_NUMBER);
            String url = dataChildrenObject.getString(THREAD_URL);
            int ups = dataChildrenObject.getInt(THREAD_UPS);

            long created1 = created * 1000;
            CharSequence created2 = DateUtils.getRelativeTimeSpanString(created1,
                    System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);

            CustomDataSet current = new CustomDataSet();
            current.setThreadTitle(title);
            current.setThreadAuthor(author);
            current.setThreadCreated(created2.toString());
            current.setThreadCommentsNumber(commentsNumber);
            current.setThreadUrl(url);
            current.setThreadUps(ups);
            customDataSet.add(current);
        }
        // Get the after ID
        after = dataObject.getString(THREAD_AFTER);
        Log.v(LOG_TAG, "After String: " + after);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Retry Button that appears when the JSON data failed to parsed
     * When clicked, make the retryButton gone and the progressBar visible
     * Request the JSON data
     */
    public void retryButton(View view) {
        progressBar.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.GONE);
        requestJSON();
    }
}
