package com.fengda.app.eventbus;

import com.fengda.app.bean.Campaign;

/**

 */
public class ToActivityMsgEvent {
	private Campaign campaign;

	public ToActivityMsgEvent() {
		super();
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public ToActivityMsgEvent(Campaign campaign) {
		this.campaign = campaign;
	}
}
