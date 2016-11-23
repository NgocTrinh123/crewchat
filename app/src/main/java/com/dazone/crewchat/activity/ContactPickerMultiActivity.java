package com.dazone.crewchat.activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.dazone.crewchat.R;
import com.dazone.crewchat.dto.UserDto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ContactPickerMultiActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivMore;

    // List variables
    public String[] Contacts = {};
    public int[] to = {};
    public ListView myListView;
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker_multi);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ivMore = (ImageView) findViewById(R.id.more_menu);
        ivMore.setOnClickListener(this);
        myListView = (ListView) findViewById(R.id.lvContact);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Cursor mCursor = getContacts();
        startManagingCursor(mCursor);

        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                mCursor,
                Contacts = new String[] { ContactsContract.Contacts.DISPLAY_NAME },
                to = new int[] { android.R.id.text1 });
        myListView.setItemsCanFocus(false);
        myListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        myListView.setAdapter(adapter);


    }

    private Cursor getContacts() {
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] { ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME };
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '"
                + ("1") + "'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        return managedQuery(uri, projection, selection, selectionArgs,
                sortOrder);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_menu:

                long[] id = myListView.getCheckedItemIds();//  i get the checked contact_id instead of position
                String[] phoneNumber = new String[id.length];

                ArrayList<UserDto> userDtos = new ArrayList<>();


                for (int i = 0; i < id.length; i++) {
                    phoneNumber[i] = getPhoneNumber(id[i]); // get phonenumber from selected id
                    UserDto u = new UserDto();
                    u.setPhoneNumber(getPhoneNumber(id[i]));
                    u.setAvatar(getPhotoUri(id[i]).toString());
                    String name = getContactName(id[i]);
                    u.setFullName(name);

                    userDtos.add(u);
                }

                Intent pickContactIntent = new Intent();
                pickContactIntent.putExtra("PICK_CONTACT", userDtos);// Add checked phonenumber in intent and finish current activity.

                setResult(RESULT_OK, pickContactIntent);
                finish();

                break;
        }
    }

    public Uri getPhotoUri(long contactId) {
        ContentResolver contentResolver = getContentResolver();

        try {
            Cursor cursor = contentResolver
                    .query(ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.CONTACT_ID
                                    + "="
                                    + contactId
                                    + " AND "

                                    + ContactsContract.Data.MIMETYPE
                                    + "='"
                                    + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                                    + "'", null, null);

            if (cursor != null) {
                if (!cursor.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Uri person = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, contactId);
        return Uri.withAppendedPath(person,
                ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    public InputStream openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }


    public InputStream openDisplayPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd =
                    getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            return fd.createInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    private String getPhoneNumber(long id) {
        String phone = null;
        Cursor phonesCursor = null;
        phonesCursor = queryPhoneNumbers(id);
        if (phonesCursor == null || phonesCursor.getCount() == 0) {
            // No valid number
            //signalError();
            return null;
        } else if (phonesCursor.getCount() == 1) {
            // only one number, call it.
            phone = phonesCursor.getString(phonesCursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        } else {
            phonesCursor.moveToPosition(-1);
            while (phonesCursor.moveToNext()) {

                // Found super primary, call it.
                phone = phonesCursor.getString(phonesCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                break;

            }
        }

        return phone;
    }


    private Cursor queryPhoneNumbers(long contactId) {
        ContentResolver cr = getContentResolver();
        Uri baseUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                contactId);
        Uri dataUri = Uri.withAppendedPath(baseUri,
                ContactsContract.Contacts.Data.CONTENT_DIRECTORY);

        Cursor c = cr.query(dataUri, new String[] { ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.IS_SUPER_PRIMARY, ContactsContract.RawContacts.ACCOUNT_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.LABEL }, ContactsContract.Contacts.Data.MIMETYPE + "=?",
                new String[] { ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE }, null);
        if (c != null && c.moveToFirst()) {
            return c;
        }
        return null;
    }

    private String getContactName(long contactId) {
        String contactName = null;
        ContentResolver cr = getContentResolver();
        Uri baseUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                contactId);
        Uri dataUri = Uri.withAppendedPath(baseUri,
                ContactsContract.Contacts.Data.CONTENT_DIRECTORY);
        // querying contact data store
        Cursor c = getContentResolver().query(dataUri, null, null, null, null);
        if (c.moveToFirst()) {
            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
            contactName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        c.close();
        return contactName;
    }
}
