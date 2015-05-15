package com.snapdonate.app.utils;

public class URLManager {
	/**
	 * Donations Urls
	 */
	public static final String EXIT_URL = "http%3a%2f%2fblog.justgiving.com%2fpath?donationId=JUSTGIVING-DONATION-ID";
	public static final String JUST_GIVING_URL = "http://blog.justgiving.com/path?donationId=";
	public static final String SANDBOX_URL = "https://uk.sandbox.virginmoneygiving.com/fundraiser-web/donate/donationConfirmationDisplay.action";
	// liv
	public static final String JUST_GIVING_DONATION_URL = "http://www.justgiving.com/4w350m3/donation/direct/charity/";
	// sandbox
	public static final String JUST_GIVING_DONATION_URL_SAND_BOX = "https://v3-sandbox.justgiving.com/4w350m3/donation/direct/charity/";
	// liv
	public static final String VIRGIN_MONEY_FUNDRAISER_URL = "http://uk.virginmoneygiving.com/BowelCancerUK";
	// sandbox
	public static final String VIRGIN_MONEY_FUNDRAISER_URL_SAND_BOX = "http://uk.sandbox.virginmoneygiving.com/team/pito";

	/**
	 * Server Database Urls
	 */
	// lotiv
	public static final String ADD_DONATION_URL_LOTIV = "http://snapdonate.lotiv.com/api/add";
	public static final String DELETE_DONATION_URL_LOTIV = "http://snapdonate.lotiv.com/api/delete";
	public static final String UPDATE_DONATION_URL_LOTIV = "http://snapdonate.lotiv.com/api/update";
	public static final String ADD_GCM_REGISTRATION_URL_LOTIV = "http://snapdonate.lotiv.com/api/add_gcm_regid";
	// live
	public static final String ADD_DONATION_URL_LIVE = "http://162.242.208.250/api/add";
	public static final String DELETE_DONATION_URL_LIVE = "http://162.242.208.250/api/delete";
	public static final String UPDATE_DONATION_URL_LIVE = "http://162.242.208.250/api/update";
	public static final String ADD_GCM_REGISTRATION_URL_LIVE = "http://162.242.208.250/api/add_gcm_regid";
}
