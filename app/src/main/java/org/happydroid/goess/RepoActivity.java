package org.happydroid.goess;


import java.util.ArrayList;
import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;


public class RepoActivity extends ExpandableListActivity implements OnChildClickListener {

    ArrayList<String> classic;
    ArrayList<String> japanese;
    ArrayList<String> korean;
    ArrayList<String> chinese;
    ArrayList<String> european;

    ArrayList<String> categories = new ArrayList<String>();
    ArrayList<Object> games = new ArrayList<Object>();

    RepoAdapter repoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        classic = intent.getStringArrayListExtra("classic");
        japanese = intent.getStringArrayListExtra("japanese");
        korean = intent.getStringArrayListExtra("korean");
        chinese = intent.getStringArrayListExtra("chinese");
        european = intent.getStringArrayListExtra("european");

        ExpandableListView expandableListView = getExpandableListView();
        expandableListView.setGroupIndicator(null);
        expandableListView.setClickable(true);

        setGamesCategory();
        setChildGames();

        repoAdapter = new RepoAdapter(categories, games);
        repoAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
        getExpandableListView().setAdapter(repoAdapter);
        expandableListView.setOnChildClickListener(this);
    }

    public void setGamesCategory() {
        categories.add("Classic");
        categories.add("Japanese");
        categories.add("Korean");
        categories.add("Chinese");
        categories.add("European");
    }

    public void setChildGames() {
        games.add(classic);
        games.add(japanese);
        games.add(korean);
        games.add(chinese);
        games.add(european);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        String name = "";
        switch (groupPosition) {
            case 0:
                name = classic.get(childPosition);
            break;
            case 1:
                name = japanese.get(childPosition);
                break;
            case 2:
                name = korean.get(childPosition);
                break;
            case 3:
                name = chinese.get(childPosition);
                break;
            case 4:
                name = european.get(childPosition);
                break;

        }
        setResult(Activity.RESULT_OK, new Intent().putExtra("gamename", name));
        finish();

        return true;
    }

}
