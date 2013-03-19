package sm.tb.geotabproj;

import java.io.File;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.core.GeoPoint;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

@SuppressLint("SdCardPath")
public class Geotab_activity extends MapActivity {
	
	private GeoTabMapView geoTabMapView;
	private MapController mapController;

	private TextToSpeech tts = null;
		
	private String folder = "map";
	private String map = "porsman";
	
	static public DisplayMetrics displaymetrics = null;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //get screen size for out of map announce
		displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        
    	// Set Full screen landscape
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        
        // Write raw ressources less than 1MB on the device SDCard
        MapSDWriter.write( getResources().openRawResource(R.raw.porsman), folder, map);
    	
        // Init Text-to-Speech
        OnInitListener onInitListener = new OnInitListener() {
    		@Override
    		public void onInit(int status) {
    		}
    	};
    	tts = new TextToSpeech(this, onInitListener);
        
        // Creates mapView instance
        geoTabMapView = new GeoTabMapView(this);
        
        // Gives file to geoTabMapView
//        geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/" + folder + "/" + map + ".map"));
//        geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/bretagne.map"));
//        geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/midi-pyrenees.map"));
        geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/africa.map"));

        // Retrieve geoTabMapView mapController  
        mapController = geoTabMapView.getController();
		
        // Set mapCenter
        //Porsman
//        mapController.setCenter(new GeoPoint(48.4426, -4.778));
        //Toulouse
//        mapController.setCenter(new GeoPoint(43.6037, 1.441779));
        //Africa
//        mapController.setCenter(new GeoPoint(5.0, 30.0));
        //Burkina
        mapController.setCenter(new GeoPoint(12.36, -1.53));
        //Brest defaulf
//		mapController.setCenter(new GeoPoint(48.40, -4.5));
   
        // Set map scale
        mapController.setZoom(geoTabMapView.mapScale);
        
	    // Set view scale
        geoTabMapView.setScaleX(geoTabMapView.viewScale);
        geoTabMapView.setScaleY(geoTabMapView.viewScale);
       
        //Fill view;
        setContentView(geoTabMapView);
		
    }

	//action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.actionbar, menu);
    	return super.onCreateOptionsMenu(menu);	
    }
    
    //item de l'action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){	
    	switch (item.getItemId()) {
		//map management
    	case R.id.map1:
			geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/africa.map"));
			mapController.setCenter(new GeoPoint(12.36, -1.53));
			geoTabMapView.mapScale=9;
			mapController.setZoom(geoTabMapView.mapScale);
			return true;
		case R.id.map2:
			geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/bretagne.map"));
			mapController.setCenter(new GeoPoint(48.40, -4.5));
			geoTabMapView.mapScale=14;
			mapController.setZoom(geoTabMapView.mapScale);
			return true;
		case R.id.map3:
			geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/" + folder + "/" + map + ".map"));
			mapController.setCenter(new GeoPoint(48.4426, -4.778));
			geoTabMapView.mapScale=18;
			mapController.setZoom(geoTabMapView.mapScale);
			return true;
		//scale management
		case R.id.scaleUp:
			geoTabMapView.mapScale = geoTabMapView.mapScale+1;
			mapController.setZoom(geoTabMapView.mapScale);
			return true;
		case R.id.scaleDown:
			geoTabMapView.mapScale = geoTabMapView.mapScale-1;
			mapController.setZoom(geoTabMapView.mapScale);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
    }
    
	public TextToSpeech getTts() {
		return tts;
	}

	public void setTts(TextToSpeech tts) {
		this.tts = tts;
	}
	
	@Override
	protected void onDestroy() {
		Log.i("GeoTabActivity","onDestroy()");
		if (geoTabMapView.tts != null){
			geoTabMapView.tts.stop();
			geoTabMapView.tts.shutdown();
		}
		super.onDestroy();	
	}

	@Override
	protected void onPause() {
		Log.i("GeoTabActivity","onPause()");
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.i("GeoTabActivity","onResume()");
		super.onResume();
	}

}
