package com.example.videodemo.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videodemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miracle on 2020/7/8
 * Email: zhaoqirong96@gmail.com
 * Describe:
 */
public class ModuleAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Module> dataList = new ArrayList<>();

    public ModuleAdapter(Context context) {
        this.context = context;
    }

    public void update(List<Module> dataList) {
        if (dataList != null && dataList.size() > 0) {
            this.dataList = dataList;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.item_moudle, parent, false);
        return new ModuleViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModuleViewHolder VH = (ModuleViewHolder) holder;
        final Module module = dataList.get(position);
        VH.tvTitle.setText(module.title);
        VH.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, module.targetClass);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ModuleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
