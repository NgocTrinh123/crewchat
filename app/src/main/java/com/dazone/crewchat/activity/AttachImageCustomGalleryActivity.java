package com.dazone.crewchat.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.dazone.crewchat.adapter.AttachGalleryAdapter;
import com.dazone.crewchat.constant.Statics;
import com.dazone.crewchat.dto.AttachDtoCustomGallery;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import com.dazone.crewchat.R;


public class AttachImageCustomGalleryActivity extends Activity {

	private Handler handler;
	private AttachGalleryAdapter adapter;

	private ImageView imgNoMedia;

	private String action;
	private ImageLoader imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.note_gallery);

		action = getIntent().getAction();
		if (action == null) {
			finish();
		}
		initImageLoader();
		init();
	}

	private void initImageLoader() {
		try {
			String CACHE_DIR = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + Statics.TEMP_PATH_IMAGE;
			new File(CACHE_DIR).mkdirs();

			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
					.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
			ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
					getBaseContext())
					.defaultDisplayImageOptions(defaultOptions)
					.memoryCache(new WeakMemoryCache());

			ImageLoaderConfiguration config = builder.build();
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(config);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {

		handler = new Handler();
		GridView gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);
		adapter = new AttachGalleryAdapter(getApplicationContext(), imageLoader);
		PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader,
				true, true);
		gridGallery.setOnScrollListener(listener);

		if (action.equalsIgnoreCase(Statics.ACTION_MULTIPLE_PICK)) {

			findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
			gridGallery.setOnItemClickListener(mItemMulClickListener);
			adapter.setMultiplePick(true);

		}

		gridGallery.setAdapter(adapter);
		imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);

		Button btnGalleryOk = (Button) findViewById(R.id.btnGalleryOk);
		btnGalleryOk.setOnClickListener(mOkClickListener);

		new Thread() {

			@Override
			public void run() {
				Looper.prepare();
				handler.post(new Runnable() {

					@Override
					public void run() {
						adapter.addAll(getGalleryPhotos());
						checkImageStatus();
					}
				});
				Looper.loop();
			}

		}.start();

	}

	private void checkImageStatus() {
		if (adapter.isEmpty()) {
			imgNoMedia.setVisibility(View.VISIBLE);
		} else {
			imgNoMedia.setVisibility(View.GONE);
		}
	}

	private final View.OnClickListener mOkClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			ArrayList<AttachDtoCustomGallery> selected = adapter.getSelected();

			String[] allPath = new String[selected.size()];
			String[] allName = new String[selected.size()];
			for (int i = 0; i < allPath.length; i++) {
				allPath[i] = selected.get(i).sdcardPath;
				allName[i] = selected.get(i).fileName;
			}

			Intent data = new Intent();
			data.putExtra(Statics.KEY_DATA_PATH_INTENT, allPath);
			data.putExtra(Statics.KEY_DATA_NAME_INTENT,allName);
			setResult(RESULT_OK, data);
			finish();

		}
	};
	private final AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			adapter.changeSelection(v, position);

		}
	};


	private ArrayList<AttachDtoCustomGallery> getGalleryPhotos() {
		ArrayList<AttachDtoCustomGallery> galleryList = new ArrayList<>();

		try {
			final String[] columns = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			final String orderBy = MediaStore.Images.Media._ID;

			Cursor imageCursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);

			if (imageCursor != null && imageCursor.getCount() > 0) {

				while (imageCursor.moveToNext()) {
					AttachDtoCustomGallery item = new AttachDtoCustomGallery();

					int dataColumnIndex = imageCursor
							.getColumnIndex(MediaStore.Images.Media.DATA);

					item.sdcardPath = imageCursor.getString(dataColumnIndex);
					int cut = item.sdcardPath.lastIndexOf('/');
					if (cut != -1) {
						item.fileName = item.sdcardPath.substring(cut + 1);
					}

					galleryList.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// show newest photo at beginning of the list
		Collections.reverse(galleryList);
		return galleryList;
	}

}
