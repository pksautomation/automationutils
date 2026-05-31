package com.pksautomation.utils.report;

public class ResultStore {

	private String status = null;
	private long duration_sec = 0;
	private String started_at = null;
	private String finished_at = null;
	private String feature_name = null;
	private String scenario_name = null;
	private String environment = null;
	private String productType = null;
	private String ticketId = null;
	private String suite_start_pointer = null;
	private String testType = null;
	private String product = null;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public long getDuration_sec() {
		return duration_sec;
	}
	public void setDuration_sec(long duration_sec) {
		this.duration_sec = duration_sec;
	}
	public String getStarted_at() {
		return started_at;
	}
	public void setStarted_at(String started_at) {
		this.started_at = started_at;
	}
	public String getFinished_at() {
		return finished_at;
	}
	public void setFinished_at(String finished_at) {
		this.finished_at = finished_at;
	}
	public String getFeature_name() {
		return feature_name;
	}
	public void setFeature_name(String feature_name) {
		this.feature_name = feature_name;
	}
	public String getScenario_name() {
		return scenario_name;
	}
	public void setScenario_name(String scenario_name) {
		this.scenario_name = scenario_name;
	}
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getTicketId() {
		return ticketId;
	}
	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}
	public String getsuite_start_pointer() {
		return suite_start_pointer;
	}
	public void setsuite_start_pointer(String suite_start_pointer) {
		this.suite_start_pointer = suite_start_pointer;
	}
	public String getTestType() {
		return testType;
	}
	public void setTestType(String testType) {
		this.testType = testType;
	}
}
