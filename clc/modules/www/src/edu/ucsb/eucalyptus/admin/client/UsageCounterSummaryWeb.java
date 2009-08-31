/**
 * Created at 4:45:47 PM Aug 27, 2009 by Tryggvi Larusson
 *
 * Copyright (C) 2009 Tryggvi Larusson All Rights Reserved.
 */
package edu.ucsb.eucalyptus.admin.client;

import java.util.List;

/**
 * <p>
 * TODO tryggvil Describe Type UsageCounterSummaryWeb
 * </p>
 * 
 * @author <a href="mailto:tryggvi.larusson[at]gmail.com">tryggvil</a>
 */
public class UsageCounterSummaryWeb {

	UserInfoWeb user;
	List<UsageCounterWeb> usageCounters;
	
	double amount=0;
	String currency;
	
	String comments;
	String usage;
	/**
	 * @param userInfoWeb
	 * @param countersForUser
	 */
	public UsageCounterSummaryWeb() {
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
	 * @return Returns the usageCounters.
	 */
	public List<UsageCounterWeb> getUsageCounters() {
		return usageCounters;
	}
	/**
	 * @param usageCounters The usageCounters to set.
	 */
	public void setUsageCounters(List<UsageCounterWeb> usageCounters) {
		this.usageCounters = usageCounters;
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
	/**
	 * @return Returns the currency.
	 */
	public String getCurrency() {
		return currency;
	}
	/**
	 * @param currency The currency to set.
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	/**
	 * @return Returns the comments.
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments The comments to set.
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	/**
	 * @return Returns the usage.
	 */
	public String getUsage() {
		return usage;
	}
	/**
	 * @param usage The usage to set.
	 */
	public void setUsage(String usage) {
		this.usage = usage;
	}
	
	public String getTotalAmount(){
		return getAmount()+" "+getCurrency();
	}
	
	public String getUserName(){
		if(this.user!=null){
			return user.getRealName();
		}
		return "";
	}
}
