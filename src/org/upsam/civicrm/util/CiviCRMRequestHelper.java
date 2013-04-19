package org.upsam.civicrm.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.upsam.civicrm.CiviCRMAsyncRequest;
import org.upsam.civicrm.CiviCRMAsyncRequest.ACTION;
import org.upsam.civicrm.CiviCRMAsyncRequest.ENTITY;
import org.upsam.civicrm.activity.model.ListActivtiesSummary;

import android.content.Context;

public class CiviCRMRequestHelper {

	public static CiviCRMAsyncRequest<ListActivtiesSummary> requestActivitiesForContact(
			int contactId, Context ctx) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>(
				1);
		params.add("contact_id", String.valueOf(contactId));
		return new CiviCRMAsyncRequest<ListActivtiesSummary>(ctx,
				ListActivtiesSummary.class, ACTION.get, ENTITY.Activity, params);
	}
}