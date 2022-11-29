package com.lv.sdumap.ui.apply_go_out;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lv.sdumap.R;
import com.lv.sdumap.utils.AutomatedWebView;
import com.lv.sdumap.utils.BaseFragment;

/**
 * 出校申请
 */
public class ApplyGoOutFragment extends BaseFragment {

    View loadingCover;

    public static ApplyGoOutFragment newInstance() {
        return new ApplyGoOutFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_apply_go_out, container, false);
        AutomatedWebView webView = root.findViewById(R.id.id_web_view);
        webView.init();
        loadingCover = root.findViewById(R.id.loading_cover);
        webView.open(new AutomatedWebView.AutoProcedure(
                loadingCover,false,
                "https://pass.sdu.edu.cn/cas/login?service=https%3A%2F%2Fscenter.sdu.edu.cn%2Ftp_fp%2Findex.jsp",
                "https://scenter.sdu.edu.cn/tp_fp/view?m=fp#act=fp/formHome",
                "https://scenter.sdu.edu.cn/tp_fp/view?m=fp#from=hall&serveID=87dc6da9-9ad8-4458-9654-90823be0d5f6&act=fp/serveapply",
                null, null, null)
        );
        return root;
    }
}