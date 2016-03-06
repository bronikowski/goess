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
    ArrayList<String> games = new ArrayList<String>();
    LayoutInflater inflater;


    public GamesRepoListAdapter(Activity context, ArrayList<String> games) {
        super(context, R.layout.repoitem, games);
        this.context = context;
        this.games = games;
        inflater = context.getLayoutInflater();
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.repoitem, null);
        }

        TextView txtTitle = (TextView) view.findViewById(R.id.gameTitle);
        txtTitle.setText(games.get(position));

        return view;
    }
}
