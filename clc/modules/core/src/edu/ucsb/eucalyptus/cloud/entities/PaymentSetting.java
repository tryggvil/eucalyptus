/**
 * Created at 11:49:54 PM Aug 27, 2009 by Tryggvi Larusson
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
 * TODO tryggvil Describe Type PaymentSettings
 * </p>
 * 
 * @author <a href="mailto:tryggvi.larusson[at]gmail.com">tryggvil</a>
 */
@Entity
@Table( name = "payment_settings" )
@Cache( usage = CacheConcurrencyStrategy.READ_WRITE )
public class PaymentSetting {

	public static final String KEY_DEFAULT_CURRENCY="defaultCurrency";
	
	@Id
	@Column( name = "setting_key" )
	private String settingKey;
	@Column( name = "setting_value" )
	private String settingValue;
	
	public PaymentSetting(String key){
		this.setSettingKey(key);
	}
	
	
	public static PaymentSetting named( String key ) throws EucalyptusCloudException {
	    EntityWrapper<PaymentSetting> db = new EntityWrapper<PaymentSetting>();
	    PaymentSetting setting = null;
	    try {
	    	setting = db.getUnique( new PaymentSetting(key) );
	    } catch( Exception e ) {
	    	PaymentSetting setting1 = new PaymentSetting(KEY_DEFAULT_CURRENCY);
	    	setting.setSettingKey("USD");
	    	db.add(setting1);
	    	if(key.equals(setting1.getSettingKey())){
	    		setting=setting1;
	    	}
	    }finally {
	      db.commit();
	    }
	    return setting;
}
	
	
	
	/**
	 * @return Returns the settingKey.
	 */
	public String getSettingKey() {
		return settingKey;
	}
	/**
	 * @param settingKey The settingKey to set.
	 */
	public void setSettingKey(String settingKey) {
		this.settingKey = settingKey;
	}
	/**
	 * @return Returns the settingValue.
	 */
	public String getSettingValue() {
		return settingValue;
	}
	/**
	 * @param settingValue The settingValue to set.
	 */
	public void setSettingValue(String settingValue) {
		this.settingValue = settingValue;
	}
	
	
}
