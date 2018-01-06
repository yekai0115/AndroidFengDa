package com.fengda.app.model;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * @author robin
 * 
 */
public class BaseModel implements Serializable {

	public static final String ALL = "A";
	public static final String DELETE = "D";
	public static final String INSERT = "I";
	public static final String UPDATE = "U";
	public static final String VIEW = "V";

	private static final long serialVersionUID = -7629925131577243428L;

	private Date lastUpdTime;
	private Long lastUpdBy;
	private String lastUpdByName;
	private Date createTime;
	private Long createBy;
	private String createByName;
	
	//
	private Integer offset;
	private Integer rows;
	
	public String getLastUpdByName() {
		return lastUpdByName;
	}

	public void setLastUpdByName(String lastUpdByName) {
		this.lastUpdByName = lastUpdByName;
	}

	public String getCreateByName() {
		return createByName;
	}

	public void setCreateByName(String createByName) {
		this.createByName = createByName;
	}

	public Long getLastUpdBy() {
		return lastUpdBy;
	}

	public Date getLastUpdTime() {
		return lastUpdTime;
	}

	public void setLastUpdTime(Date lastUpdTime) {
		this.lastUpdTime = lastUpdTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setLastUpdBy(Long lastUpdBy) {
		this.lastUpdBy = lastUpdBy;
	}

	public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}
}
