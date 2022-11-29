package com.lv.sdumap.ui.misc;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.lv.sdumap.R;
import com.lv.sdumap.utils.BaseFragment;

/**
 * 功能
 */
public class MiscFragment extends BaseFragment {

    public static MiscFragment newInstance() {
        return new MiscFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_misc, container, false);
        root.findViewById(R.id.id_btn_map).setOnClickListener(
                v -> Navigation.findNavController(v).navigate(R.id.action_navigation_misc_to_navigation_map));
        root.findViewById(R.id.id_btn_library).setOnClickListener(
                v -> Navigation.findNavController(v).navigate(R.id.action_navigation_misc_to_libraryFragment));
        root.findViewById(R.id.id_btn_apply_go_out).setOnClickListener(
                v -> Navigation.findNavController(v).navigate(R.id.action_navigation_misc_to_applyGoOutFragment));
        root.findViewById(R.id.id_btn_exam_schedule).setOnClickListener(
                v -> Navigation.findNavController(v).navigate(R.id.action_navigation_misc_to_examScheduleFragment));
        root.findViewById(R.id.id_btn_grade).setOnClickListener(
                v -> Navigation.findNavController(v).navigate(R.id.action_navigation_misc_to_navigation_grade));
        root.findViewById(R.id.id_btn_qr_code).setOnClickListener(
                v -> Navigation.findNavController(v).navigate(R.id.action_navigation_misc_to_qrCodeFragment));
        root.findViewById(R.id.id_btn_empty_room).setOnClickListener(
                v -> Navigation.findNavController(v).navigate(R.id.action_navigation_misc_to_emptyRoomFragment));

        return root;
    }

}