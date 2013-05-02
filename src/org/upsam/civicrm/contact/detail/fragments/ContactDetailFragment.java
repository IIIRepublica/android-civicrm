package org.upsam.civicrm.contact.detail.fragments;

import java.util.List;
import java.util.Locale;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.upsam.civicrm.AbstractAsyncFragment;
import org.upsam.civicrm.CiviCRMAsyncRequest;
import org.upsam.civicrm.CiviCRMAsyncRequest.ACTION;
import org.upsam.civicrm.CiviCRMAsyncRequest.ENTITY;
import org.upsam.civicrm.R;
import org.upsam.civicrm.contact.detail.req.ContactImageRequest;
import org.upsam.civicrm.contact.model.contact.Contact;
import org.upsam.civicrm.contact.model.contact.ContactSummary;
import org.upsam.civicrm.contact.model.email.Email;
import org.upsam.civicrm.contact.model.email.ListEmails;
import org.upsam.civicrm.contact.model.lang.PreferredLanguage;
import org.upsam.civicrm.contact.model.telephone.ListPhones;
import org.upsam.civicrm.contact.model.telephone.Phone;
import org.upsam.civicrm.util.CiviCRMRequestHelper;
import org.upsam.civicrm.util.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

//@SuppressLint("ValidFragment")
public class ContactDetailFragment extends AbstractAsyncFragment {
	/**
	 * Vista nombre de contacto
	 */
	private TextView displayName;
	/**
	 * Vista tipo de contacto
	 */
	private TextView type;
	/**
	 * Vista foto de contacto
	 */
	private QuickContactBadge badge;
	/**
	 * 
	 */
	private LinearLayout contactData;
	/**
	 * Datos generales de contacto
	 */
	private ContactSummary contactSummary;
	/**
	 * Datos detallados de contacto
	 */
	private Contact contactDetails;
	/**
	 * Indica si hemos mostrado ya las preferencias de comunicaci�n
	 */
	private boolean yetUpdatedCommunicationPreferences;
	/**
	 * Indica si hemos mostrado ya los datos demogr�ficos
	 */
	private boolean yetUpdateDemographics;

