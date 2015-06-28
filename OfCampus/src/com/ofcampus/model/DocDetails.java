package com.ofcampus.model;

public class DocDetails {

	private int DocID = -1;
	private String DocURL = "";
	private String Docsize = "";
	private String DocName = "";

	public String getDocName() {
		return DocName;
	}

	public void setDocName(String docName) {
		DocName = docName;
	}

	public int getDocID() {
		return DocID;
	}

	public void setDocID(int docID) {
		DocID = docID;
	}

	public String getDocURL() {
		return DocURL;
	}

	public void setDocURL(String docURL) {
		DocURL = docURL;
	}

	public String getDocsize() {
		return Docsize;
	}

	public void setDocsize(String docsize) {
		Docsize = docsize;
	}

}
