package pojo;

import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class PageData {

	@SerializedName("PageData")
	@Expose
	private Map<String,How> PageData;
	
	@SerializedName("PageData")
	@Expose
	private List<String> locatoryKeyNames;

	public List<String> getLocatoryKeyNames() {
		return locatoryKeyNames;
	}

	public void setLocatoryKeyNames(List<String> locatoryKeyNames) {
		this.locatoryKeyNames = locatoryKeyNames;
	}

	public Map<String, How> getPageData() {
		return PageData;
	}

	public void setPageData(Map<String, How> pageData) {
		PageData = pageData;
	}
	
	
}
