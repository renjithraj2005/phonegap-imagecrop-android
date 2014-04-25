package com.phonegap.plugins.cropPlugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;

public class openCropCamera extends CordovaPlugin {
	
    public CallbackContext callbackContext;
    private int encodingType;  
    private static final int JPEG = 0;                  // Take a picture of type JPEG

    
	private static final int CROP_FROM_CAMERA = 2;
	private static final int CAMERA = 1;    
	private static final int CROP = 2; 
	private static final int PICK_FROM_GALLERY = 3;
	private static final int MAX_IMAGE_SIZE = 150;
	
	private int srcType ,targetWidth ,targetHeight;
	private int mQuality = 80;  
	private Uri imageUri;  
    private static final String GET_PICTURE = "Get Picture";
	private static final String LOG_TAG = "CameraCrop";
	
	
	
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
		this.callbackContext = callbackContext;
		try {
			String params = args.getString(0);
			JSONObject jsonObject = new JSONObject(params);
			this.targetWidth = jsonObject.getInt("targetWidth");
			this.targetHeight = jsonObject.getInt("targetHeight");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			this.targetWidth = 150;
			this.targetHeight = 150;
			e1.printStackTrace();
		}
        
		if (action.equals("camera")) {
			srcType = CAMERA;
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			this.encodingType = JPEG;
			File photo = createCaptureFile(encodingType);
	        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
	        this.imageUri = Uri.fromFile(photo);

			try {
				if (this.cordova != null) {
		            this.cordova.startActivityForResult((CordovaPlugin) this, intent, CAMERA);
		        }
				//startActivityForResult(intent, PICK_FROM_CAMERA);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
				callbackContext.error("There is an issue with the camera, Please try again later." );
				PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                callbackContext.sendPluginResult(r);
                return true;
			}
            
            return true;
		}else if(action.equals("gallery")){
			srcType = PICK_FROM_GALLERY;
			this.encodingType = JPEG;

			Intent intent = new Intent();
	        String title = GET_PICTURE;
	        intent.setType("image/*");
	        intent.setAction(Intent.ACTION_GET_CONTENT);
	        intent.addCategory(Intent.CATEGORY_OPENABLE);

			try {
				 if (this.cordova != null) {
			            this.cordova.startActivityForResult((CordovaPlugin) this, Intent.createChooser(intent,
			                    new String(title)), PICK_FROM_GALLERY);
			        }
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
				callbackContext.error("There is an issue with the camera, Please try again later." );
				PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                callbackContext.sendPluginResult(r);
                return true;
			}
			
			return true;
		}
		
		else{
			this.failPicture("Error capturing image.");
		}
		return false;
	}
	
	
    private File createCaptureFile(int encodingType) {
        File photo = null;
        if (encodingType == JPEG) {
            photo = new File(getTempDirectoryPath(), ".Pic.jpg");
        } else {
            throw new IllegalArgumentException("Invalid Encoding Type: " + encodingType);
        }
        return photo;
    }
    
	private String getTempDirectoryPath() {
        File cache = null;

        // SD Card Mounted
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Android/data/" + cordova.getActivity().getPackageName() + "/cache/");
        }
        // Use internal storage
        else {
            cache = cordova.getActivity().getCacheDir();
        }

        // Create the cache directory if it doesn't exist
        cache.mkdirs();
        return cache.getAbsolutePath();
    }
	
	/**
     * Send error message to JavaScript.
     *
     * @param err
     */
    public void failPicture(String err) {
        this.callbackContext.error(err);
    }

   
    
    /**
     * Compress bitmap using jpeg, convert to Base64 encoded string, and return to JavaScript.
     *
     * @param bitmap
     */
    public void processPicture(Bitmap bitmap) {
        ByteArrayOutputStream jpeg_data = new ByteArrayOutputStream();
        try {
            if (bitmap.compress(CompressFormat.JPEG, mQuality, jpeg_data)) {
                byte[] code = jpeg_data.toByteArray();
                byte[] output = Base64.encode(code, Base64.NO_WRAP);
                String js_out = new String(output);
                this.callbackContext.success(js_out);
                js_out = null;
                output = null;
                code = null;
            }
        } catch (Exception e) {
            this.failPicture("Error compressing image.");
        }
        jpeg_data = null;
    }
       

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == Activity.RESULT_CANCELED) {
			this.failPicture("Operation Cancelled.");
		}else if(srcType == CAMERA){
        	srcType = CROP;
        	doCrop();
        }else if(srcType == PICK_FROM_GALLERY){
        	srcType = CROP;
        	try {
        		imageUri = intent.getData();
            	doCrop();
        	}
        	catch (Exception e) {
                this.failPicture("Error capturing image.");
            }
        	
        }else if (srcType == CROP){
        	try {
                // Create an ExifHelper to save the exif data that is lost during compression

                Bitmap bitmap = null;

                // If sending base64 image back
                Bundle extras = intent.getExtras();
		        if (extras != null) {	        	
		            bitmap = extras.getParcelable("data");
		        }
	
                
                // Double-check the bitmap.
                if (bitmap == null) {
                    this.failPicture("Unable to create bitmap!");
                    return;
                }
                
                BitmapUtils bputils =  new BitmapUtils();
				this.processPicture(bputils.scaleDown(bitmap , MAX_IMAGE_SIZE ,targetWidth,targetHeight, true));
                //this.processPicture(bitmap);

                bitmap = null;
                
            } catch (Exception e) {
                e.printStackTrace();
                this.failPicture("Error capturing image.");
            }
        }else if (resultCode == Activity.RESULT_CANCELED) {
            this.failPicture("Selection cancelled.");
        }
        else {
            this.failPicture("Selection did not complete!");
        }
	    
	}
    
    private void doCrop() {
    	
    	Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        
        List<ResolveInfo> list = this.cordova.getActivity().getPackageManager().queryIntentActivities( intent, 0 );
        
        int size = list.size();
        if (size == 0) {	        
        	Toast.makeText(this.cordova.getActivity(), "Can not find image crop app", Toast.LENGTH_SHORT).show();
        	try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.cordova.getActivity().getContentResolver(), imageUri);
				BitmapUtils bputils =  new BitmapUtils();
				this.processPicture(bputils.scaleDown(bitmap , MAX_IMAGE_SIZE ,targetWidth,targetHeight, true));
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return;
        } else {
        	intent.setData(imageUri);
            
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            
        	//if (size == 1) {
    		Intent i 		= new Intent(intent);
        	ResolveInfo res	= list.get(0);
	        
        	i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
        	this.cordova.startActivityForResult((CordovaPlugin) this, i, CROP_FROM_CAMERA);
        }
	}
}