/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.model;

import java.util.ArrayList;

public class JobDetails {

	private String postid = "";
	private String subject = "";
	public int important = 0;
	public int like = 0;
	private String isb_jobs = "";

	private String content = "";
	private String postedon = "";
	private String userdto = "";
	private String id = "";
	private String name = "";
	private String image = "";
	private String replydto = "";
	private String sharedto = "";

	private String replyEmail = "";
	private String replyPhone = "";
	private String replyWatsApp = "";

	private String commentID = "";

	private String to = "";
	private String from = "";
	private String salaryTo = "";
	private String salaryFrom = "";

	public String postType = "";
	private ArrayList<ImageDetails> images = new ArrayList<ImageDetails>();
	private ArrayList<DocDetails> Doclist = new ArrayList<DocDetails>();

	private String numreplies = "";
	private String numshared = "";
	private String numcomment = "";
	private String numhides = "";
	private String numimportant = "";
	private String numspam = "";
	private String numlikes = "";

	public String getNumlikes() {
		return numlikes;
	}

	public void setNumlikes(String numlikes) {
		this.numlikes = numlikes;
	}

	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	public ArrayList<ImageDetails> getImages() {
		return images;
	}

	public void setImages(ArrayList<ImageDetails> images) {
		this.images = images;
	}

	public ArrayList<DocDetails> getDoclist() {
		return Doclist;
	}

	public void setDoclist(ArrayList<DocDetails> doclist) {
		Doclist = doclist;
	}

	public int showProgress = 0;

	/* For Sync */
	private String ISSyncData = "0";

	public String getISSyncData() {
		return ISSyncData;
	}

	public void setISSyncData(String iSSyncData) {
		ISSyncData = iSSyncData;
	}

	public String getPostid() {
		return postid;
	}

	public void setPostid(String postid) {
		this.postid = postid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getIsb_jobs() {
		return isb_jobs;
	}

	public void setIsb_jobs(String isb_jobs) {
		this.isb_jobs = isb_jobs;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPostedon() {
		return postedon;
	}

	public void setPostedon(String postedon) {
		this.postedon = postedon;
	}

	public String getUserdto() {
		return userdto;
	}

	public void setUserdto(String userdto) {
		this.userdto = userdto;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getReplydto() {
		return replydto;
	}

	public void setReplydto(String replydto) {
		this.replydto = replydto;
	}

	public String getSharedto() {
		return sharedto;
	}

	public void setSharedto(String sharedto) {
		this.sharedto = sharedto;
	}

	public int getImportant() {
		return important;
	}

	public void setImportant(int important) {
		this.important = important;
	}

	public int getLike() {
		return like;
	}

	public void setLike(int like) {
		this.like = like;
	}

	public String getReplyEmail() {
		return replyEmail;
	}

	public void setReplyEmail(String replyEmail) {
		this.replyEmail = replyEmail;
	}

	public String getReplyPhone() {
		return replyPhone;
	}

	public void setReplyPhone(String replyPhone) {
		this.replyPhone = replyPhone;
	}

	public String getReplyWatsApp() {
		return replyWatsApp;
	}

	public void setReplyWatsApp(String replyWatsApp) {
		this.replyWatsApp = replyWatsApp;
	}

	public String getCommentID() {
		return commentID;
	}

	public void setCommentID(String commentID) {
		this.commentID = commentID;
	}

	public String getNumreplies() {
		return numreplies;
	}

	public void setNumreplies(String numreplies) {
		this.numreplies = numreplies;
	}

	public String getNumshared() {
		return numshared;
	}

	public void setNumshared(String numshared) {
		this.numshared = numshared;
	}

	public String getNumcomment() {
		return numcomment;
	}

	public void setNumcomment(String numcomment) {
		this.numcomment = numcomment;
	}

	public String getNumhides() {
		return numhides;
	}

	public void setNumhides(String numhides) {
		this.numhides = numhides;
	}

	public String getNumimportant() {
		return numimportant;
	}

	public void setNumimportant(String numimportant) {
		this.numimportant = numimportant;
	}

	public String getNumspam() {
		return numspam;
	}

	public void setNumspam(String numspam) {
		this.numspam = numspam;
	}
}
