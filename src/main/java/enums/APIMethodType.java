package enums;

public class APIMethodType {

public enum APIMethodsType {
		GET("get"),
		POST("post"),
		DELETE("delete"),
		PUT("put"),
	    PATCH("patch");
		private String value;
		APIMethodsType(String val){
			this.value = val;
		}
		public String getValue(){
			return value;
		}

	}
}
