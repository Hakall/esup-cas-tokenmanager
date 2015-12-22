package org.esupportail.cas.addon.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class IPInformation {

	// @JsonProperty("ip")
	@JsonProperty("ipAddress")
	private String ip;

	// @JsonProperty("country_code")
	@JsonProperty("countryCode")
	private String countryCode;

	@JsonProperty("countryName")
	private String countryName;

	// @JsonProperty("region_code")
	// private String regionCode;

	@JsonProperty("regionName")
	private String regionName;

	// @JsonProperty("city")
	@JsonProperty("cityName")
	private String city;

	// @JsonProperty("zipcode")
	@JsonProperty("zipCode")
	private String zipCode;

	@JsonProperty("latitude")
	private String latitude;

	@JsonProperty("longitude")
	private String longitude;

	// @JsonProperty("metro_code")
	// private String metroCode;

	// @JsonProperty("area_code")
	// private String areaCode;

	@JsonProperty("statusCode")
	private String statusCode;

	@JsonProperty("statusMessage")
	private String statusMessage;

	@JsonProperty("timeZone")
	private String timeZone;
	
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getCountryCode() {
		return this.countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	// public String getRegionCode() {
	// 	return this.regionCode;
	// }

	// public void setRegionCode(String regionCode) {
	// 	this.regionCode = regionCode;
	// }

	public String getRegionName() {
		return this.regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipCode() {
		return this.zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	// public String getMetroCode() {
	// 	return this.metroCode;
	// }

	// public void setMetroCode(String metroCode) {
	// 	this.metroCode = metroCode;
	// }

	// public String getAreaCode() {
	// 	return this.areaCode;
	// }

	// public void setAreaCode(String areaCode) {
	// 	this.areaCode = areaCode;
	// }

	public String getStatusCode() {
		return this.statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getStatusMessage() {
		return this.statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public String getTimeZone() {
		return this.timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

}
