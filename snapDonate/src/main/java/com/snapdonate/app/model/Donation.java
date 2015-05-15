package com.snapdonate.app.model;

import java.io.Serializable;

public class Donation implements Serializable {
	private static final long serialVersionUID = -4124086699918414278L;
	private String mAutoID;
	private Charity mCharity;
	private String mAmount;
	private String mStatus;
	private String mTimeStamp;
	private String mIsSynced;
	private String mReferenceId;
	private String mSavedServerId;

	public Donation() {
	}

	public Donation(String autoId, Charity charity, String amount,
			String status, String date, String isSyced, String referenceId,
			String savedServerID) {
		super();
		this.mAutoID = autoId;
		this.mCharity = charity;
		this.mAmount = amount;
		this.mStatus = status;
		this.mTimeStamp = date;
		this.mIsSynced = isSyced;
		this.mReferenceId = referenceId;
		this.mSavedServerId = savedServerID;
	}

	public String getAutoId() {
		return mAutoID;
	}

	public void setAutoId(String autoId) {
		this.mAutoID = autoId;
	}

	public Charity getCharity() {
		return mCharity;
	}

	public void setCharity(Charity charity) {
		this.mCharity = charity;
	}

	public String getAmount() {
		return mAmount;
	}

	public void setAmount(String amount) {
		this.mAmount = amount;
	}

	public String getSatus() {
		return mStatus;
	}

	public void setStatus(String status) {
		mStatus = status;
	}

	public String getTimeStamp() {
		return mTimeStamp;
	}

	public void setTimeStamp(String date) {
		this.mTimeStamp = date;
	}

	public String getIsSynced() {
		return mIsSynced;
	}

	public void setIsSynced(String isSyced) {
		this.mIsSynced = isSyced;
	}

	public String getDonationReferenceId() {
		return mReferenceId;
	}

	public void setDonationReferenceId(String donationReferenceId) {
		this.mReferenceId = donationReferenceId;
	}

	public String getSavedServerId() {
		return mSavedServerId;
	}

	public void setSavedServerId(String savedServerId) {
		this.mSavedServerId = savedServerId;
	}

	@Override
	public String toString() {
		return "Donation [autoId=" + mAutoID + ", charityModel=" + mCharity
				+ ", amount=" + mAmount + ", status=" + mStatus + ", date="
				+ mTimeStamp + ", isSyced=" + mIsSynced + ", referenceId="
				+ mReferenceId + ", savedServerID=" + mSavedServerId
				+ ", getAutoId()=" + getAutoId() + ", getcharityModel()="
				+ getCharity() + ", getAmount()=" + getAmount()
				+ ", getSatus()=" + getSatus() + ", getDate()=" + getTimeStamp()
				+ ", getIsSyced()=" + getIsSynced()
				+ ", getDonationReferenceId()=" + getDonationReferenceId()
				+ ", getSavedServerId()=" + getSavedServerId()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

}
