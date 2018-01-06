package com.fengda.app.view.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.fengda.app.R;
import com.fengda.app.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 城市Picker
 * 
 * @author zd
 * 
 */
public class CityPicker extends LinearLayout {
	/** 滑动控件 */
	public static ScrollerNumberPicker provincePicker;
	private ScrollerNumberPicker cityPicker;
	private ScrollerNumberPicker counyPicker;
	/** 选择监听 */
	private OnSelectingListener onSelectingListener;
	/** 刷新界面 */
	private static final int REFRESH_VIEW = 0x001;
	/** 临时日期 */
	private int tempProvinceIndex = -1;
	private int temCityIndex = -1;
	private int tempCounyIndex = -1;
	private Context context;
	private List<Cityinfo> province_list = new ArrayList<Cityinfo>();
	private HashMap<String, List<Cityinfo>> city_map = new HashMap<String, List<Cityinfo>>();
	private HashMap<String, List<Cityinfo>> couny_map = new HashMap<String, List<Cityinfo>>();
	private static ArrayList<String> province_list_code = new ArrayList<String>();
	private static ArrayList<String> city_list_code = new ArrayList<String>();
	private static ArrayList<String> couny_list_code = new ArrayList<String>();

	private CitycodeUtil citycodeUtil;
	private String proId;//省份id
	private String cityId;//城市id
	private String areaId;//区id
	private String city_string;
	public String province;//省
	public String city;//市
	public String area;//区
	
	
	
