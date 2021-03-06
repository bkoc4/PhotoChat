package com.photo.advanced.photochat.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.photo.advanced.photochat.R;
import com.photo.advanced.photochat.adapter.ConversationsAdapter;
import com.photo.advanced.photochat.controller.activity.ShowCaptureActivity;
import com.photo.advanced.photochat.helper.DataHelper;
import com.photo.advanced.photochat.model.Message;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ConversationsFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.lvChats) ListView lvChats;

    private ConversationsAdapter adapter;
    private List<Message> dataList = new ArrayList<>();

    public static final String EXTRA_OPEN_CHAT_IMAGE = "extra.open_chat_image";

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_chat;
    }

    @Override
    public void initView(View root, @Nullable ViewGroup container, Bundle savedInstanceState) {

        DataHelper.getInstance().getUserCollection()
                .document(DataHelper.getInstance().getUserAuth().getUid())
                .collection("received")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Message data = document.toObject(Message.class);
                                data.setId(document.getId());
                                dataList.add(data);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.w("Burak", "Error getting documents.", task.getException());
                        }
                    }
                });
        adapter = new ConversationsAdapter(getContext(), dataList);

        lvChats.setAdapter(adapter);
        lvChats.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(getActivity(), ShowCaptureActivity.class);
        intent.putExtra(EXTRA_OPEN_CHAT_IMAGE,dataList.get(position).getId());
        startActivity(intent);
    }
}
