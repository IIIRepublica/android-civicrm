package org.upsam.civicrm.contact;

import org.upsam.civicrm.contact.model.ListContacts;

import android.net.Uri;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class ContactsRequest extends SpringAndroidSpiceRequest<ListContacts> {
	/**
	 * 
	 */
	final String key = "96783019a4bef4dd8541b176c828ff5f";
	/**
	 * 
	 */
	final String api_key = "81579f0a8cd7ba2e002267459d100476";
	/**
	 * 
	 */
	final String baseUri = "http://civicrm-upsam.btnhost.net/drupal/sites/all/modules/civicrm/extern/rest.php?json=1&sequential=1";

	/**
	 * 
	 */
	public ContactsRequest() {
		super(ListContacts.class);
	}

	@Override
	public ListContacts loadDataFromNetwork() throws Exception {
		Uri.Builder uriBuilder = Uri.parse(baseUri).buildUpon();
		uriBuilder.appendQueryParameter("entity", "Contact");
		uriBuilder.appendQueryParameter("action", "get");
		uriBuilder.appendQueryParameter("key", key);
		uriBuilder.appendQueryParameter("api_key", api_key);
		uriBuilder.appendQueryParameter("return[display_name]", "1");
		uriBuilder.appendQueryParameter("return[contact_type]", "1");
		uriBuilder.appendQueryParameter("return[contact_sub_type]", "1");

		String url = uriBuilder.build().toString();

		ListContacts contacts = getRestTemplate().getForObject(url,
				ListContacts.class);
		return contacts;
	}

	public String createCacheKey() {
		return "org.upsam.civicrm.contact.model.cache";
	}
}
