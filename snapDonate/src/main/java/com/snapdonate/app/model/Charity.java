package com.snapdonate.app.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Charity implements Serializable {
	private static final long serialVersionUID = -4493078677965775108L;
	private String mID;
	private String mName;
	private String mMedium;
	// TODO: Transient Variable
	// TODO: It marks a member variable not to be serialized when it is
	// persisted to streams of bytes.
	// TODO: When an object is transferred through the network, the object needs
	// to be 'serialized'.
	// TODO: Serialization converts the object state to serial bytes. Those
	// bytes are sent over the
	// TODO: network and the object is recreated from those bytes. Member
	// variables marked by the
	// TODO: java transient keyword are not transferred, they are lost
	// intentionally.
	private transient Bitmap mLogo;
	private String mVuforiaRecognizedName = "";

	public Charity() {
		// TODO Auto-generated constructor stub
	}

	public Charity(String ID, String name, String medium, Bitmap logo,
			String vuforiaRecognizedName) {
		super();
		this.mID = ID;
		this.mName = name;
		this.mMedium = medium;
		this.mLogo = logo;
		this.mVuforiaRecognizedName = vuforiaRecognizedName;
	}

	public String getId() {
		return mID;
	}

	public void setId(String id) {
		mID = id;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getVuforiaRecognizedName() {
		return mVuforiaRecognizedName;
	}

	public void setVuforiaRecognizedName(String vuforiaRecognizedName) {
		mVuforiaRecognizedName = vuforiaRecognizedName;
	}

	public Bitmap getLogo() {
		return mLogo;
	}

	public void setLogo(Bitmap logo) {
		mLogo = logo;
	}

	public String getMedium() {
		return mMedium;
	}

	public void setMedium(String medium) {
		mMedium = medium;
	}

	@Override
	public String toString() {
		return "Charity [id=" + mID + ", name=" + mName + ", medium=" + mMedium
				+ ", logo=" + mLogo + ", vuforiaRecognizedName="
				+ mVuforiaRecognizedName + ", getId()=" + getId()
				+ ", getName()=" + getName() + ", getVuforiaRecognizedName()="
				+ getVuforiaRecognizedName() + ", getLogo()=" + getLogo()
				+ ", getMedium()=" + getMedium() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}
