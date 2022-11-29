package com.lv.sdumap.ui.empty_room;

import androidx.appcompat.app.AlertDialog;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;

import com.lv.sdumap.R;
import com.lv.sdumap.utils.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 空教室查询
 */
public class EmptyRoomFragment extends BaseFragment {
    SduRoomHelper sduRoomHelper = new SduRoomHelper();
    Handler handler = new Handler();
    GridLayout gridLayout;
    EditText textDate, textBuilding;
    View loadingCover;

    public static EmptyRoomFragment newInstance() {
        return new EmptyRoomFragment();
    }

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_empty_room, container, false);
        gridLayout = root.findViewById(R.id.grid_layout_rooms);
        textDate = root.findViewById(R.id.editTextDate);
        textBuilding = root.findViewById(R.id.editTextBuilding);
        loadingCover = root.findViewById(R.id.loading_cover);

        textDate.setText(dateFormat.format(new Date()));
        textDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                String[] parts = textDate.getText().toString().split("-");
                int mYear = Integer.parseInt(parts[0]);
                int mMonth = Integer.parseInt(parts[1]) - 1;
                int mDay = Integer.parseInt(parts[2]);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                textDate.setText(String.format(Locale.CHINESE, "%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
                                reloadData();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        textBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] buildings = {"振声苑", "会文北楼", "会文南楼", "第周苑A座", "第周苑B座", "淦昌苑E座", "K5楼", "公共实验教学中心",};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("选择教学楼");
                builder.setItems(buildings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textBuilding.setText(buildings[which]);
                        reloadData();
                    }
                });
                builder.show();
            }
        });
        reloadData();
        return root;
    }

    void reloadData() {
        handler.post(() -> loadingCover.setVisibility(View.VISIBLE));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    handler.post(() -> sduRoomHelper.clearViews(getContext(), gridLayout));
                    List<Room> rooms = sduRoomHelper.query(textDate.getText().toString(), textBuilding.getText().toString());
                    handler.post(() -> sduRoomHelper.drawViews(getContext(), rooms, gridLayout));
                    handler.post(() -> loadingCover.setVisibility(View.INVISIBLE));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}