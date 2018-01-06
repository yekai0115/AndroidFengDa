package com.fengda.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Address implements Parcelable{

	/**收货ID*/
	private String addr_id;
	/**收货人姓名*/
	private String addr_name;
	/**收货人手机号*/
	private String addr_mobile;
	/**收货人省id*/
	private String addr_province;
	/**收货人市id*/
	private String addr_city;
	/**收货区id*/
	private String addr_county;
	/**收货人省*/
	private String  addr_province_name;
	private String  addr_city_name;
	private String  addr_county_name;
	/**收货详细地址*/
	private String addr_detail;
	/**收货ID*/
	private int  addr_primary;




	/**是否被选中*/
	private Boolean isSelect=false;
	/**选项框是否可见*/
	private Boolean isVisibility=false;

	/**
	 * 传递地址对象
	 * @param addr_id
	 * @param addr_name
	 * @param addr_mobile
	 * @param addr_province
	 * @param addr_city
	 * @param addr_county
	 * @param addr_detail
	 * @param addr_primary
	 */
	public Address(String addr_id, String addr_name, String addr_mobile, String addr_province, String addr_city, String addr_county, String addr_detail, int addr_primary,
				   String  addr_province_name,String  addr_city_name,String  addr_county_name) {
		this.addr_id = addr_id;
		this.addr_name = addr_name;
		this.addr_mobile = addr_mobile;
		this.addr_province = addr_province;
		this.addr_city = addr_city;
		this.addr_county = addr_county;
		this.addr_detail = addr_detail;
		this.addr_primary = addr_primary;
		this.addr_province_name = addr_province_name;
		this.addr_city_name = addr_city_name;
		this.addr_county_name = addr_county_name;

	}

	public String getAddr_id() {
		return addr_id;
	}

	public void setAddr_id(String addr_id) {
		this.addr_id = addr_id;
	}

	public String getAddr_name() {
		return addr_name;
	}

	public void setAddr_name(String addr_name) {
		this.addr_name = addr_name;
	}

	public String getAddr_mobile() {
		return addr_mobile;
	}

	public void setAddr_mobile(String addr_mobile) {
		this.addr_mobile = addr_mobile;
	}

	public String getAddr_province() {
		return addr_province;
	}

	public void setAddr_province(String addr_province) {
		this.addr_province = addr_province;
	}

	public String getAddr_city() {
		return addr_city;
	}

	public void setAddr_city(String addr_city) {
		this.addr_city = addr_city;
	}

	public String getAddr_county() {
		return addr_county;
	}

	public void setAddr_county(String addr_county) {
		this.addr_county = addr_county;
	}

	public String getAddr_detail() {
		return addr_detail;
	}

	public void setAddr_detail(String addr_detail) {
		this.addr_detail = addr_detail;
	}

	public int getAddr_primary() {
		return addr_primary;
	}

	public void setAddr_primary(int addr_primary) {
		this.addr_primary = addr_primary;
	}

	public String getAddr_province_name() {
		return addr_province_name;
	}

	public void setAddr_province_name(String addr_province_name) {
		this.addr_province_name = addr_province_name;
	}

	public String getAddr_city_name() {
		return addr_city_name;
	}

	public void setAddr_city_name(String addr_city_name) {
		this.addr_city_name = addr_city_name;
	}

	public String getAddr_county_name() {
		return addr_county_name;
	}

	public void setAddr_county_name(String addr_county_name) {
		this.addr_county_name = addr_county_name;
	}

	@Override
	public int describeContents() {
		
		return 0;
	}


	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(addr_id);
		parcel.writeString(addr_name);
		parcel.writeString(addr_mobile);
		parcel.writeString(addr_province);
		parcel.writeString(addr_city);
		parcel.writeString(addr_county);
		parcel.writeString(addr_detail);
		parcel.writeInt(addr_primary);
		parcel.writeString(addr_province_name);
		parcel.writeString(addr_city_name);
		parcel.writeString(addr_county_name);
	}
	
	public static final Creator<Address> CREATOR = new Creator<Address>(){

		@Override
		public Address createFromParcel(Parcel source) {
			
			return new Address(source.readString(),
					source.readString(),
					source.readString(),
					source.readString(),
					source.readString(),
					source.readString(),
					source.readString(),
					source.readInt(),
					source.readString(),
					source.readString(),
			        source.readString());
		}

		@Override
		public Address[] newArray(int size) {
			return new Address[size];
		}
	};	
}
