package com.xiaosa.testapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private ItemDeletableListView lv;
    private MyAdapter adapter;
    private static ArrayList<String> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.idlv);
        adapter = new MyAdapter(getApplicationContext());
        lv.setAdapter(adapter);
        lv.setOnItemDeleteListener(new ItemDeletableListView.OnDeleteItemListener() {
            @Override
            public void deleteItem(int position) {
                Log.e("TAG", "deleteItem : " + position);
                datas.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

    }

    private static class MyAdapter extends BaseAdapter {

        private Context context;

        public MyAdapter(Context context) {
            for (int i = 0; i < 10; i++) {
                datas.add(String.valueOf(i));
            }
            this.context = context;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_lv, null);
                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
            }
            ((TextView)convertView.findViewById(R.id.tv_item)).setText(datas.get(position));
            return convertView;
        }
    }
}
