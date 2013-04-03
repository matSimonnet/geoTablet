package sm.tb.geotabproj;

import java.io.File;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.Projection;
import org.mapsforge.android.maps.overlay.ArrayCircleOverlay;
import org.mapsforge.android.maps.overlay.OverlayCircle;
import org.mapsforge.core.GeoPoint;
import org.mapsforge.core.MercatorProjection;
import org.mapsforge.core.Tile;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

@SuppressLint("SdCardPath")
public class Geotab_activity extends MapActivity {
	
	private GeoTabMapView geoTabMapView;
	private MapController mapController;

	private TextToSpeech tts = null;
		
	private String folder = "map";
	private String map = "porsman";
	
	static public DisplayMetrics displaymetrics = null;
	
	private GeoPoint mapCenter;
	private float nodeRadiusInMeter = 0;
	
	private ArrayCircleOverlay circleOverlay;
	OverlayCircle circle;
	
	private GeoPoint node2Touch[] = new GeoPoint[100];
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //get screen size for out of map announce
		displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        
    	//set Full screen landscape
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		//hide actionBar (up)
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//hide menuBar (bottom)
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        
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
        geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/bretagne.map"));
//        geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/midi-pyrenees.map"));
//        geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/africa.map"));

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
//        mapController.setCenter(new GeoPoint(12.36, -1.53));
        //Brest defaulf
//		mapController.setCenter(new GeoPoint(48.40, -4.5));
        //TB
		mapController.setCenter(new GeoPoint(48.358855, -4.570278));
   
        // Set map scale
        mapController.setZoom(geoTabMapView.mapScale);
        geoTabMapView.mapScaleQuery = geoTabMapView.mapScale-2;
        
	    // Set view scale
        geoTabMapView.setScaleX(geoTabMapView.viewScale);
        geoTabMapView.setScaleY(geoTabMapView.viewScale);
        
        // set view parameter
//        geoTabMapView.setClickable(true);
//        geoTabMapView.setBuiltInZoomControls(true);
        
        //Fill view
        setContentView(geoTabMapView);
        
        // create a point to be used in overlay
        mapCenter = new GeoPoint(48.358855, -4.570278);
        
        // create 
//        nodeRadiusInMeter = geoTabMapView.convertRadiusToMeters(this.mapCenter);
//        Log.i("NODERADIUSINMETER", ""+nodeRadiusInMeter);
        
 
        // create the default paint objects for overlay circles
        Paint circleDefaultPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleDefaultPaintFill.setStyle(Paint.Style.FILL);
        circleDefaultPaintFill.setColor(Color.BLUE);
        circleDefaultPaintFill.setAlpha(64);
 
        Paint circleDefaultPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleDefaultPaintOutline.setStyle(Paint.Style.STROKE);
        circleDefaultPaintOutline.setColor(Color.BLUE);
        circleDefaultPaintOutline.setAlpha(128);
        circleDefaultPaintOutline.setStrokeWidth(3);
 
        // create the CircleOverlay and add the circles
        circleOverlay = new ArrayCircleOverlay(circleDefaultPaintFill,circleDefaultPaintOutline);
        circle = new OverlayCircle(this.mapCenter, nodeRadiusInMeter , "first overlay"); //radios in meter
        circleOverlay.addCircle(circle);
 
        // add all overlays to the MapView
        geoTabMapView.getOverlays().add(circleOverlay);        
    }

	//action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.actionbar, menu);
    	return super.onCreateOptionsMenu(menu);	
    }
    
    //action bar items
    @Override
    public boolean onOptionsItemSelected(MenuItem item){	
    	switch (item.getItemId()) {
		//map management
    	case R.id.map1:
			geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/africa.map"));
			mapCenter = new GeoPoint(12.36, -1.53);
			mapController.setCenter(mapCenter);
			geoTabMapView.mapScale=9;
			refreshMapViewScales();
			return true;
		case R.id.map2:
			geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/bretagne.map"));
			mapCenter = new GeoPoint(48.40, -4.5);
			mapController.setCenter(mapCenter);
			geoTabMapView.mapScale=14;
			refreshMapViewScales();
			return true;
		case R.id.map3:
			geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/" + folder + "/" + map + ".map"));
			mapCenter = new GeoPoint(48.4426, -4.778);
			mapController.setCenter(mapCenter);
			geoTabMapView.mapScale=18;
			refreshMapViewScales();
			return true;
		//scale management
		case R.id.scaleUp:
			if (geoTabMapView.mapScale < 18) geoTabMapView.mapScale = geoTabMapView.mapScale+1;
			refreshMapViewScales();
			return true;
		case R.id.scaleDown:
			if (geoTabMapView.mapScale > 1)geoTabMapView.mapScale = geoTabMapView.mapScale-1;
			refreshMapViewScales();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
    }
    
    public void refreshMapViewScales(){
		geoTabMapView.mapScaleQuery = geoTabMapView.mapScale-2;
		long tileY = MercatorProjection.latitudeToTileY( mapCenter.getLatitude() , (byte) geoTabMapView.mapScaleQuery);
		long tileX = MercatorProjection.longitudeToTileX( mapCenter.getLongitude(), (byte) geoTabMapView.mapScaleQuery);
		Tile tile = new Tile(tileX, tileY, (byte) geoTabMapView.mapScaleQuery);
		geoTabMapView.mapDatabase.executeQuery(tile, geoTabMapView.callback);	
		Log.i("SIZE", "" + geoTabMapView.callback.pois.size());
		
		for(int i = 1; i<geoTabMapView.callback.pois.size();i++){
		node2Touch[i] = new GeoPoint(geoTabMapView.callback.pois.get(i).getLatitude(),geoTabMapView.callback.pois.get(i).getLongitude() );
		Log.i("NODE2TOUCH", "NODE2TOUCH_" + i + " = " +  geoTabMapView.callback.pois.get(i).getTags().toString() 
			 //+ "Lat = "	+  node2Touch[i].getLatitude() + " Long = " +  node2Touch[i].getLongitude() );
				+ "Lat = "	+  geoTabMapView.callback.pois.get(i).getLatitude()*Math.pow(10, -6) + geoTabMapView.callback.pois.get(i).getLongitude()*Math.pow(10, -6) );
		}
		
		mapController.setZoom(geoTabMapView.mapScale);
		nodeRadiusInMeter = geoTabMapView.convertRadiusToMeters(this.mapCenter);
		circle.setCircleData(mapCenter, nodeRadiusInMeter);		
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
