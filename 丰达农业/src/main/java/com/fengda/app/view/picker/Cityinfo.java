package com.fengda.app.view.picker;

import java.io.Serializable;

public class Cityinfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2852621825459542514L;
	private String region_id;
	private String region_name;
	private String parent_id;

	public String getRegion_id() {
		return region_id;
	}

	public void setRegion_id(String region_id) {
		this.region_id = region_id;
	}

	public String getRegion_name() {
		return region_name;
	}

	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}
}
