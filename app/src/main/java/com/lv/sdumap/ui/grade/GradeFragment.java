package com.lv.sdumap.ui.grade;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lv.sdumap.R;
import com.lv.sdumap.utils.AutomatedWebView;
import com.lv.sdumap.utils.BaseFragment;

import java.util.List;

/**
 * 成绩查询
 */
public class GradeFragment extends BaseFragment {
    AutomatedWebView hiddenWebView;
    Handler emptyHandler = new Handler();
    GridLayout gridLayout;
    View loadingCover;
    List<Grade> grades;

    public static GradeFragment newInstance() {
        return new GradeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_grade, container, false);
        hiddenWebView = root.findViewById(R.id.grade_hidden_web_view);
        gridLayout = root.findViewById(R.id.gridLayout);
        loadingCover = root.findViewById(R.id.loading_cover);
        hiddenWebView.init();
        emptyHandler.postDelayed(this::retrieveGrades, 500);
        return root;
    }

    /**
     * 用获取的数据更新 ui
     *
     * @param html 获取到的 html
     */
    private void updateData(String html) {
        try {
            grades = new GradeParser().parseHtml(html);
            Toast.makeText(GradeFragment.this.getContext(), "成绩获取成功。", Toast.LENGTH_SHORT).show();

            for (int i = 0; i < grades.size(); i++) {
                grades.get(i).addRow(this.getContext(), gridLayout, i);
            }
        } catch (Exception e) {
            try {
                Toast.makeText(GradeFragment.this.getContext(), "成绩获取错误。", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * 获取成绩并更新 ui
     */
    public void retrieveGrades() {
        hiddenWebView.setVisibility(View.VISIBLE);
        hiddenWebView.open(new AutomatedWebView.AutoProcedure(
                loadingCover, true,
                "http://bkzhjx.wh.sdu.edu.cn/sso.jsp",
                "https://bkzhjx.wh.sdu.edu.cn/jsxsd/framework/xsMainV.htmlx",
                "https://bkzhjx.wh.sdu.edu.cn/jsxsd/kscj/cjcx_list",
                null, null,
                html -> emptyHandler.post(() -> {
                    hiddenWebView.setVisibility(View.INVISIBLE);
                    updateData(html);
                })
        ));
    }
}
