package com.lv.sdumap.utils;

import com.tencent.mmkv.MMKV;

/**
 * 存储类
 */
public class Storage {
    // key 末尾的的字母表示数据类型，l for long, s for string, 默认为 string
    public static final String KEY_CLOUD_CUR_SEMESTER = "cloud_cur_semester_s";
    public static final String KEY_CLOUD_SYLLABUS_FIRST_DAY = "cloud_syllabus_first_day_s";
    public static final String KEY_CLOUD_SYLLABUS_TIME = "cloud_syllabus_time_s";
    public static final String KEY_CLOUD_GITHUB_LINK = "cloud_github_link_s";
    public static final String KEY_CLOUD_SHARE_QR_URL = "cloud_share_qr_url_s";
    public static final String KEY_CLOUD_SHARE_TEXT = "cloud_share_text_s";

    public static final String KEY_USER_NAME = "SECRET_user_name_s";
    public static final String KEY_USER_PASSWORD = "SECRET_user_password_s";
    public static final String KEY_SETTING_SYLLABUS_SHOW_WEEKEND = "setting_syllabus_show_weekend_l";
    public static final String KEY_SETTING_SYLLABUS_COURSE_COMMENT_PREFIX = "setting_syllabus_course_comment_";
    public static final String KEY_SETTING_SYLLABUS_HTML = "setting_syllabus_html_s";
    public static final String KEY_SETTING_QR_CODE_NAME = "setting_qr_code_name_s";
    public static final String KEY_SETTING_QR_CODE_ID_NUMBER = "setting_qr_code_id_number_s";
    public static final String KEY_SETTING_QR_CODE_MASK_PATTERN = "setting_qr_code_mask_pattern_l";
    public static final String KEY_SETTING_TODO_ITEMS_PREFIX = "setting_todo_items_";

    public final MMKV kv;

    private Storage() {
        kv = MMKV.mmkvWithID("MyID", MMKV.SINGLE_PROCESS_MODE, "$.+D9_^25/74!hD");
    }

    public static Storage getSingleton() {
        return Storage.Singleton.instance;
    }

    /**
     * 删除 key
     * @param key key
     */
    public void remove(String key) {
        kv.remove(key);
    }

    /**
     * 设置 string 类型的 key
     * @param key key
     * @param value value
     */
    public void setString(String key, String value) {
        kv.encode(key, value);
    }

    /**
     * 获取 string 类型的值
     * @param key key
     * @param defaultValue 默认值
     * @return value
     */
    public String getString(String key, String defaultValue) {
        return kv.decodeString(key, defaultValue);
    }

    /**
     * 设置string值，返回新值与旧值是否不同
     * @param key key
     * @param value value
     * @return 返回true表示新值与旧值不同
     */
    public boolean compareAndSetString(String key, String value) {
        String defaultValue = "";
        if (value.length() == 0) {
            defaultValue = "null";
        }
        String old = getString(key, defaultValue);
        if (old.compareTo(value) == 0) {
            return false;
        }
        setString(key, value);
        return true;
    }
    /**
     * 设置 long 类型的 key
     * @param key key
     * @param value value
     */
    public void setLong(String key, long value) {
        kv.encode(key, value);
    }

    /**
     * 获取 long 类型的值
     * @param key key
     * @param defaultValue 默认值
     * @return value
     */
    public long getLong(String key, long defaultValue) {
        return kv.decodeLong(key, defaultValue);
    }

    /**
     * 将值增加value的增量，返回新值
     * @param key key
     * @param add 增量
     * @return value
     */
    public long incrementLong(String key, long add) {
        long value = getLong(key, 0) + add;
        setLong(key, value);
        return value;
    }

    /**
     * 单例模式
     */
    private static class Singleton {
        static Storage instance = new Storage();

        private Singleton() {
        }
    }

//    public void debug() {
//        String[] allKeys = kv.allKeys();
//        if (allKeys == null) return;
//        Log.e("Storage.debug", String.valueOf(allKeys.length));
//        for (String key : allKeys) {
//            if (key.endsWith("s")) {
//                Log.e(key, getString(key, ""));
//            } else {
//                Log.e(key, String.valueOf(getLong(key, 0)));
//            }
//        }
//    }
}
