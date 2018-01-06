package com.fengda.app.eventbus;

import com.fengda.app.bean.Specifications;

/**
 */
public class MsgEvent4 {
	private String id;
	private String number;
	private int position;
	private int type;
	public MsgEvent4() {
		super();
	}

private Specifications specifications;




	public MsgEvent4(String id, Specifications specifications, String number, int position, int type) {
		super();
		this.id = id;
		this.specifications = specifications;
		this.number = number;
		this.position = position;
		this.type = type;
	}


	public String getId() {
		return id;
	}

	public String getNumber() {
		return number;
	}

	public int getPosition() {
		return position;
	}

	public int getType() {
		return type;
	}



	public Specifications getSpecifications() {
		return specifications;
	}
}
