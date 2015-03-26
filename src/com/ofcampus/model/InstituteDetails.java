/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.model;


public class InstituteDetails {
	private String id="";
	private String nm="";
	private String emsuffix="";
	private String thpartyauth="";
	private String provider="";
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNm() {
		return nm;
	}
	public void setNm(String nm) {
		this.nm = nm;
	}
	public String getEmsuffix() {
		return emsuffix;
	}
	public void setEmsuffix(String emsuffix) {
		this.emsuffix = emsuffix;
	}
	public String getThpartyauth() {
		return thpartyauth;
	}
	public void setThpartyauth(String thpartyauth) {
		this.thpartyauth = thpartyauth;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
}
