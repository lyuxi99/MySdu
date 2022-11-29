package com.lv.sdumap.ui.grade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 成绩解析器
 */
public class GradeParser {
    private List<Grade> grades;

    private void jumpMatches(Matcher m, int num) {
        for (int i = 0; i < num; i++) m.find();
    }

    /**
     * 解析一条成绩
     *
     * @param html 该行 html
     */
    private void parseRow(String html) {
        Grade grade = new Grade();
        Pattern r = Pattern.compile("<td[^>]*>(<!--[\\s\\S].*?-->)?([^<]*?)<\\/td>");
        Matcher m = r.matcher(html);
        jumpMatches(m, 2);
        grade.semester = m.group(2).trim();
        jumpMatches(m, 2);
        grade.name = m.group(2).trim();
        jumpMatches(m, 2);
        grade.gradePart1 = m.group(2).trim();
        m.find();
        grade.gradePart2 = m.group(2).trim();
        m.find();
        grade.grade = m.group(2).trim();
        m.find();
        grade.gpa = m.group(2).trim();
        m.find();
        grade.level = m.group(2).trim();
        jumpMatches(m, 2);
        grade.credits = m.group(2).trim();
        grades.add(grade);
    }

    /**
     * 过滤出最新学期并按学分降序排序
     */
    private void filterAndSort() {
        String maxSemester = "0000";
        for (Grade g : grades) {
            if (g.semester.compareTo(maxSemester) > 0) maxSemester = g.semester;
        }
        String finalMaxSemester = maxSemester;
        grades = grades.stream().filter(g -> finalMaxSemester.equals(g.semester)).collect(Collectors.toList());
        Collections.sort(grades);
    }

    /**
     * 解析从教务获取到的 html
     *
     * @param html html
     * @return 解析到的成绩列表
     */
    public List<Grade> parseHtml(String html) {
        grades = new ArrayList<>();
        Pattern r = Pattern.compile("<tr>[\\s\\S]*?<\\/tr>");
        Matcher m = r.matcher(html);
        m.find();
        while (m.find()) {
            parseRow(m.group());
        }
        if (grades.size() == 0) return grades;
        filterAndSort();
        return grades;
    }
}
