package com.file.api.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class APIProperties {

	String requetURL;
	String filesURI;
	String listFilesURI;

	String userSecret;
	String organizationSecret;
	String elementSecret;

	private String getRequetURL() {
		return requetURL;
	}

	public void setRequetURL(String requetURL) {
		this.requetURL = requetURL;
	}

	private String getFilesURI() {
		return filesURI;
	}

	public void setFilesURI(String filesURI) {
		this.filesURI = filesURI;
	}

	private String getListFilesURI() {
		return listFilesURI;
	}

	public void setListFilesURI(String listFilesURI) {
		this.listFilesURI = listFilesURI;
	}

	private String getUserSecret() {
		return userSecret;
	}

	public void setUserSecret(String userSecret) {
		this.userSecret = userSecret;
	}

	private String getOrganizationSecret() {
		return organizationSecret;
	}

	public void setOrganizationSecret(String organizationSecret) {
		this.organizationSecret = organizationSecret;
	}

	private String getElementSecret() {
		return elementSecret;
	}

	public void setElementSecret(String elementSecret) {
		this.elementSecret = elementSecret;
	}

	public String getAuthentication() {
		return "User " + getUserSecret() + ", Organization " + getOrganizationSecret() + ", Element "
				+ getElementSecret();
	}

	public String getFilesURL() {
		return getRequetURL() + getFilesURI();
	}

	public String getListFilesURL() {
		return getRequetURL() + getListFilesURI();
	}
}
