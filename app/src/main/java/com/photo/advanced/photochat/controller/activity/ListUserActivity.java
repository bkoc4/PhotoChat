package com.photo.advanced.photochat.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.photo.advanced.photochat.R;
import com.photo.advanced.photochat.helper.DataHelper;
import com.photo.advanced.photochat.model.Message;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ListUserActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    public static final String EXTRA_USER_ID = "extra.user_id";

    @BindView(R.id.lvChats) ListView lvChats;

    List<String> userIds = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        DataHelper.getInstance().getUserCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                userIds.add(document.getId());
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.w("Burak", "Error getting documents.", task.getException());
                        }
                    }
                });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userIds);

        lvChats.setAdapter(adapter);
        lvChats.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = getIntent();
        i.putExtra(EXTRA_USER_ID,userIds.get(position));
        setResult(RESULT_OK,i);
        finish();
    }
}
