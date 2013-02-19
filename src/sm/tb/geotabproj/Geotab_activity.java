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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

@SuppressLint("SdCardPath")
public class Geotab_activity extends MapActivity {
	
	private GeoTabMapView geoTabMapView;
	private MapController mapController;

	private TextToSpeech tts = null;
		
	private String folder = "map";
	private String map = "porsman";

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	// Set Full screen landscape
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/" + folder + "/" + map + ".map"));
//        geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/bretagne.map"));
//        geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/midi-pyrenees.map"));

        // Retrieve geoTabMapView mapController  
        mapController = geoTabMapView.getController();
		
        // Set mapCenter
        //Porsman
//        mapController.setCenter(new GeoPoint(48.4426, -4.778));
        //Toulouse
//        mapController.setCenter(new GeoPoint(43.6037, 1.441779));
        //Africa
        mapController.setCenter(new GeoPoint(5, 16));
		
        // Set map scale
        mapController.setZoom(geoTabMapView.mapScale);
        
	    // Set view scale
        geoTabMapView.setScaleX(geoTabMapView.viewScale);
        geoTabMapView.setScaleY(geoTabMapView.viewScale);
       
        //Fill view;
        setContentView(geoTabMapView);
		
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
