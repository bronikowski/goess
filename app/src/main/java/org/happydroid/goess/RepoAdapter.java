package org.happydroid.goess;

import java.util.ArrayList;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;


public class RepoAdapter extends BaseExpandableListAdapter {

    public ArrayList<String> groupItem, tempChild;
    public ArrayList<Object> childItem = new ArrayList<Object>();
    public LayoutInflater inflater;
    public Activity activity;

    public RepoAdapter(ArrayList<String> grList, ArrayList<Object> childItem) {
        groupItem = grList;
        this.childItem = childItem;
    }

    public void setInflater(LayoutInflater mInflater, Activity a) {
        this.inflater = mInflater;
        activity = a;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        tempChild = (ArrayList<String>) childItem.get(groupPosition);
        TextView text = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.childrow, null);

        }
        text = (TextView) convertView.findViewById(R.id.textView);
        text.setText(tempChild.get(childPosition));
        ViewGroup.LayoutParams params = text.getLayoutParams();
        text.setLayoutParams(new AbsListView.LayoutParams(params.width, params.height));
        return text;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((ArrayList<String>) childItem.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return groupItem.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grouprow, null);
        }
        CheckedTextView tv = (CheckedTextView)convertView.findViewById(R.id.textView);
        tv.setText(groupItem.get(groupPosition));
        tv.setChecked(isExpanded);

        ImageView down = (ImageView)convertView.findViewById(R.id.dropdownImg);
        down.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
        ImageView up = (ImageView)convertView.findViewById(R.id.dropupImg);
        up.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}