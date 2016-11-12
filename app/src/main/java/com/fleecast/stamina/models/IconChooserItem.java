package com.fleecast.stamina.models;

import android.graphics.Bitmap;

/**
 * 
 * @author nnt
 *
 */

public class IconChooserItem {
	Bitmap image;
	String image_name;
	
	public IconChooserItem(Bitmap image, String image_name) {
		super();
		this.image = image;
		this.image_name = image_name;
	}
	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}
	public String getImageName() {
		return image_name;
	}
	public void setImageName(String image_name) {
		this.image_name = image_name;
	}
	

}
