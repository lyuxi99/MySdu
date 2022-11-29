package com.lv.sdumap.ui.library;

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
 * 图书馆预约
 */
public class LibraryFragment extends BaseFragment {

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_library, container, false);
        AutomatedWebView webView = root.findViewById(R.id.id_web_view);
        webView.init();
        View loadingCover = root.findViewById(R.id.loading_cover);
        webView.allowZoom();
        webView.open(new AutomatedWebView.AutoProcedure(
                loadingCover,false,
                "https://pass.sdu.edu.cn/cas/login?service=http%3A%2F%2Fseat.lib.sdu.edu.cn%2Fcas%2Findex.php%3Fcallback%3Dhttp%3A%2F%2Fseat.lib.sdu.edu.cn%2Fhome%2Fweb%2Fseat%2Farea%2F19",
                null,
                "http://seat.lib.sdu.edu.cn/",
                null, null, null)
        );
        return root;
    }


}