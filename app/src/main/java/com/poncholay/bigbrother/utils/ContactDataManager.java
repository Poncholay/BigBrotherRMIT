package com.poncholay.bigbrother.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

/**
 * Helps getting information from the system's contacts
 */
public class ContactDataManager {
   private static final String LOG_TAG = ContactDataManager.class.getName();
   private Context context;
   private Intent intent;

   public class ContactQueryException extends Exception {
      public ContactQueryException(String message) {
         super(message);
      }
   }

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
   public String getContactName() throws ContactQueryException {
      Cursor cursor = null;
      String name = null;

      try {
         cursor = context.getContentResolver().query(intent.getData(), null, null, null, null);
         if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(Contacts.DISPLAY_NAME));
         }
      } catch (Exception e) {
         Log.e(LOG_TAG, e.getMessage());
         throw new ContactQueryException(e.getMessage());
      } finally {
         if (cursor != null) {
            cursor.close();
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

}