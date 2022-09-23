package com.innovaccer.utils.v2.dataHelper.pageobject;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


@Generated("jsonschema2pojo")
public class How {
	@SerializedName("Strategy")
	@Expose
	private String strategy;
	@SerializedName("Value")
	@Expose
	private String value;
	@SerializedName("Description")
	@Expose
	private String description;
	@SerializedName("type")
	@Expose
	private String type;
	@SerializedName("key")
	@Expose
	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getStrategy() {
	return strategy;
	}

	public void setStrategy(String strategy) {
	this.strategy = strategy;
	}

	public String getValue() {
	return value;
	}

	public void setValue(String value) {
	this.value = value;
	}

	public String getDescription() {
	return description;
	}

	public void setDescription(String description) {
	this.description = description;
	}

	public String getType() {
	return type;
	}

	public void setType(String type) {
	this.type = type;
	}
}
