package de.davidschuld.jsonpapiproxy;

public class Api {
	
	private String url;

	public Api(String url) {
		super();
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Api [url=" + url + "]";
	}

}
