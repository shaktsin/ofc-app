/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.model;

public class ImageDetails {
	private int ImageID = -1;
	private String ImageURL = "";
	private String ImageName = "";

	public String getImageName() {
		return ImageName;
	}

	public void setImageName(String imageName) {
		ImageName = imageName;
	}

	public int getImageID() {
		return ImageID;
	}

	public void setImageID(int imageID) {
		ImageID = imageID;
	}

	public String getImageURL() {
		return ImageURL;
	}

	public void setImageURL(String imageURL) {
		ImageURL = imageURL;
	}
}
