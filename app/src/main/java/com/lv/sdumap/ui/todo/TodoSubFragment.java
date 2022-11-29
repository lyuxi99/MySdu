package com.lv.sdumap.ui.todo;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.lv.sdumap.R;
import com.lv.sdumap.ui.todo.view.DragSwipeCallback;
import com.lv.sdumap.ui.todo.view.MyRecyclerViewAdapter;

/**
 * 事项子页面，实例化为两个：一个“未完成”，另一个“已完成”
 */
public class TodoSubFragment extends Fragment {

    private static final String ARG_PARAM_PAGE_CHECKED = "param_page_checked";
    private boolean pageChecked = false;
    private int state = 0;
    private RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;
    private FloatingActionButton floatingActionButtonAdd;
    TodoDataManager dataManager = TodoDataManager.getSingleton();
    private boolean firstTime = true;
    MyRecyclerViewAdapter.ItemActionCallback itemActionCallback;
    private TextView textViewEmptyHint;

    public TodoSubFragment() {
        // Required empty public constructor
    }

    public static TodoSubFragment newInstance(boolean page_checked) {
        TodoSubFragment fragment = new TodoSubFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM_PAGE_CHECKED, page_checked);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageChecked = getArguments().getBoolean(ARG_PARAM_PAGE_CHECKED);
            state = pageChecked ? 1 : 0;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_todo_sub, container, false);

        recyclerView = root.findViewById(R.id.id_recycler_view);
        textViewEmptyHint = root.findViewById(R.id.id_empty_hint);
        itemActionCallback = new MyRecyclerViewAdapter.ItemActionCallback() {
            @Override
            public void onTextClick(int position) {
                editItem(position);
            }

            @Override
            public void onChangeItemState(int position) {
                String item = dataManager.getItem(state, position);
                dataManager.changeState(state, position);
                adapter.notifyItemRemoved(position);
                updateEmptyHintVisibility();
                Snackbar snackbar = Snackbar.make(getView(), "已完成。", Snackbar.LENGTH_LONG);
                snackbar.setAction("撤销", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onAddItem(item);
                        dataManager.deleteItem(1 - state, 0);
                        Toast.makeText(getContext(), "已撤销", Toast.LENGTH_SHORT).show();
                    }
                });
                snackbar.show();
            }

            @Override
            public void onItemSwapped(int fromPosition, int toPosition) {
                dataManager.swapItem(state, fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onItemDeleted(int position) {
                String item = dataManager.getItem(state, position);
                dataManager.deleteItem(state, position);
                adapter.notifyItemRemoved(position);
                updateEmptyHintVisibility();
                Snackbar snackbar = Snackbar.make(getView(), "已删除。", Snackbar.LENGTH_LONG);
                snackbar.setAction("撤销", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onAddItem(item);
                        Toast.makeText(getContext(), "已撤销", Toast.LENGTH_SHORT).show();
                    }
                });
                snackbar.show();
            }
        };
        adapter = new MyRecyclerViewAdapter(getContext(), state, root, itemActionCallback);
        ItemTouchHelper.Callback callback = new DragSwipeCallback(itemActionCallback);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        touchHelper.attachToRecyclerView(recyclerView);
        floatingActionButtonAdd = root.findViewById(R.id.id_floating_btn_add);
        if (pageChecked) {
            floatingActionButtonAdd.setVisibility(View.GONE);
        } else {
            floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createNewItem();
                }
            });
        }
        return root;
    }

    public void updateEmptyHintVisibility(){
        textViewEmptyHint.setVisibility(dataManager.getLength(state)>0 ? View.INVISIBLE: View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateEmptyHintVisibility();
        if (!firstTime) {
            adapter.notifyDataSetChanged();
        }
        firstTime = false;
    }

    public void onAddItem(String item) {
        dataManager.addItem(state, item);
        adapter.notifyItemInserted(0);
        recyclerView.smoothScrollToPosition(0);
        updateEmptyHintVisibility();
    }

    public void createNewItem() {
        editItem(-1);
    }

    /**
     * 处理编辑或创建
     * @param position
     */
    void editItem(int position) {
        final EditText inputServer = new EditText(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (position < 0) {
            builder.setTitle("新建");
        } else {
            builder.setTitle("编辑");
            inputServer.setText(dataManager.getItem(state, position));
        }
        builder.setView(inputServer)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String str = inputServer.getText().toString();
                if (str.isEmpty()) {
                    Toast.makeText(getContext(), "输入为空。", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (position < 0) {
                    onAddItem(str);
                } else {
                    dataManager.modifyItem(state, position, str);
                    adapter.notifyItemChanged(position);
                }

                Toast.makeText(getContext(), "已保存。", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog ad = builder.create();
        ad.setCancelable(true);
        ad.setCanceledOnTouchOutside(true);
        ad.show();
        inputServer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                inputServer.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(inputServer, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        inputServer.requestFocus();
    }

}