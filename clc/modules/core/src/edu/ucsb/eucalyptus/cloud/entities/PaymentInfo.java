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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * <p>
 * TODO tryggvil Describe Type PaymentInfo
 * </p>
 * 
 * @author <a href="mailto:tryggvi.larusson[at]gmail.com">tryggvil</a>
 */
@Entity
@Table( name = "payment_info" )
@Cache( usage = CacheConcurrencyStrategy.READ_WRITE )
public class PaymentInfo {

	@Id
	@GeneratedValue
	@Column( name = "payment_info_id" )
	private Long id = -1l;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false, referencedColumnName="user_id")	
	private UserInfo user;
	
	@Column( name = "payer_name" )
	private String payerName;
	
	/*VISA/MASTER*/
	@Column( name = "payment_method" )
	private String paymentMethod;
	/*Cardnumber*/
	@Column( name = "payment_method_number" )
	private String paymentMethodNumber;
	/*ExpiryDate*/
	@Column( name = "payment_method_expires" )
	private String paymentMethodExpires;
	/*CCV Number*/
	@Column( name = "payment_method_checknumber" )
	private String paymentMethodCheckNumber;
	
}
