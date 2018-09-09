package com.photo.advanced.photochat.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.photo.advanced.photochat.R;
import com.photo.advanced.photochat.model.Message;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ConversationsAdapter extends ArrayAdapter<Message> {


    private List<Message> dataList;

    public ConversationsAdapter(@NonNull Context context, @NonNull List<Message> objects) {
        super(context, R.layout.list_item_chat, objects);
        dataList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View itemView = convertView;
        ViewHolder viewHolder;

        Message message = dataList.get(position);

        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_chat, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.ivStatusImage = itemView.findViewById(R.id.ivStatusImage);
            viewHolder.tvDate = itemView.findViewById(R.id.tvDate);
            viewHolder.tvFromUser = itemView.findViewById(R.id.tvFromUser);

            itemView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) itemView.getTag();
        }

        viewHolder.tvFromUser.setText(message.getFromUserId());
        viewHolder.ivStatusImage.setBackgroundResource(message.isRead()? R.drawable.rectangle : R.drawable.rounded_rectange);

        return itemView;
    }

    private class ViewHolder {
        ImageView ivStatusImage;
        TextView tvFromUser;
        TextView tvDate;
    }
}