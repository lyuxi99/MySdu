package com.lv.sdumap.ui.syllabus;

import com.lv.sdumap.ui.syllabus.model.MySubject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 课程表解析器
 */
public class SyllabusParser {
    private List<MySubject> courses;

    /**
     * 从周字符串中解析出列表，如 1-12,16
     * @param weeksString 周字符串
     * @return 列表
     */
    public static List<Integer> getWeekList(String weeksString) {
        List<Integer> weekList = new ArrayList<>();
        if (weeksString == null || weeksString.length() == 0) return weekList;

        weeksString = weeksString.replaceAll("[^\\d\\-\\,]", "");
        if (weeksString.contains(",")) {
            String[] arr = weeksString.split(",");
            for (String s : arr) {
                weekList.addAll(getWeekList2(s));
            }
        } else {
            weekList.addAll(getWeekList2(weeksString));
        }
        return weekList;
    }

    /**
     * 处理简单的周范围字符串
     * @param weeksString 周字符串
     * @return 列表
     */
    public static List<Integer> getWeekList2(String weeksString) {
        List<Integer> weekList = new ArrayList<>();
        int first = -1, end = -1, index = -1;
        if ((index = weeksString.indexOf("-")) != -1) {
            first = Integer.parseInt(weeksString.substring(0, index));
            end = Integer.parseInt(weeksString.substring(index + 1));
        } else {
            first = Integer.parseInt(weeksString);
            end = first;
        }
        for (int i = first; i <= end; i++)
            weekList.add(i);
        return weekList;
    }

    /**
     * 从 html 的一格表格解析出该格中的课程表，存入courses
     * @param html html 的一格表格
     * @param slot 时间槽
     * @param weekday 星期
     */
    private void parseSlotWeekdayHtml(String html, int slot, int weekday) {
        // ">([^<]*)\\((sd[^)]*)\\)<br\\/?><font title=['\\\"]教师['\\\"]>([^<]*)<\\/font><br\\/?><font title=['\\\"]周次\\(节次\\)['\\\"]>第([0-9\\-]*)周\\(周\\)\\[([0-9\\-]*)节\\]<\\/font>(<br\\/?><font title=['\\\"]教学楼['\\\"][^>]*>([^<]*)<\\/font><font title=['\\\"]教室['\\\"]>([^<]*)<\\/font>)?"
        String pattern = ">(?<name>[^<]*)\\((?<cid>sd[^)]*)\\)(<br\\/?>[^<]*)*<font title=['\\\"]教师['\\\"]>(?<teacher>[^<]*)<\\/font><br\\/?><font title=['\\\"]周次\\(节次\\)['\\\"]>第(?<week>[0-9\\-]*)周\\(周\\)\\[([0-9\\-]*)节\\]<\\/font>(<br\\/?><font title=['\\\"]教学楼['\\\"][^>]*>([^<]*)<\\/font><font title=['\\\"]教室['\\\"]>(?<room>[^<]*)<\\/font>)?";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(html);
        while (m.find()) {
            String name = m.group("name").trim();
            String courseID = m.group("cid").trim();
            String teacher = m.group("teacher").trim();
            String weekid = m.group("week").trim();
            String room = "";
            if (m.group("room") != null)
                room = m.group("room").replaceAll("青岛校区", "").replaceAll("振声苑振声苑", "振声苑");
            courses.add(new MySubject(courseID, name, "", room, teacher, getWeekList(weekid), slot, 1, weekday, -1, null));
        }
    }

    /**
     * 从 html 的一行表格解析出该时间槽的一周课程表，存入courses
     * @param html html 的一行表格
     * @param slot 时间槽
     */
    private void parseSlotHtml(String html, int slot) {
        Pattern r = Pattern.compile("<td[\\s\\S]*?<\\/td>");
        Matcher m = r.matcher(html);
        for (int weekday = 1; weekday <= 7; weekday++) {
            m.find();
            parseSlotWeekdayHtml(m.group(), slot, weekday);
        }
    }

    /**
     * 从 html 中解析出课程表
     * @param html html
     * @return 课程表
     */
    public List<MySubject> parseHtml(String html) {
        courses = new ArrayList<>();
        Pattern r = Pattern.compile("<tr[\\s\\S]*?<\\/tr>");
        Matcher m = r.matcher(html);
        m.find();
        for (int slot = 1; slot <= 5; slot++) {
            m.find();
            parseSlotHtml(m.group(), slot);
        }
        return courses;
    }

}