	/**
	 * 
	 * @param contentManager
	 */
	public ContactDetailFragment(SpiceManager contentManager,
			Context activityContext) {
		super(contentManager, activityContext);
		this.yetUpdatedCommunicationPreferences = false;
		this.yetUpdateDemographics = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.contact_details_layout,
				container, false);
		this.displayName = (TextView) view.findViewById(R.id.display_name);
		this.type = (TextView) view.findViewById(R.id.contact_type);
		this.badge = (QuickContactBadge) view.findViewById(R.id.contac_img);
		this.badge.setMode(ContactsContract.QuickContact.MODE_SMALL);
		this.badge.setVisibility(View.INVISIBLE);
		this.contactData = (LinearLayout) view.findViewById(R.id.contact_data);
		this.type.setText(this.contactSummary.getType());
		this.displayName.setText(this.contactSummary.getName());
		return view;
	}

	/**
	 * 
	 */
	private void executeRequests() {
		this.progressDialog = Utilities.showLoadingProgressDialog(
				this.progressDialog, activityContext,
				getString(R.string.progress_bar_msg_generico));
		this.contactSummary = getArguments().getParcelable("contact");
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>(
				1);
		params.add("contact_id", Integer.toString(this.contactSummary.getId()));
		CiviCRMAsyncRequest<Contact> request = new CiviCRMAsyncRequest<Contact>(
				activityContext, Contact.class, ACTION.getsingle,
				ENTITY.Contact, params);
		contentManager.execute(request, request.createCacheKey(),
				DurationInMillis.ONE_MINUTE, new ContactDetailListener());
		// peticionar emails y phones
		CiviCRMAsyncRequest<ListEmails> emailsReq = new CiviCRMAsyncRequest<ListEmails>(
				activityContext, ListEmails.class, ACTION.get, ENTITY.Email,
				params);
		CiviCRMAsyncRequest<ListPhones> phonesReq = new CiviCRMAsyncRequest<ListPhones>(
				activityContext, ListPhones.class, ACTION.get, ENTITY.Phone,
				params);
		contentManager.execute(emailsReq, emailsReq.createCacheKey(),
				DurationInMillis.ONE_MINUTE, new ContactEmailListener());
		contentManager.execute(phonesReq, phonesReq.createCacheKey(),
				DurationInMillis.ONE_MINUTE, new ContactPhoneListener());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		executeRequests();
	}

	public void showCommunicationPreferences() {
		this.progressDialog = Utilities.showLoadingProgressDialog(
				this.progressDialog, activityContext,
				getString(R.string.progress_bar_msg_generico));
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>(
				2);
		params.add("contact_id", Integer.toString(this.contactSummary.getId()));
		params.add("return[preferred_language]", "1");
		CiviCRMAsyncRequest<PreferredLanguage> langReq = new CiviCRMAsyncRequest<PreferredLanguage>(
				activityContext, PreferredLanguage.class, ACTION.getsingle,
				ENTITY.Contact, params);
		contentManager.execute(langReq, langReq.createCacheKey(),
				DurationInMillis.ONE_MINUTE, new ContactLangListener());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
	}

	private void refreshView(Contact result) {
		this.contactDetails = result;
		String img = this.contactDetails.getImage();
		if (StringUtils.hasText(img)) {
			ContactImageRequest request = new ContactImageRequest(img);
			contentManager.execute(request, img, DurationInMillis.ONE_MINUTE,
					new ContactImageListener());
		} else {
			refreshImageView(null);
		}
		final String email = this.contactDetails.getEmail();
		final String phone = this.contactDetails.getPhone();
		if (StringUtils.hasText(phone)) {
			this.badge.assignContactFromPhone(phone, true);

		} else if (StringUtils.hasText(email)) {
			this.badge.assignContactFromEmail(email, true);
		}

	}

	private void refreshImageView(Bitmap result) {
		if (result != null) {
			this.badge.setImageBitmap(result);
		}
		this.badge.setVisibility(View.VISIBLE);
	}

	private void paintDataRow(int iconResource, String text1, String text2) {
		View view = null;
		ImageView imageView = null;
		TextView textView1 = null;
		TextView textView2 = null;
		view = getLayoutInflater(null).inflate(R.layout.contact_detail_row,
				contactData, false);
		imageView = (ImageView) view.findViewById(R.id.imageView1);
		textView1 = (TextView) view.findViewById(R.id.textView1);
		textView2 = (TextView) view.findViewById(R.id.textView2);
		imageView.setImageResource(iconResource);
		textView1.setText(text1);
		textView1.setTextAppearance(activityContext, R.style.textoDefault);
		if (StringUtils.hasText(text2)) {
			textView2.setText(text2);
			textView2.setTextAppearance(activityContext, R.style.textoWhite);
		} else {
			textView2.setVisibility(View.GONE);
		}
		this.contactData.addView(view);
	}

	private void refreshEmailsView(ListEmails result) {
		List<Email> emails = result.getValues();

		if (emails != null && !emails.isEmpty()) {
			for (Email email : emails) {
				paintDataRow(
						android.R.drawable.ic_dialog_email,
						email.getEmail(),
						email.isPrimary() ? getString(R.string.email_detail_ppal)
								: getString(R.string.email_detail));
			}
		}
	}

	private void refreshPhoneView(ListPhones result) {
		List<Phone> phones = result.getValues();
		if (phones != null && !phones.isEmpty()) {
			for (Phone phone : phones) {
				paintDataRow(
						android.R.drawable.ic_menu_call,
						phone.getPhone(),
						phone.isPrimary() ? getString(R.string.telephone_detail_ppal)
								: getString(R.string.telephone_detail));
			}
		}
		Utilities.dismissProgressDialog(progressDialog);
	}

	public void updateDemographics() {
		if (!this.yetUpdateDemographics) {
			String gender = this.contactDetails.getGender();
			String birthDate = this.contactDetails.getBirthDate();
			char isDeceased = this.contactDetails.getIsDeceased();
			if (StringUtils.hasText(gender)) {
				paintDataRow(android.R.drawable.ic_menu_view, gender,
						getString(R.string.gender_detail));
			}
			if (StringUtils.hasText(birthDate)) {
				paintDataRow(android.R.drawable.ic_menu_my_calendar, birthDate,
						getString(R.string.birthdate_detail));
			}
			if ('1' == isDeceased) {
				String deceasedDate = this.contactDetails.getDeceasedDate();
				paintDataRow(android.R.drawable.ic_menu_myplaces,
						getString(R.string.deceased_detail),
						StringUtils.hasText(deceasedDate) ? deceasedDate : "");
			}
			this.yetUpdateDemographics = true;
		}

	}

	private void updateCommunicationPreferences(PreferredLanguage result) {
		if (!this.yetUpdatedCommunicationPreferences) {
			char[] props = { this.contactDetails.getDoNotEmail(),
					this.contactDetails.getDoNotPhone(),
					this.contactDetails.getDoNotSms(),
					this.contactDetails.getDoNotTrade() };
			String[] values = { getString(R.string.do_not_email),
					getString(R.string.do_not_phone),
					getString(R.string.do_not_sms),
					getString(R.string.do_not_trade) };
			for (int i = 0; i < props.length; i++) {
				if ('1' == (props[i])) {
					paintDataRow(android.R.drawable.ic_delete, values[i], "");
				}
			}
			if (result != null
					&& StringUtils.hasText(result.getPreferredLanguage())) {
				paintDataRow(android.R.drawable.ic_menu_sort_alphabetically,
						new Locale(result.getPreferredLanguage())
								.getDisplayLanguage(),
						getString(R.string.language_detail));
			}
			this.yetUpdatedCommunicationPreferences = true;
		}
		Utilities.dismissProgressDialog(progressDialog);
	}

	private class ContactDetailListener implements RequestListener<Contact> {

		@Override
		public void onRequestFailure(SpiceException spiceException) {
			CiviCRMRequestHelper.notifyRequestError(activityContext,
					progressDialog);
		}

		@Override
		public void onRequestSuccess(Contact result) {
			refreshView(result);
		}

	}

	private class ContactImageListener implements RequestListener<Bitmap> {

		@Override
		public void onRequestFailure(SpiceException spiceException) {
			CiviCRMRequestHelper.notifyRequestError(activityContext,
					progressDialog);
		}

		@Override
		public void onRequestSuccess(Bitmap result) {
			if (result == null)
				return;
			refreshImageView(result);
		}
	}

	private class ContactEmailListener implements RequestListener<ListEmails> {

		@Override
		public void onRequestFailure(SpiceException spiceException) {
			CiviCRMRequestHelper.notifyRequestError(activityContext,
					progressDialog);
		}

		@Override
		public void onRequestSuccess(ListEmails result) {
			if (result == null)
				return;
			refreshEmailsView(result);
		}

	}

	private class ContactPhoneListener implements RequestListener<ListPhones> {

		@Override
		public void onRequestFailure(SpiceException spiceException) {
			CiviCRMRequestHelper.notifyRequestError(activityContext,
					progressDialog);
		}

		@Override
		public void onRequestSuccess(ListPhones result) {
			if (result == null)
				return;
			refreshPhoneView(result);
		}

	}

	public class ContactLangListener implements
			RequestListener<PreferredLanguage> {

		@Override
		public void onRequestFailure(SpiceException spiceException) {
			CiviCRMRequestHelper.notifyRequestError(activityContext,
					progressDialog);
		}

		@Override
		public void onRequestSuccess(PreferredLanguage result) {
			if (result == null)
				return;
			updateCommunicationPreferences(result);

		}
	}

}
