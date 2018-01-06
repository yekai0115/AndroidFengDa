package com.fengda.app.model;

public class User extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1655057247085968032L;

	public final static String BEAN_NAME="userBean";

	public Long userId;
	public String loginId;
	public String userName;
	public String email;
	public String password;
	public String sex; //男\女
	public String phoneNumber;
	public String mobile;
	public String description;
	public boolean loggedIn;
	public String token;
	public Integer type;
	public String photo;
    
	public String yzm;//验证码
	public Long yzmTime;//每次申请验证码的时间
	public Long yzmFirstTime;//当天第一次重置密码时间 (同一个用户当天申请发送短信的次数限制)
	public Integer yzmCount;//申请验证码次数

	public String getYzm() {
		return yzm;
	}
	public void setYzm(String yzm) {
		this.yzm = yzm;
	}
	public Long getYzmTime() {
		return yzmTime;
	}
	public void setYzmTime(Long yzmTime) {
		this.yzmTime = yzmTime;
	}
	public Long getYzmFirstTime() {
		return yzmFirstTime;
	}
	public void setYzmFirstTime(Long yzmFirstTime) {
		this.yzmFirstTime = yzmFirstTime;
	}
	public Integer getYzmCount() {
		return yzmCount;
	}
	public void setYzmCount(Integer yzmCount) {
		this.yzmCount = yzmCount;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	} 

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}


}
