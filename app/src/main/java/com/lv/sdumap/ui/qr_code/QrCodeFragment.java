package com.lv.sdumap.ui.qr_code;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.lv.sdumap.R;
import com.lv.sdumap.utils.DoubleClickListener;
import com.lv.sdumap.utils.BaseFragment;
import com.lv.sdumap.utils.Storage;
import com.lv.sdumap.utils.qr_code.QrCode;
import com.lv.sdumap.utils.qr_code.QrSegment;

import java.util.List;


/**
 * 二维码
 */
public class QrCodeFragment extends BaseFragment {
    private TextInputEditText textName;
    private TextInputEditText textIdNumber;
    private Button buttonSave;
    private ImageView imageQrCode;
    private ImageButton buttonQuestion;
    private int maskPattern = 4;
    Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_qr_code, container, false);
        textName = root.findViewById(R.id.id_text_name);
        textIdNumber = root.findViewById(R.id.id_text_id_number);
        buttonSave = root.findViewById(R.id.id_button_save);
        imageQrCode = root.findViewById(R.id.id_img_qr_code);
        buttonQuestion = root.findViewById(R.id.id_btn_question);
        textName.setText(Storage.getSingleton().getString(Storage.KEY_SETTING_QR_CODE_NAME, ""));
        textIdNumber.setText(Storage.getSingleton().getString(Storage.KEY_SETTING_QR_CODE_ID_NUMBER, ""));
        maskPattern = (int) Storage.getSingleton().getLong(Storage.KEY_SETTING_QR_CODE_MASK_PATTERN, 4);
        updateQrCode();
        textIdNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                maskPattern = -1;
                handler.post(() -> updateQrCode());
            }
        });
        imageQrCode.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick() {
                handler.post(() -> changeMaskPattern());
            }
        });
        buttonSave.setOnClickListener(v -> {
            Storage.getSingleton().setString(Storage.KEY_SETTING_QR_CODE_NAME, textName.getText().toString().trim());
            Storage.getSingleton().setString(Storage.KEY_SETTING_QR_CODE_ID_NUMBER, textIdNumber.getText().toString().trim());
            Storage.getSingleton().setLong(Storage.KEY_SETTING_QR_CODE_MASK_PATTERN, maskPattern);
            Toast.makeText(getContext(), "已保存。", Toast.LENGTH_SHORT).show();
        });
        buttonQuestion.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("为什么生成的二维码和山大校园通上的二维码不一样？");
            dialog.setMessage("那是因为二维码的生成中有一个不太重要的参数叫 Mask Pattern，取值从 0 到 7，这生成的 8 个不同的二维码虽然看起来图案不同，但是它们的内容都完全一致，无需担心。\n\n如果想和山大校园通上的二维码保持完全一致，可以双击上面的二维码以切换 Mask Pattern，直至出现想要的二维码图案，再点击“保存”按钮。");
            dialog.setPositiveButton(" OK ", (dialog1, id) -> dialog1.dismiss());
            dialog.show();
        });
        return root;
    }

    /**
     * 更新二维码
     */
    void updateQrCode() {
        String text = textIdNumber.getText().toString().trim();
        if(text.isEmpty()){
            imageQrCode.setImageBitmap(null);
            buttonQuestion.setVisibility(View.INVISIBLE);
            return;
        }
        buttonQuestion.setVisibility(View.VISIBLE);
        List<QrSegment> segs = QrSegment.makeSegments(text);
        QrCode qr = QrCode.encodeSegments(segs, QrCode.Ecc.LOW, 1, 5, maskPattern, false);
        Bitmap img = qr.toImage(11, 1); // Convert to bitmap image
        imageQrCode.setImageBitmap(img);
    }

    /**
     * 切换 mask pattern
     */
    void changeMaskPattern() {
        maskPattern = (maskPattern + 1) % 8;
        updateQrCode();
    }

}