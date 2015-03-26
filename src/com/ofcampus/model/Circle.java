/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.model;

public class Circle {
	private String Circleid = "";
	private String Circlename = "";
	private String Circleselected = "";
	public int isTick = 0;

	public String getCircleid() {
		return Circleid;
	}

	public void setCircleid(String circleid) {
		Circleid = circleid;
	}

	public String getCirclename() {
		return Circlename;
	}

	public void setCirclename(String circlename) {
		Circlename = circlename;
	}

	public String getCircleselected() {
		return Circleselected;
	}

	public void setCircleselected(String circleselected) {
		Circleselected = circleselected;
	}
}
