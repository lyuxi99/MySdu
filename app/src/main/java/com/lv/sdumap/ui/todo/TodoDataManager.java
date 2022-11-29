package com.lv.sdumap.ui.todo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lv.sdumap.utils.Storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 事项数据持久化存储
 */
public class TodoDataManager {
    public static TodoDataManager getSingleton() {
        return TodoDataManager.Singleton.instance;
    }

    private List<List<String>> allItems;

    public TodoDataManager() {
    }

    private String getStorageKey(int state) {
        return Storage.KEY_SETTING_TODO_ITEMS_PREFIX + state + "_s";
    }

    /**
     * 加载某状态的数据
     *
     * @param state state(0 未完成，1 已完成)
     * @return 事项
     */
    private List<String> load(int state) {
        String defaultVal = state == 0 ? "[\"欢迎使用待办事项！\",\"这里可以方便地记作业和和其他事情哦！\",\"点击右下角的加号以创建新事项，点击一条现有事项以编辑。\",\"左滑删除，右滑标记完成。\",\"长按可以拖动排序。\",\"在“已完成”页面中可以查看已完成的事项。\"]" : "[]";
        String data = Storage.getSingleton().getString(getStorageKey(state), defaultVal);
        ArrayList<String> items = new Gson().fromJson(data, new TypeToken<ArrayList<String>>() {
        }.getType());
        if (items != null) return items;
        return new ArrayList<>();
    }

    /**
     * 保存某状态的数据
     *
     * @param state state(0 未完成，1 已完成)
     */
    private void save(int state) {
        Storage.getSingleton().setString(getStorageKey(state), new Gson().toJson(allItems.get(state)));
    }

    /**
     * 保存全部数据
     */
    public void saveAll() {
        save(0);
        save(1);
    }

    /**
     * 懒加载，负责全量数据只加载一遍
     */
    public void lazyLoad() {
        if (allItems == null) {
            allItems = new ArrayList<>();
            allItems.add(load(0));
            allItems.add(load(1));
        }
    }


    /**
     * 获取某状态事项数量
     */
    public int getLength(int state) {
        return allItems.get(state).size();
    }

    /**
     * 获取某状态某位置的事项
     */
    public String getItem(int state, int position) {
        return allItems.get(state).get(position);
    }

    /**
     * 向某状态增添新事项
     */
    public void addItem(int state, String item) {
        allItems.get(state).add(0, item);
        save(state);
    }

    /**
     * 修改某状态某位置的事项为给定的事项
     */
    public void modifyItem(int state, int position, String item) {
        allItems.get(state).set(position, item);
        save(state);
    }

    /**
     * 移动某状态某位置中的事项到某新位置
     */
    public void swapItem(int state, int fromPosition, int toPosition) {
        // 需要一步一步地交换
        int step = (toPosition - fromPosition) > 0 ? 1 : -1;
        for (int i = fromPosition; i != toPosition; i += step) {
            Collections.swap(allItems.get(state), i, i + step);
        }
        save(state);
    }

    /**
     * 删除某状态某位置的事项
     *
     * @param state
     * @param position
     */
    public void deleteItem(int state, int position) {
        allItems.get(state).remove(position);
        save(state);
    }

    /**
     * 切换某状态某位置的事项的状态
     *
     * @param oldState
     * @param oldPosition
     */
    public void changeState(int oldState, int oldPosition) {
        String item = getItem(oldState, oldPosition);
        deleteItem(oldState, oldPosition);
        addItem(1 - oldState, item);// 怎么通知另一个页面呢，用notifyDataSetChanged
//        saveAll(); // 不用了，因为deleteItem和addItem中已经save过了
    }

    /**
     * 单例模式
     */
    private static class Singleton {
        static TodoDataManager instance = new TodoDataManager();

        private Singleton() {
        }
    }

}
