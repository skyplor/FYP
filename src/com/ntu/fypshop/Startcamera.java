package com.ntu.fypshop;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;

public class Startcamera extends Activity {
	private static final int CAMERA_PIC_REQUEST = 1337;
	private static final int GALLERY_REQUEST = 1500;
	private static String _path = "";
	Uri outputFileUri;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		openAddPhoto();
		// startCameraActivity();

	}

	protected void startCameraActivity()
	{
		_path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
		// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// File file = new File(_path, "test.jpg");
		// Uri outputFileUri = Uri.fromFile(file);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		// startActivityForResult(intent, 0);

		// Log.i("MakeMachine", "startCameraActivity()" );
		File file = new File(_path, "testing.jpg");

		try
		{
			if (file.exists() == false)
			{
				file.createNewFile();
			}

		}
		catch (IOException e)
		{}

		outputFileUri = Uri.fromFile(file);

		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outputFileUri);

		// Uri outputFileUri = Uri.fromFile( file );

		// Intent intent = new
		// Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
		// intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );

		startActivityForResult(intent, CAMERA_PIC_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == CAMERA_PIC_REQUEST)
		{
			// BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inSampleSize = 2;

			// Bitmap bitmap = BitmapFactory.decodeFile( _path + "test.jpg",
			// options );
			// _image = (ImageView) findViewById(R.id.imageView2);
			// _image.setImageBitmap(bitmap);
			// Bundle bundle=data.getExtras();
			// Bitmap pic = (Bitmap) bundle.get("pic");
			// Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
			_path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
			File file = new File(_path, "testing.jpg");
			outputFileUri = Uri.fromFile(file);
			Bundle bundle = getIntent().getExtras();
			if (bundle == null)
			{
				Intent i = new Intent(Startcamera.this, Browseplace.class);
				i.putExtra("pic", outputFileUri);
				startActivity(i);
			}
			else
			{
				Intent i = new Intent(Startcamera.this, Attraction.class);
				i.putExtra("pic", outputFileUri);
				i.putExtra("EMPLOYEE_ID", getIntent().getIntExtra("EMPLOYEE_NAME", 0));
				i.putExtra("EMPLOYEE_NAME", getIntent().getStringExtra("EMPLOYEE_NAME"));
				startActivity(i);
			}

			// _image = (ImageView) findViewById(R.id.imageView2);
			// _image.setImageBitmap(thumbnail);
		}
		else if (requestCode == GALLERY_REQUEST)
		{
			Bundle bundle = getIntent().getExtras();
			if (bundle == null)
			{
				Intent i = new Intent(this, Browseplace.class);
				outputFileUri = data.getData();
				i.putExtra("pic", outputFileUri);
				startActivity(i);
			}
			else
			{
				Intent i = new Intent(this, Attraction.class);
				outputFileUri = data.getData();
				i.putExtra("pic", outputFileUri);
				i.putExtra("EMPLOYEE_ID", getIntent().getIntExtra("EMPLOYEE_NAME", 0));
				i.putExtra("EMPLOYEE_NAME", getIntent().getStringExtra("EMPLOYEE_NAME"));
				startActivity(i);
			}

		}
		/*
		 * Log.i( "MakeMachine", "resultCode: " + resultCode ); switch(
		 * resultCode ) { case 0: Log.i( "MakeMachine", "User cancelled" );
		 * break;
		 * 
		 * case -1: onPhotoTaken(); break; }
		 */
	}

	private void openAddPhoto()
	{

		String[] addPhoto = new String[]
		{ "Camera", "Gallery" };
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Get your picture from");

		dialog.setItems(addPhoto, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int id)
			{
				if (id == 0)
				{
					startCameraActivity();
				}
				if (id == 1)
				{
					startGallery();
				}
			}
		});

		dialog.setNeutralButton("Cancel", new android.content.DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

				dialog.dismiss();
			}
		});
		dialog.show();
	}

	protected void startGallery()
	{
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, GALLERY_REQUEST);
	}

}
