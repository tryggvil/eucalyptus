/**
 * Created at 4:45:47 PM Aug 27, 2009 by Tryggvi Larusson
 *
 * Copyright (C) 2009 Tryggvi Larusson All Rights Reserved.
 */
package edu.ucsb.eucalyptus.cloud.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import edu.ucsb.eucalyptus.util.UsageManagement;

/**
 * <p>
 * TODO tryggvil Describe Type UsageCounterSummaryWeb
 * </p>
 * 
 * @author <a href="mailto:tryggvi.larusson[at]gmail.com">tryggvil</a>
 */
public class UsageCounterSummary {

	UserInfo user;
	List<UsageCounter> usageCounters;
	
	double amount=0;
	String currency;
	
	String comments;
	String usage;
	/**
	 * @param userInfoWeb
	 * @param countersForUser
	 */
	public UsageCounterSummary(UserInfo userInfoWeb,
			List<UsageCounter> countersForUser) {
		this.user=userInfoWeb;
		setUsageCounters(countersForUser);
	}
	/**
	 * @return Returns the user.
	 */
	public UserInfo getUser() {
		return user;
	}
	/**
	 * @param user The user to set.
	 */
	public void setUser(UserInfo user) {
		this.user = user;
	}
	/**
	 * @return Returns the usageCounters.
	 */
	public List<UsageCounter> getUsageCounters() {
		return usageCounters;
	}
	/**
	 * @param usageCounters The usageCounters to set.
	 */
	public void setUsageCounters(List<UsageCounter> usageCounters) {
		this.usageCounters = usageCounters;
		calculateTotalSummary(usageCounters);
	}
	/**
	 * <p>
	 * Calculate the total summary of usage
	 * </p>
	 * @param usageCounters2
	 */
	protected void calculateTotalSummary(List<UsageCounter> usageCounters2) {
		this.amount=0;
		List<String> usageTypes = getUniqueUsageTypes(usageCounters2);
		for (String usageType : usageTypes) {
			List<UsageCounter> usagesForType = getUsagesForType(usageType,usageCounters2);
			double totalAmountForType=0;
			double totalUnitsConsumedForType=0;
			String unitName="";
			for (UsageCounter usageCounterWeb : usagesForType) {
				UsageType uUsageType = usageCounterWeb.getUsageType();
				double unitsConsumedForCounter=usageCounterWeb.getAmount();
				if(unitsConsumedForCounter==0){
					Date dateNow = new Date();
					Date startDate = usageCounterWeb.getStartTime();
					unitsConsumedForCounter=UsageManagement.countHoursBetween(startDate, dateNow);
				}
				double amountForCounter = unitsConsumedForCounter*uUsageType.getUnitPrice();
				totalAmountForType+=amountForCounter;
				totalUnitsConsumedForType+=unitsConsumedForCounter;
				unitName = uUsageType.getUnitName();
			}
			if(totalUnitsConsumedForType>0){
				String descriptionString = totalUnitsConsumedForType+" "+unitName;
				addToComments(descriptionString);
			}
			this.amount+=totalAmountForType;
		}
	}
	/**
	 * <p>
	 * TODO tryggvil describe method addToComments
	 * </p>
	 * @param descriptionString
	 */
	private void addToComments(String descriptionString) {
		if(this.comments==null){
			comments=descriptionString;
		}
		else{
			comments=comments+", "+descriptionString;
		}
	}
	/**
	 * <p>
	 * TODO tryggvil describe method getUsagesForType
	 * </p>
	 * @param usageType
	 * @param usageCounters2
	 * @return
	 */
	private List<UsageCounter> getUsagesForType(String usageType,
			List<UsageCounter> usageCounters2) {
		List<UsageCounter> theReturn = new ArrayList<UsageCounter>();
		for (UsageCounter usageCounterWeb : usageCounters2) {
			if(usageCounterWeb.getUsageType().equals(usageType)){
				theReturn.add(usageCounterWeb);
			}
		}
		return theReturn;
	}
	/**
	 * <p>
	 * TODO tryggvil describe method getUniqueUsageTypes
	 * </p>
	 * @param usageCounters2
	 * @return
	 */
	private List<String> getUniqueUsageTypes(
			List<UsageCounter> usageCounters2) {
		ArrayList<String> theReturn = new ArrayList<String>();
		for (Iterator iterator = usageCounters2.iterator(); iterator.hasNext();) {
			UsageCounter usageCounterWeb = (UsageCounter) iterator.next();
			String usageType = usageCounterWeb.getUsageType().getUsageType();
			if(!theReturn.contains(usageType)){
				theReturn.add(usageType);
			}
		}
		return theReturn;
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
