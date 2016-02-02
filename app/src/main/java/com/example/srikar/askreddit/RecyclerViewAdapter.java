package com.example.srikar.askreddit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private final ArrayList<CustomDataSet> customDataSet;
    private Context context;
    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Intent Extras
        private static final String EXTRA_URL = "EXTRA_URL";
        private static final String EXTRA_TITLE = "EXTRA_TITLE";
        // Retrieve the container (ie the root ViewGroup from your custom item layout)
        // It's the view that will be animated
        public View container;
        // Each data item
        public TextView mThreadTitle;
        public TextView mThreadAuthor;
        public TextView mThreadCreated;
        public TextView mThreadCommentsNumber;
        public TextView mThreadUps;

        public ViewHolder(View v) {
            super(v);
            container = v.findViewById(R.id.listItem);
            mThreadTitle = (TextView) v.findViewById(R.id.threadTitle);
            mThreadAuthor = (TextView) v.findViewById(R.id.threadAuthor);
            mThreadCreated = (TextView) v.findViewById(R.id.threadCreated);
            mThreadCommentsNumber = (TextView) v.findViewById(R.id.threadCommentsNumber);
            mThreadUps = (TextView) v.findViewById(R.id.threadUps);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString(EXTRA_URL, customDataSet.get(getAdapterPosition()).getThreadUrl());
                    extras.putString(EXTRA_TITLE, customDataSet.get(getAdapterPosition()).getThreadTitle());
                    intent.putExtras(extras);
                    context.startActivity(intent);
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of data set)
    public RecyclerViewAdapter(Context context, ArrayList<CustomDataSet> customDataSet) {
        this.customDataSet = customDataSet;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);
        // Set the view's size, margins, padding and layout parameters
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your data set at this position
        // - replace the contents of the view with that element
        holder.mThreadTitle.setText(customDataSet.get(position).getThreadTitle());
        holder.mThreadCreated.setText(String.valueOf(
                customDataSet.get(position).getThreadCreated()));
        holder.mThreadAuthor.setText(customDataSet.get(position).getThreadAuthor());
        holder.mThreadCommentsNumber.setText(String.valueOf(
                customDataSet.get(position).getThreadCommentsNumber()));
        holder.mThreadUps.setText(String.valueOf(
                customDataSet.get(position).getThreadUps()));

        // Apply the animation when the view is bound
        setAnimation(holder.container, position);
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return customDataSet.size();
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
