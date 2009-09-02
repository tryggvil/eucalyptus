/**
 * Created at 6:27:11 PM Aug 27, 2009 by Tryggvi Larusson
 *
 * Copyright (C) 2009 Tryggvi Larusson All Rights Reserved.
 */
package edu.ucsb.eucalyptus.admin.client;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * <p>
 * TODO tryggvil Describe Type UsageTypeWeb
 * </p>
 * 
 * @author <a href="mailto:tryggvi.larusson[at]gmail.com">tryggvil</a>
 */
public class UsageTypeWeb {

	private Long id=-1l;
	private String serviceName;
	private String operationName;
	private String usageType;
	private String usageSubType;
	private String description;
	private double unitPrice;
	private String unitName;
	/**
	 * @return Returns the id.
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return Returns the serviceName.
	 */
	public String getServiceName() {
		return serviceName;
	}
	/**
	 * @param serviceName The serviceName to set.
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	/**
	 * @return Returns the usageSubType.
	 */
	public String getUsageSubType() {
		return usageSubType;
	}
	/**
	 * @param usageSubType The usageSubType to set.
	 */
	public void setUsageSubType(String usageSubType) {
		this.usageSubType = usageSubType;
	}
	private String currency;
	/**
	 * @return Returns the usageType.
	 */
	public String getUsageType() {
		return usageType;
	}
	/**
	 * @param usageType The usageType to set.
	 */
	public void setUsageType(String usageType) {
		this.usageType = usageType;
	}
	/**
	 * @return Returns the descrption.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param descrption The descrption to set.
	 */
	public void setDescription(String descrption) {
		this.description = descrption;
	}
	/**
	 * @return Returns the operationName.
	 */
	public String getOperationName() {
		return operationName;
	}
	/**
	 * @param operationName The operationName to set.
	 */
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	/**
	 * @return Returns the unitPrice.
	 */
	public double getUnitPrice() {
		return unitPrice;
	}
	/**
	 * @param unitPrice The unitPrice to set.
	 */
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
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
	 * @param unitName The unitName to set.
	 */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	/**
	 * @return Returns the unitName.
	 */
	public String getUnitName() {
		return unitName;
	}
}
