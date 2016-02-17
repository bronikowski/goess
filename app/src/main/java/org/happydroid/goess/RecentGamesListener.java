package org.happydroid.goess;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RecentGamesListener implements DialogInterface.OnShowListener {

    @Override
    public void onShow(DialogInterface alert) {
        ListView listView = ((AlertDialog) alert).getListView();
        final ListAdapter originalAdapter = listView.getAdapter();

        listView.setAdapter(new ListAdapter() {

            @Override
            public int getCount() {
                return originalAdapter.getCount();
            }

            @Override
            public Object getItem(int id) {
                return originalAdapter.getItem(id);
            }

            @Override
            public long getItemId(int id) {
                return originalAdapter.getItemId(id);
            }

            @Override
            public int getItemViewType(int id) {
                return originalAdapter.getItemViewType(id);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = originalAdapter.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(12);
                return view;
            }

            @Override
            public int getViewTypeCount() {
                return originalAdapter.getViewTypeCount();
            }

            @Override
            public boolean hasStableIds() {
                return originalAdapter.hasStableIds();
            }

            @Override
            public boolean isEmpty() {
                return originalAdapter.isEmpty();
            }

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {
                originalAdapter.registerDataSetObserver(observer);

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {
                originalAdapter.unregisterDataSetObserver(observer);

            }

            @Override
            public boolean areAllItemsEnabled() {
                return originalAdapter.areAllItemsEnabled();
            }

            @Override
            public boolean isEnabled(int position) {
                return originalAdapter.isEnabled(position);
            }

        });
    }
}
