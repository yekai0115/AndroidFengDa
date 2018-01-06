package com.fengda.app.model;

import com.fengda.app.bean.AreaBean;
import com.fengda.app.bean.CityBean;
import com.fengda.app.bean.ProvinceBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by StormShadow on 2017/4/2.
 * Knowledge is power.
 * 省市县列表
 */

public class Addrs {

    // Province List ---------------------------------------
    private static List<ProvinceBean> provinceObjects = new ArrayList<>();
    private static List<String> provinceNameList = new ArrayList<>();
    public static void setProvinceObjects(List<ProvinceBean> pList) {
        provinceObjects.clear();
        provinceNameList.clear();
        provinceObjects.addAll(pList);
        for (ProvinceBean addrObject : provinceObjects) {
            provinceNameList.add(addrObject.getName());
        }
    }

    public static List<ProvinceBean> getProvinceObjects() {
        return provinceObjects;
    }

    public static List<String> getProvinceNameList() {
        return provinceNameList;
    }

    // CityList ---------------------------------------
    private static List<CityBean> cityObjects = new ArrayList<>();
    private static List<String> cityNameList = new ArrayList<>();

    public static void setCityObjects(List<CityBean> pList) {
        cityObjects.clear();
        cityNameList.clear();
        cityObjects.addAll(pList);
        for (CityBean addrObject : cityObjects) {
            cityNameList.add(addrObject.getName());
        }
    }

    public static List<CityBean> getCityObjects() {
        return cityObjects;
    }

    public static List<String> getCityNameList() {
        return cityNameList;
    }

    // AreaList ---------------------------------------
    private static List<AreaBean> areaObjects = new ArrayList<>();
    private static List<String> areaNameList = new ArrayList<>();

    public static void setAreaObjects(List<AreaBean> pList) {
        areaObjects.clear();
        areaNameList.clear();
        areaObjects.addAll(pList);
        for (AreaBean addrObject : areaObjects) {
            areaNameList.add(addrObject.getName());
        }
    }

    public static List<AreaBean> getAreaObjects() {
        return areaObjects;
    }

    public static List<String> getAreaNameList() {
        return areaNameList;
    }

}
