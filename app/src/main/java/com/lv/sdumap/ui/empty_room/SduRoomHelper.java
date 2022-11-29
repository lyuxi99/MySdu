package com.lv.sdumap.ui.empty_room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.lv.sdumap.R;
import com.lv.sdumap.utils.SizeUtils;
import com.lv.sdumap.utils.Storage;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 教室查询的辅助类
 */
public class SduRoomHelper {
    OkHttpClient client = new OkHttpClient();
    Map<String, String> buildingName2IdMap; // 教学楼名到id的映射
    int numViews = 0; // 记录动态添加了多少views

    SduRoomHelper() {
        buildingName2IdMap = new HashMap<>();
        buildingName2IdMap.put("振声苑", "DFEA42382ACB4BAE898C516B5EC86BD2");
        buildingName2IdMap.put("K5楼", "63A1EF8676B747DE8B6DAF42B0AE8F1B");
        buildingName2IdMap.put("公共实验教学中心", "32FBB900E5714D78AD5567E6A40932B3");
        buildingName2IdMap.put("会文北楼", "B5A8BF56F2CA4045A5FFBE938B409156");
        buildingName2IdMap.put("会文南楼", "47EBCDEF3E344169808AC4BC4B823A9B");
        buildingName2IdMap.put("第周苑A座", "FC7B80C91DE54E2289C61547653A6A56");
        buildingName2IdMap.put("第周苑B座", "3C271729A7CA49C8A9E58C0A4211AC7B");
        buildingName2IdMap.put("淦昌苑E座", "08C9D65D61A6444DABE7FC934C792994");
    }

    /**
     * 发出查教室请求
     *
     * @param semester   学期
     * @param weekId     周
     * @param weekday    星期
     * @param buildingId 教学楼
     * @return 原始 html
     */
    private String request(String semester, int weekId, int weekday, String buildingId) {
        Request request;
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String body = String.format(Locale.CHINESE, "gnq_mh=&jsmc_mh=&typewhere=jwyx&xnxqh=%s&xqbh=07&jxlbh=%s&zc=%d&zc2=%d&xq=%d&xq2=%d", semester, buildingId, weekId, weekId, weekday, weekday);

        request = new Request.Builder()
                .url("https://bkzhjx.wh.sdu.edu.cn/jiaowu/kxjsgl/kxjsgl.do?method=queryKxxxByJs_sddxFP&typewhere=jwyx")
                .method("POST", RequestBody.create(mediaType, body))
                .addHeader("Origin", "https://bkzhjx.wh.sdu.edu.cn")
                .addHeader("Referer", "https://bkzhjx.wh.sdu.edu.cn/jiaowu/pkgl/jsjy/queryKxByJs_sddxFP.jsp")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.body() == null) return null;
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析一行数据，返回一个 Room 对象
     *
     * @param html 一行数据
     * @return Room 对象
     */
    private Room parseRow(String html) {
        Room room = new Room();
        Pattern r = Pattern.compile("<td[^>]*>\\s*<p[^>]*>([^\\(]*)\\(([0-9]*)[\\s\\S]*<\\/p>\\s*<\\/td>");
        Matcher m = r.matcher(html);
        m.find();
        room.name = m.group(1).trim().replace("青岛校区", "").replace("青岛实验教学中心", "");
        room.numSeats = m.group(2);
        r = Pattern.compile("<td[^>]*>\\s*(<font[^>]*>([\\s\\S]*?)<\\/font>)?\\s*<\\/td>");
        m = r.matcher(html);
        for (int i = 0; i < 5; i++) {
            m.find();
            String mark = m.group(2);
            if (mark != null) room.status[i] = mark.trim();
        }
        return room;
    }

    /**
     * 解析 html，返回一个 List<Room>
     *
     * @param html html
     * @return 解析出的所有 Room
     */
    private List<Room> parseHtml(String html) {
        List<Room> rooms = new ArrayList<>();
        html = html.replace("&nbsp;", " ");
        Pattern r = Pattern.compile("<tbody>[\\s\\S]*<\\/tbody>");
        Matcher m = r.matcher(html);
        m.find();
        html = m.group();
        r = Pattern.compile("<tr[^>]*>[\\s\\S]*?<\\/tr>");
        m = r.matcher(html);
        while (m.find()) {
            rooms.add(parseRow(m.group()));
        }
        return rooms;
    }

    /**
     * 查询该日该教学楼的教室状态
     *
     * @param date     日期
     * @param building 教学楼
     * @return 解析出的所有 Room
     */
    public List<Room> query(String date, String building) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strTime = Storage.getSingleton().getString(Storage.KEY_CLOUD_SYLLABUS_FIRST_DAY, "2022-09-05");
        try {
            long firstDay = dateFormat.parse(strTime).getTime() / 1000 / 3600 / 24;
            long tarDay = dateFormat.parse(date).getTime() / 1000 / 3600 / 24;
            long dif = tarDay - firstDay;
            int weekId = (int) (dif / 7) + 1;
            int weekday = (int) (dif % 7) + 1;
            String html = request(Storage.getSingleton().getString(Storage.KEY_CLOUD_CUR_SEMESTER, "2022-2023-1"), weekId, weekday, buildingName2IdMap.get(building));
            List<Room> rooms = parseHtml(html);
            Collections.sort(rooms);
            return rooms;
        } catch (ParseException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 删除所有动态创建的 views
     *
     * @param context    context
     * @param gridLayout parent gridLayout
     */
    public void clearViews(Context context, GridLayout gridLayout) {
        gridLayout.removeViews(4, numViews);
        numViews = 0;
    }

    /**
     * 绘制 views 以展示数据
     *
     * @param context    context
     * @param rooms      教室状态信息
     * @param gridLayout parent gridLayout
     */
    public void drawViews(Context context, List<Room> rooms, GridLayout gridLayout) {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            boolean isGray = i % 2 == 1;
            TextView tv = newTextView(context, room.getNameWithSeats(), isGray, 1);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setPadding(SizeUtils.dp2px(20), 0, 0, 0);
            gridLayout.addView(tv);
            for (int j = 0; j < 5; j++) {
                if (room.status[j].isEmpty())
                    gridLayout.addView(newTextViewWithColor(context, "闲", isGray, 1, Color.rgb(34, 139, 34), true));
                else
                    gridLayout.addView(newTextViewWithColor(context, "占", isGray, 1, Color.LTGRAY, false));
            }
        }
    }

    /**
     * 创建 TextView
     */
    private TextView newTextViewWithColor(Context context, String text, boolean isGray, int weight, int color, boolean bold) {
        TextView tv = newTextView(context, text, isGray, weight);
        tv.setTextColor(color);
        if (bold) tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }

    /**
     * 创建 TextView
     */
    private TextView newTextView(Context context, String text, boolean isGray, int weight) {
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, weight);
        int margin = SizeUtils.dp2px(2);
        layoutParams.setMargins(margin, margin, margin, margin);
        layoutParams.height = SizeUtils.dp2px(30);
        TextView tv = new TextView(context);
        tv.setLayoutParams(layoutParams);
        tv.setGravity(Gravity.CENTER);
        tv.setText(text);
        tv.setHeight(SizeUtils.dp2px(30));
        if (isGray) tv.setBackground(context.getResources().getDrawable(R.drawable.gray));
        numViews++;
        return tv;
    }
}