	public CityPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		getaddressinfo();
		
	}

	public CityPicker(Context context) {
		super(context);
		this.context = context;
		getaddressinfo();
	}

	/**
	 * 获取城市信息
	 */
	private void getaddressinfo() {
		// 读取城市信息string
		try {
			JSONParser parser = new JSONParser();
			String area_str = FileUtils.readAssets(context, "area.json");
			province_list = parser.getJSONParserResult(area_str, "area0");
			// citycodeUtil.setProvince_list_code(parser.province_list_code);
			city_map = parser.getJSONParserResultArray(area_str, "area1");
			// System.out.println("city_mapsize" +
			// parser.city_list_code.toString());
			// citycodeUtil.setCity_list_code(parser.city_list_code);
			couny_map = parser.getJSONParserResultArray(area_str, "area2");
			// citycodeUtil.setCouny_list_code(parser.city_list_code);
			// System.out.println("couny_mapsize" +
			// parser.city_list_code.toString());
		} catch (Exception e) {

		}
		
	}

	public static class JSONParser {
		public ArrayList<String> province_list_code = new ArrayList<String>();
		public ArrayList<String> city_list_code = new ArrayList<String>();

		public List<Cityinfo> getJSONParserResult(String JSONString, String key) {
			List<Cityinfo> list = new ArrayList<Cityinfo>();
			JsonObject result = new JsonParser().parse(JSONString).getAsJsonObject().getAsJsonObject(key);

			Iterator iterator = result.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, JsonElement> entry = (Entry<String, JsonElement>) iterator.next();
				Cityinfo cityinfo = new Cityinfo();

				cityinfo.setRegion_name(entry.getValue().getAsString());
				cityinfo.setRegion_id(entry.getKey());
				province_list_code.add(entry.getKey());
				list.add(cityinfo);
			}
			return list;
		}

		public HashMap<String, List<Cityinfo>> getJSONParserResultArray(
				String JSONString, String key) {
			HashMap<String, List<Cityinfo>> hashMap = new HashMap<String, List<Cityinfo>>();
			JsonObject result = new JsonParser().parse(JSONString).getAsJsonObject().getAsJsonObject(key);

			Iterator iterator = result.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, JsonElement> entry = (Entry<String, JsonElement>) iterator.next();
				List<Cityinfo> list = new ArrayList<Cityinfo>();
				JsonArray array = entry.getValue().getAsJsonArray();
				for (int i = 0; i < array.size(); i++) {
					Cityinfo cityinfo = new Cityinfo();
					cityinfo.setRegion_name(array.get(i).getAsJsonArray().get(0).getAsString());
					cityinfo.setRegion_id(array.get(i).getAsJsonArray().get(1).getAsString());
					city_list_code.add(array.get(i).getAsJsonArray().get(1).getAsString());
					list.add(cityinfo);
				}
				hashMap.put(entry.getKey(), list);
			}
			return hashMap;
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		LayoutInflater.from(getContext()).inflate(R.layout.city_picker, this);
		citycodeUtil = CitycodeUtil.getSingleton();
		// 获取控件引用
		try {
			provincePicker = (ScrollerNumberPicker) findViewById(R.id.province);
			cityPicker = (ScrollerNumberPicker) findViewById(R.id.city);
			counyPicker = (ScrollerNumberPicker) findViewById(R.id.couny);
			//设置省数据
			provincePicker.setData(citycodeUtil.getProvince(province_list));
			//设置默认展示的省
			provincePicker.setDefault(0);
			//设置市数据
			cityPicker.setData(citycodeUtil.getCity(city_map, citycodeUtil.getProvince_list_code().get(0)));
			//设置默认展示的市
			cityPicker.setDefault(0);
			//设置区数据
			counyPicker.setData(citycodeUtil.getCouny(couny_map, citycodeUtil.getCity_list_code().get(0)));
			//设置默认展示的区
			counyPicker.setDefault(0);
		
			//如果没有滑动，取出默认展示的省市区id
			proId=citycodeUtil.getProvince_list_code().get(provincePicker.getSelected());//110000
			cityId = citycodeUtil.getCity_list_code().get(cityPicker.getSelected());//110200
			areaId = citycodeUtil.getCouny_list_code().get(counyPicker.getSelected());//110228
			
		provincePicker.setOnSelectListener(new ScrollerNumberPicker.OnSelectListener() {

			@Override
			public void endSelect(int id, String text) {
				try {
//					System.out.println("id-->" + id + "text----->" + text);
					if (text.equals("") || text == null)
						return;
					if (tempProvinceIndex != id) {
//						System.out.println("endselect");
						String selectDay = cityPicker.getSelectedText();
						if (selectDay == null || selectDay.equals(""))
							return;
						String selectMonth = counyPicker.getSelectedText();
						if (selectMonth == null || selectMonth.equals(""))
							return;

//						// 切换省时获取城市数组
						cityPicker.setData(citycodeUtil.getCity(city_map,citycodeUtil.getProvince_list_code().get(id)));
						int citySize=cityPicker.getListSize();
						if(citySize!=0){
							cityPicker.setDefault(0);
							counyPicker.setData(citycodeUtil.getCouny(couny_map,citycodeUtil.getCity_list_code().get(0)));
							int countySize=counyPicker.getListSize();
							if(countySize!=0){
								counyPicker.setDefault(0);
							}
							proId = citycodeUtil.getProvince_list_code().get(id);
							cityId = citycodeUtil.getCity_list_code().get(cityPicker.getSelected());
							areaId = citycodeUtil.getCouny_list_code().get(counyPicker.getSelected());
						}
						int lastDay = Integer.valueOf(provincePicker.getListSize());
						if (id > lastDay) {
							provincePicker.setDefault(lastDay - 1);
						}
					}
					tempProvinceIndex = id;
					Message message = new Message();
					message.what = REFRESH_VIEW;
					handler.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}

			@Override
			public void selecting(int id, String text) {
				
			}
		});
		cityPicker.setOnSelectListener(new ScrollerNumberPicker.OnSelectListener() {

			@Override
			public void endSelect(int id, String text) {
				try {
					if (text.equals("") || text == null)
						return;
					if (temCityIndex != id) {
						String selectDay = provincePicker.getSelectedText();
						if (selectDay == null || selectDay.equals(""))
							return;
						String selectMonth = counyPicker.getSelectedText();
						if (selectMonth == null || selectMonth.equals(""))
							return;
						counyPicker.setData(citycodeUtil.getCouny(couny_map,citycodeUtil.getCity_list_code().get(id)));
						counyPicker.setDefault(0);
						// 切换市时获取区数组
						cityId = citycodeUtil.getCity_list_code().get(id);
						proId = citycodeUtil.getProvince_list_code().get(provincePicker.getSelected());
						areaId = citycodeUtil.getCouny_list_code().get(counyPicker.getSelected());
						
						int lastDay = Integer.valueOf(cityPicker.getListSize());
						if (id > lastDay) {
							cityPicker.setDefault(lastDay - 1);
						}
					}
					temCityIndex = id;
					Message message = new Message();
					message.what = REFRESH_VIEW;
					handler.sendMessage(message);
				} catch (Exception e) {
					
				}
				
			}

			@Override
			public void selecting(int id, String text) {
				

			}
		});
		counyPicker.setOnSelectListener(new ScrollerNumberPicker.OnSelectListener() {

			@Override
			public void endSelect(int id, String text) {
				
				try {
					if (text.equals("") || text == null)
						return;
					if (tempCounyIndex != id) {
						String selectDay = provincePicker.getSelectedText();
						if (selectDay == null || selectDay.equals(""))
							return;
						String selectMonth = cityPicker.getSelectedText();
						if (selectMonth == null || selectMonth.equals(""))
							return;
						// 城市数组
						areaId = citycodeUtil.getCouny_list_code().get(id);
						proId = citycodeUtil.getProvince_list_code().get(provincePicker.getSelected());
						cityId = citycodeUtil.getCity_list_code().get(cityPicker.getSelected());
						int lastDay = Integer.valueOf(counyPicker.getListSize());
						if (id > lastDay) {
							counyPicker.setDefault(lastDay - 1);
						}
					}
					tempCounyIndex = id;
					Message message = new Message();
					message.what = REFRESH_VIEW;
					handler.sendMessage(message);
				} catch (Exception e) {
	
				}
			
			}

			@Override
			public void selecting(int id, String text) {
				

			}
		});} catch (Exception e) {
			
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH_VIEW:
				if (onSelectingListener != null)
					onSelectingListener.selected(true);
				break;
			default:
				break;
			}
		}

	};

	public void setOnSelectingListener(OnSelectingListener onSelectingListener) {
		this.onSelectingListener = onSelectingListener;
	}

	
	/**
	 * 返回省市区
	 * @return
	 */
	public String getCity_string() {
		city_string = provincePicker.getSelectedText()+ cityPicker.getSelectedText() + counyPicker.getSelectedText();
		return city_string;
	}

	
	/**
	 * 返回省
	 * @return
	 */
	public String getProvince() {
		province = provincePicker.getSelectedText();
		return province;
	}
	
	/**
	 * 返回市
	 * @return
	 */
	public String getCtiy() {
		city =cityPicker.getSelectedText();
		return city;
	}
	
	/**
	 * 返回区
	 * @return
	 */
	public String getArea() {
		area =counyPicker.getSelectedText();
		return area;
	}
	
	
	
	
	
	/**
	 * 返回省id
	 * @return
	 */
	public String getProId() {
		return proId;
	}
	
	/**
	 * 返回市id
	 * @return
	 */
	public String getCityId() {
		return cityId;
	}
	/**
	 * @return
	 */
	public String getAreaId() {
		return areaId;
	}

	
	public interface OnSelectingListener {

		public void selected(boolean selected);
	}
}
