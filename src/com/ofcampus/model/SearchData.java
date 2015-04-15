package com.ofcampus.model;

import com.ofcampus.Util.SearchType;

public class SearchData {

	private String Id = "";
	private String data = "";
	private String datatype = "";
	private SearchType mSearchType;
	
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public SearchType getmSearchType() {
		return mSearchType;
	}
	public void setmSearchType(SearchType mSearchType) {
		this.mSearchType = mSearchType;
	}
	

}
