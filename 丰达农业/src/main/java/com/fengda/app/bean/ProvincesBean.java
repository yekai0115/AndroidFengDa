package com.fengda.app.bean;

import com.bigkoo.pickerview.model.IPickerViewData;

import java.util.List;

/**
 * TODO<省市区son数据源>
 *
 */

public class ProvincesBean implements IPickerViewData {


    /**
     * name : 省份
     * city : [{"name":"北京市","area":["东城区","西城区","崇文区","宣武区","朝阳区"]}]
     */

    private String name;
    private String code;
    private List<CityBean> sub;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityBean> getSub() {
        return sub;
    }

    public void setSub(List<CityBean> sub) {
        this.sub = sub;
    }

    public String getCode() {
        return code;
    }

    // 实现 IPickerViewData 接口，
    // 这个用来显示在PickerView上面的字符串，
    // PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。
    @Override
    public String getPickerViewText() {
        return this.name;
    }



    public static class CityBean {
        /**
         * name : 城市
         * area : ["东城区","西城区","崇文区","昌平区"]
         */

        private String name;
        private String code;
        private List<CityBean> sub;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<CityBean> getSub() {
            return sub;
        }

        public void setSub(List<CityBean> sub) {
            this.sub = sub;
        }

        public String getCode() {
            return code;
        }
    }
}
