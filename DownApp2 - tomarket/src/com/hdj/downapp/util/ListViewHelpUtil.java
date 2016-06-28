package com.hdj.downapp.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.hdj.downapp_market.MyAdapter;

public class ListViewHelpUtil {
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		BaseAdapter listAdapter=(BaseAdapter) listView.getAdapter();
		if(listAdapter==null){
			return;
		}
		int totalHeight=0;
		for (int i = 0, len=listAdapter.getCount();i < len; i++) {
			View listItem=listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight+=listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params=listView.getLayoutParams();
		params.height=totalHeight+(listView.getDividerHeight()*listAdapter.getCount()-1);
		listView.setLayoutParams(params);
	}

}
