/**
 * Created at 12:08:21 PM Aug 27, 2009 by Tryggvi Larusson
 *
 * Copyright (C) 2009 Tryggvi Larusson All Rights Reserved.
 */
package edu.ucsb.eucalyptus.cloud.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import edu.ucsb.eucalyptus.cloud.EucalyptusCloudException;

/**
 * <p>
 * TODO tryggvil Describe Type UsageType
 * </p>
 * 
 * @author <a href="mailto:tryggvi.larusson[at]gmail.com">tryggvil</a>
 */
@Entity
@Table( name = "usage_types" )
@Cache( usage = CacheConcurrencyStrategy.READ_WRITE )
public class UsageType {

	/*
	 * 	<OperationUsage>
			<ServiceName>AmazonEC2</ServiceName>
			<OperationName>AssociateAddress</OperationName>
			<UsageType>ElasticIP:IdleAddress</UsageType>
			<StartTime>08/01/09 00:00:00</StartTime>
			<EndTime>09/01/09 00:00:00</EndTime>
			<UsageValue>577</UsageValue>
		</OperationUsage>
		<OperationUsage>
			<ServiceName>AmazonEC2</ServiceName>
			<OperationName>RunInstances</OperationName>
			<UsageType>DataTransfer-Out-Bytes</UsageType>
			<StartTime>08/01/09 00:00:00</StartTime>
			<EndTime>09/01/09 00:00:00</EndTime>
			<UsageValue>42495869</UsageValue>
		</OperationUsage>
		<OperationUsage>
			<ServiceName>AmazonEC2</ServiceName>
			<OperationName>RunInstances</OperationName>
			<UsageType>BoxUsage</UsageType>
			<StartTime>08/01/09 00:00:00</StartTime>
			<EndTime>09/01/09 00:00:00</EndTime>
			<UsageValue>243</UsageValue>
		</OperationUsage>
	 */

	public static String DEFAULT_SERVICE_NAME="Eucalyptus";
	public static String OPERATION_RUNINSTANCES="RunInstances";
	public static String USAGE_TYPE_BOX_USAGE="BoxUsage";
	
	@Id
	@GeneratedValue
	@Column( name = "usage_type_id" )
	private Long id = -1l;
	
	/* e.g. Eucalyptus */
	@Column( name = "service_name" )
	private String serviceName=DEFAULT_SERVICE_NAME;
	
	/* e.g. RunInstances */
	@Column( name = "operation_name" )
	private String operationName;
	/* e.g. BoxUsage */
	@Column( name = "usage_type" )
	private String usageType;
	
	@Column( name = "usage_sub_type" )
	private String usageSubType;
	
	@Column( name = "description" )
	private String description;
	/* e.g. 0.1 */
	@Column( name = "unit_price" )
	private double unitPrice;
	/* e.g. Hrs */
	@Column( name = "unit_name" )
	private String unitName;

	private String currency;
	
	  /**
	 * @param name
	 */
	public UsageType() {
	}
	  /**
	 * @param name
	 */
	public UsageType(String operationName,String usageType) {
		this.operationName=operationName;
		this.usageType=usageType;
	}

	public static UsageType findBy( String operationName, String usageType, String usageSubType) throws EucalyptusCloudException {
		    EntityWrapper<UsageType> db = new EntityWrapper<UsageType>();
		    UsageType myType = null;
		    try {
		    	myType = db.getUnique( new UsageType(operationName,usageType) );
		    } catch( Exception e ) {
		    	if(operationName.equals(OPERATION_RUNINSTANCES) && usageType.equals(USAGE_TYPE_BOX_USAGE)){
		    	String[] defaultVMTypes = {VmType.M1_SMALL,VmType.M1_LARGE,VmType.M1_XLARGE,VmType.C1_MEDIUM,VmType.C1_XLARGE};
		    	for (int i = 0; i < defaultVMTypes.length; i++) {
		    		String vmType = defaultVMTypes[i];
			    	UsageType type = new UsageType(OPERATION_RUNINSTANCES,USAGE_TYPE_BOX_USAGE);
			    	//type.setUsageType(USAGE_TYPE_BOX_USAGE);
			    	type.setUsageSubType(vmType);
			    	//type.setOperationName(RUNINSTANCES_OPERATION);
			    	type.setDescription("Hourly server usage");
			    	type.setUnitName("Hrs");
			    	type.setUnitPrice(0.1);
			    	db.add( type );
			    	if(type.getOperationName().equals(operationName) && type.getUsageType().equals(usageType) && (type.getUsageSubType()==null || type.getUsageSubType().equals(usageSubType))){
			    		myType=type;
			    	}
		    	}
				}
		    }finally {
		      db.commit();
		    }
		    return myType;
	}

	/**
	 * @return Returns the uSAGE_TYPE_BOX_USAGE.
	 */
	public static String getUSAGE_TYPE_BOX_USAGE() {
		return USAGE_TYPE_BOX_USAGE;
	}

	/**
	 * @param usage_type_box_usage The uSAGE_TYPE_BOX_USAGE to set.
	 */
	public static void setUSAGE_TYPE_BOX_USAGE(String usage_type_box_usage) {
		USAGE_TYPE_BOX_USAGE = usage_type_box_usage;
	}

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
		if(this.currency==null){
			try {
				this.currency=PaymentSetting.named(PaymentSetting.KEY_DEFAULT_CURRENCY).getSettingValue();
			} catch (EucalyptusCloudException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.currency;
	}

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

	/**
	 * @return Returns the unitName.
	 */
	public String getUnitName() {
		return unitName;
	}

	/**
	 * @param unitName The unitName to set.
	 */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	
}
