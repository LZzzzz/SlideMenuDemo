package com.lzj.slidemenudemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzj.slidemenudemo.view.DragerView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView mLv1, mLv2;
    private DragerView dragerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mLv1 = (ListView) findViewById(R.id.lv_main);
        mLv2 = (ListView) findViewById(R.id.lv_menu);
        mLv1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Commons.NAMES));
        mLv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = Commons.NAMES[position];
                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
            }
        });

        mLv2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Commons.MENU) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(26);
                return view;
            }
        });

        mLv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = Commons.MENU[position];
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });

        final ImageView mIv = (ImageView) findViewById(R.id.iv_header);
        mIv.setOnClickListener(this);
        dragerView = (DragerView) findViewById(R.id.dv);
    }

    @Override
    public void onClick(View v) {
        if (dragerView != null) {
            dragerView.open(true);
        }
    }
}
