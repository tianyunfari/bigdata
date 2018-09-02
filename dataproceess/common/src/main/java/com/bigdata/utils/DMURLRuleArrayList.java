package com.bigdata.utils;

import com.bigdata.pojo.DMURLRule;

import java.util.ArrayList;

public class DMURLRuleArrayList<E> {
    private ArrayList<E> arrayList;

    public DMURLRuleArrayList(ArrayList<E> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<E> getArrayList() {
        return arrayList;
    }

    public void add(E e) {
        this.arrayList.add(e);
    }

    public DMURLRule getDmURLRule(String url) {
        if (url == null || url == "") {
            return null;
        } else {
            for (int i = 0; i < this.arrayList.size(); i++) {
                DMURLRule dmurlRule = (DMURLRule)this.arrayList.get(i);
                if (url.contains(dmurlRule.getUrl().trim()))
                    return dmurlRule;
            }
        }
        return null;
    }
}
