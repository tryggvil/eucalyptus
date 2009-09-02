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
	
	public PaymentInfo(){
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
	 * @return Returns the payerName.
	 */
	public String getPayerName() {
		return payerName;
	}

	/**
	 * @param payerName The payerName to set.
	 */
	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

	/**
	 * @return Returns the paymentMethod.
	 */
	public String getPaymentMethod() {
		return paymentMethod;
	}

	/**
	 * @param paymentMethod The paymentMethod to set.
	 */
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	/**
	 * @return Returns the paymentMethodNumber.
	 */
	public String getPaymentMethodNumber() {
		return paymentMethodNumber;
	}

	/**
	 * @param paymentMethodNumber The paymentMethodNumber to set.
	 */
	public void setPaymentMethodNumber(String paymentMethodNumber) {
		this.paymentMethodNumber = paymentMethodNumber;
	}

	/**
	 * @return Returns the paymentMethodExpires.
	 */
	public String getPaymentMethodExpires() {
		return paymentMethodExpires;
	}

	/**
	 * @param paymentMethodExpires The paymentMethodExpires to set.
	 */
	public void setPaymentMethodExpires(String paymentMethodExpires) {
		this.paymentMethodExpires = paymentMethodExpires;
	}

	/**
	 * @return Returns the paymentMethodCheckNumber.
	 */
	public String getPaymentMethodCheckNumber() {
		return paymentMethodCheckNumber;
	}

	/**
	 * @param paymentMethodCheckNumber The paymentMethodCheckNumber to set.
	 */
	public void setPaymentMethodCheckNumber(String paymentMethodCheckNumber) {
		this.paymentMethodCheckNumber = paymentMethodCheckNumber;
	}
	
}
