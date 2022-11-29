package com.lv.sdumap.ui.todo.view;

import android.graphics.Canvas;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.lv.sdumap.R;
import com.lv.sdumap.utils.SizeUtils;

public class DragSwipeCallback extends ItemTouchHelper.Callback {

    /** 通过此变量通知外界发生了排序、删除等操作 */
    private MyRecyclerViewAdapter.ItemActionCallback callback;

    public DragSwipeCallback(MyRecyclerViewAdapter.ItemActionCallback callback){
        this.callback = callback;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 确定拖拽、滑动支持的方向
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /**
     * 拖拽、交换事件
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        callback.onItemSwapped(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    /**
     * 滑动成功的事件
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        switch (direction) {
            case ItemTouchHelper.END: // START->END 标记完成事件
                callback.onChangeItemState(viewHolder.getAdapterPosition());
                break;
            case ItemTouchHelper.START: // END->START 删除事件
                callback.onItemDeleted(viewHolder.getAdapterPosition());
                break;
            default:
        }
    }

    /**
     * 拖拽、滑动时如何绘制列表
     * actionState只会为ACTION_STATE_DRAG或者ACTION_STATE_SWIPE
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        switch (actionState) {
            case ItemTouchHelper.ACTION_STATE_DRAG:
                // 拖拽时，如果是isCurrentlyActive，则设置translationZ，否则复位
                viewHolder.itemView.setTranslationZ(SizeUtils.dp2px(isCurrentlyActive ? 4 : 0));
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                break;
            case ItemTouchHelper.ACTION_STATE_SWIPE:
                // 滑动时，对view的绘制
                View rootView = viewHolder.itemView;
                View contentView = rootView.findViewById(R.id.todo_item_content_root);
                View actionView = rootView.findViewById(R.id.todo_item_action_root);
                ImageView doneImageView = actionView.findViewById(R.id.todo_item_action_done);
                ImageView deleteImageView = actionView.findViewById(R.id.todo_item_action_delete);
                if (dX < 0) {
                    deleteImageView.setVisibility(View.VISIBLE);
                    doneImageView.setVisibility(View.INVISIBLE);
                    actionView.setBackgroundResource(R.color.app_todo_delete_bg);
                } else {
                    deleteImageView.setVisibility(View.INVISIBLE);
                    doneImageView.setVisibility(View.VISIBLE);
                    actionView.setBackgroundResource(R.color.app_todo_done_bg);
                }
                contentView.setTranslationX(dX);
                break;
            default:
        }
    }

    /**
     * 在onSelectedChanged、onChildDraw、onChildDrawOver操作完成后可以在此进行清楚操作
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        View rootView = viewHolder.itemView;
        View contentView = rootView.findViewById(R.id.todo_item_content_root);
        contentView.setTranslationX(0);
    }
}
