package com.poncholay.bigbrother.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.util.Log;
import android.widget.Toast;

/**
 * Helps getting information from the system's contacts
 */
public class ContactDataManager {
	private static final String LOG_TAG = ContactDataManager.class.getName();
	final private Context context;
	final private Intent intent;

	public ContactDataManager(Context aContext, Intent anIntent) {
		this.context = aContext;
		this.intent = anIntent;
	}

	/**
	 * Retrieves the display Name of a contact
	 *
	 * @return Name of the contact referred to by the URI specified through the
	 * intent, {@link ContactDataManager#intent}
	 * @throws ContactQueryException if querying the Contact Details Fails
	 */
	public String getContactFirstName() throws ContactQueryException {
		return getContactInfo(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
	}

	public String getContactLastName() throws ContactQueryException {
		return getContactInfo(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
	}

	/**
	 * Retrieves the display Name of a contact
	 *
	 * @return Name of the contact referred to by the URI specified through the
	 * intent, {@link ContactDataManager#intent}
	 * @throws ContactQueryException if querying the Contact Details Fails
	 */
	private String getContactInfo(String info) throws ContactQueryException {
		Cursor nameCur = null;
		Cursor cont = null;
		String name = "";

		try {
			cont = context.getContentResolver().query(intent.getData(), null, null, null, null);
			if (!cont.moveToNext()) {
				Toast.makeText(context, "Cursor contains no data", Toast.LENGTH_LONG).show();
				return "";
			}
			int columnIndexForId = cont.getColumnIndex(ContactsContract.Contacts._ID);
			String contactId = cont.getString(columnIndexForId);

			String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = " + contactId;
			String[] whereNameParams = new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
			nameCur = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
			if (nameCur.moveToNext()) {
				name = nameCur.getString(nameCur.getColumnIndex(info));
			}
		} catch (NullPointerException ignored) {
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
			throw new ContactQueryException(e.getMessage());
		} finally {
			if (nameCur != null) {
				nameCur.close();
			}
			if (cont != null) {
				cont.close();
			}
		}

		return name;
	}

	/**
	 * Retrieves the email of a contact
	 *
	 * @return Email of the contact referred to by the URI specified through the
	 * intent, {@link ContactDataManager#intent}
	 * @throws ContactQueryException if querying the Contact Details Fails
	 */
	public String getContactEmail() throws ContactQueryException {
		Cursor cursor = null;
		String email = null;

		try {
			cursor = context.getContentResolver().query(Email.CONTENT_URI, null, Email.CONTACT_ID + "=?", new String[]{intent.getData().getLastPathSegment()}, null);
			if (cursor.moveToFirst()) {
				email = cursor.getString(cursor.getColumnIndex(Email.DATA));
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
			throw new ContactQueryException(e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return email;
	}

	public class ContactQueryException extends Exception {
		public ContactQueryException(String message) {
			super(message);
		}
	}

}