package com.fleecast.stamina.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.fleecast.stamina.R;
import com.fleecast.stamina.models.IconChooserGridViewAdapter;
import com.fleecast.stamina.utility.Constants;


public class IconChooserActivity extends Activity implements AdapterView.OnItemClickListener{

   private GridView gridView;

    private String[] IMAGE_URLS;
    public static Activity myActivityInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_icon_chooser);

        myActivityInstance = IconChooserActivity.this;

        AssetManager assetManager  = getAssets();
        try {
            IMAGE_URLS = assetManager.list("group_images");
        }
        catch (Exception e){
            Log.e("MMM","BBB");
            e.printStackTrace();
        }
        gridView = (GridView) findViewById(R.id.icon_chooser_gridview);
        gridView.setAdapter(new IconChooserGridViewAdapter(this,IMAGE_URLS));
        gridView.setOnItemClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("DBG", "IconChooserActivity onPause");
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {

        try {

            Intent intent = new Intent(this,AddEditGroupItem.class);

            intent.putExtra(Constants.ICON_NAME_FROM_ASSETS,IMAGE_URLS[position]);

            setResult(Constants.REQUEST_CODE_ICON_CHOOSE_ACTIVITY, intent);

            this.finish();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
