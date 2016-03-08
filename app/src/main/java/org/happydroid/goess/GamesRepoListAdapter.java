package org.happydroid.goess;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GamesRepoListAdapter extends ArrayAdapter<String>  {

    private final Activity context;
    String[] games;
    ArrayList<Integer> iconsVisible = new ArrayList<>();
    LayoutInflater inflater;


    public GamesRepoListAdapter(Activity context, String[] games) {
        super(context, R.layout.repoitem, games);
        this.context = context;
        this.games = games;
        inflater = context.getLayoutInflater();
        iconsVisible.clear();
    }


    static class ViewHolder {
        TextView txtTitle;
        ImageView icon;
        int position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (view == null) {
            view = inflater.inflate(R.layout.repoitem, null);
            viewHolder = new ViewHolder();
            viewHolder.position = position;
            viewHolder.txtTitle = (TextView) view.findViewById(R.id.gameTitle);
            viewHolder.icon = (ImageView) view.findViewById(R.id.img);
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();

        viewHolder.txtTitle.setText(games[position]);

        if (iconsVisible.contains(viewHolder.position)) {
            viewHolder.icon.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
