package com.lv.sdumap.ui.todo.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lv.sdumap.R;
import com.lv.sdumap.ui.todo.TodoDataManager;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private View anchorView;
    private ItemActionCallback itemActionCallback;
    private int state;
    private TodoDataManager dataManager = TodoDataManager.getSingleton();

    public MyRecyclerViewAdapter(Context context, int state, View anchorView, ItemActionCallback itemActionCallback) {
        this.state = state;
        this.itemActionCallback = itemActionCallback;
        mContext = context;
        this.anchorView = anchorView;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.recycle_view_todo_item, viewGroup, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder slideViewHolder, int i) {
        slideViewHolder.tvContentText.setText(dataManager.getItem(state, i));
    }

    @Override
    public int getItemCount() {
        return dataManager.getLength(state);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvContentText;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContentText = itemView.findViewById(R.id.todo_item_content_text);
            tvContentText.setOnClickListener(v -> MyRecyclerViewAdapter.this.itemActionCallback.onTextClick(getAdapterPosition()));
            CheckBox checkBox = itemView.findViewById(R.id.todo_item_checkbox);
            if (state > 0) {
                checkBox.setChecked(true);
                ImageView imageViewDone = itemView.findViewById(R.id.todo_item_action_done);
                imageViewDone.setImageResource(R.drawable.ic_todo_undo);
            }
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if ((isChecked ? 1 : 0) != state) {
                    MyRecyclerViewAdapter.this.itemActionCallback.onChangeItemState(getAdapterPosition());
                    checkBox.setChecked(!isChecked);
                }
            });
        }
    }

    public interface ItemActionCallback {
        void onTextClick(int position);

        void onChangeItemState(int position);

        void onItemSwapped(int fromPosition, int toPosition);

        void onItemDeleted(int position);

    }
}