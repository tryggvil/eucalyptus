/**
 * Created at 4:47:37 PM Aug 27, 2009 by Tryggvi Larusson
 *
 * Copyright (C) 2009 Tryggvi Larusson All Rights Reserved.
 */
package edu.ucsb.eucalyptus.admin.client;

import java.util.Date;

import edu.ucsb.eucalyptus.cloud.entities.UsageType;
import edu.ucsb.eucalyptus.cloud.entities.UserInfo;

/**
 * <p>
 * TODO tryggvil Describe Type UsageCounterWeb
 * </p>
 * 
 * @author <a href="mailto:tryggvi.larusson[at]gmail.com">tryggvil</a>
 */
public class UsageCounterWeb {

	private Long id = -1l;
	private UserInfoWeb user;
	UsageTypeWeb usageType;
	private String usageInstanceKey;

    private Date startTime;
    private Date endTime;
	
	private double amount;

	/**
	 * @return Returns the id.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return Returns the user.
	 */
	public UserInfoWeb getUser() {
		return user;
	}

	/**
	 * @param user The user to set.
	 */
	public void setUser(UserInfoWeb user) {
		this.user = user;
	}

	/**
	 * @return Returns the usageType.
	 */
	public UsageTypeWeb getUsageType() {
		return usageType;
	}

	/**
	 * @param usageType The usageType to set.
	 */
	public void setUsageType(UsageTypeWeb usageType) {
		this.usageType = usageType;
	}

	/**
	 * @return Returns the usageInstanceKey.
	 */
	public String getUsageInstanceKey() {
		return usageInstanceKey;
	}

	/**
	 * @param usageInstanceKey The usageInstanceKey to set.
	 */
	public void setUsageInstanceKey(String usageInstanceKey) {
		this.usageInstanceKey = usageInstanceKey;
	}

	/**
	 * @return Returns the startTime.
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime The startTime to set.
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return Returns the endTime.
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime The endTime to set.
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return Returns the amount.
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @param amount The amount to set.
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}
}
