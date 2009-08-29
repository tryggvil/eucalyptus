/**
 * Created at 12:08:21 PM Aug 27, 2009 by Tryggvi Larusson
 *
 * Copyright (C) 2009 Tryggvi Larusson All Rights Reserved.
 */
package edu.ucsb.eucalyptus.cloud.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import edu.ucsb.eucalyptus.cloud.EucalyptusCloudException;

/**
 * <p>
 * TODO tryggvil Describe Type UsageCounter
 * </p>
 * 
 * @author <a href="mailto:tryggvi.larusson[at]gmail.com">tryggvil</a>
 */
@Entity
@Table( name = "usage_counter" )
@Cache( usage = CacheConcurrencyStrategy.READ_WRITE )
public class UsageCounter {

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
	
	@Id
	@GeneratedValue
	@Column( name = "usage_counter_id" )
	private Long id = -1l;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false, referencedColumnName="user_id")	
	private UserInfo user;
	
	//@Column( name = "user_id" )
	//private Long userId = -1l;
	
	@ManyToOne
	@JoinColumn(name = "usage_type_id", nullable = false, referencedColumnName="usage_type_id")	
	UsageType usageType;
	
	//@Column( name = "usage_type" )
	//private String usageType;
	@Column( name = "usage_instance_key" )
	/* e.g. i-32D706FB or vol-32A604A6 */
	private String usageInstanceKey;
	
    @Column( name = "start_time" )
    private Date startTime;
    @Column( name = "end_time" )
    private Date endTime;
	
	@Column( name = "amount" )
	private double amount;

	
	public static UsageCounter findBy( UserInfo user, String instanceId) throws EucalyptusCloudException {
		EntityWrapper<UsageCounter> db = new EntityWrapper<UsageCounter>();
		UsageCounter myType = null;	    	UsageCounter counter = new UsageCounter();
	    	
	   counter.setUser(user);
	   counter.setUsageInstanceKey(instanceId);
	   myType = db.getUnique( counter );
	   return myType;
	}
	
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
	 * @return Returns the usageType.
	 */
	public UsageType getUsageType() {
		return usageType;
	}

	/**
	 * @param usageType The usageType to set.
	 */
	public void setUsageType(UsageType usageType) {
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
