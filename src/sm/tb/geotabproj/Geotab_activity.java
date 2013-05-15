package sm.tb.geotabproj;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.overlay.ArrayCircleOverlay;
import org.mapsforge.android.maps.overlay.OverlayCircle;
import org.mapsforge.core.GeoPoint;
import org.mapsforge.core.MercatorProjection;
import org.mapsforge.core.Tag;
import org.mapsforge.core.Tile;

import android.R.string;
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
	
	static public DisplayMetrics displaymetrics = null;
	
private float nodeRadiusInMeter = 0;
	
	public float getNodeRadiusInMeter() {
		return nodeRadiusInMeter;
	}
	
	private GeoTabMapView geoTabMapView;
	private MapController mapController;

	private TextToSpeech tts = null;
		
	private String folder = "map";
	private String map = "porsman";
	
	private GeoPoint mapCenter;
	
	private ArrayCircleOverlay circleOverlay;
	private OverlayCircle circle;
	
	private Tag[] allTags = null;
	
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
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//      getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        	
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
        geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/midi-pyrenees.map"));
//        geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/africa.map"));

        // Retrieve geoTabMapView mapController  
        mapController = geoTabMapView.getController();
		
        // Set mapCenter
        //Porsman
//        mapController.setCenter(new GeoPoint(48.4426, -4.778));
        //Toulouse
        mapController.setCenter(new GeoPoint(43.6037, 1.441779));
        //Africa
//        mapController.setCenter(new GeoPoint(5.0, 30.0));
        //Burkina
//        mapController.setCenter(new GeoPoint(12.36, -1.53));
        //Brest defaulf
//		  mapController.setCenter(new GeoPoint(48.40, -4.5));
        //TB
//		mapController.setCenter(new GeoPoint(48.358855, -4.570278));
   
        // Set map scale
        mapController.setZoom(geoTabMapView.getMapScale());
        //geoTabMapView.mapScaleQuery = geoTabMapView.mapScale;
        
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
        circleDefaultPaintFill.setAlpha(2);
 
        Paint circleDefaultPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleDefaultPaintOutline.setStyle(Paint.Style.STROKE);
        circleDefaultPaintOutline.setColor(Color.BLUE);
        circleDefaultPaintOutline.setAlpha(128);
        circleDefaultPaintOutline.setStrokeWidth(3);
 
        // create the CircleOverlay and add the circles
        circleOverlay = new ArrayCircleOverlay(circleDefaultPaintFill,circleDefaultPaintOutline);
        //circle = new OverlayCircle(this.mapCenter, nodeRadiusInMeter , "first overlay"); //radios in meter
        //circleOverlay.addCircle(circle);
 
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
			geoTabMapView.setMapScale(9);
			refreshMap();
			return true;
		case R.id.map2:
			geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/bretagne.map"));
			mapCenter = new GeoPoint(48.40, -4.5);
			mapController.setCenter(mapCenter);
			geoTabMapView.setMapScale(14);
			refreshMap();
			return true;
		case R.id.map3:
			geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/midi-pyrenees.map"));
			mapCenter = new GeoPoint(43.6037, 1.441779);
			mapController.setCenter(mapCenter);
			geoTabMapView.setMapScale(12);
			refreshMap();
			return true;
//		case R.id.map3:
//			geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/" + folder + "/" + map + ".map"));
//			mapCenter = new GeoPoint(48.4426, -4.778);
//			mapController.setCenter(mapCenter);
//			geoTabMapView.setMapScale(18);
//			refreshMapViewScales();
//			return true;
		//scale management
		case R.id.scaleUp:
			if (geoTabMapView.getMapScale() < 18) geoTabMapView.setMapScale(geoTabMapView.getMapScale()+1);
			refreshMap();
			return true;
		case R.id.scaleDown:
			if (geoTabMapView.getMapScale() > 1)geoTabMapView.setMapScale(geoTabMapView.getMapScale()-1);
			refreshMap();
			return true;
		case R.id.item1:
			geoTabMapView.tagKeyCurrent = "place";
			geoTabMapView.tagValueCurrent = "city";
			refreshMap();
			return true;
		case R.id.item2:
			geoTabMapView.tagKeyCurrent = "place";
			geoTabMapView.tagValueCurrent = "town";
			refreshMap();
			return true;
		case R.id.item3:
			geoTabMapView.tagKeyCurrent = "place";
			geoTabMapView.tagValueCurrent = "village";
			refreshMap();
			return true;
		case R.id.item4:
			geoTabMapView.tagKeyCurrent = "place";
			geoTabMapView.tagValueCurrent = "hamlet";
			refreshMap();
			return true;
		case R.id.item5:
			geoTabMapView.tagKeyCurrent = "highway";
			geoTabMapView.tagValueCurrent = "bus_station";
			refreshMap();
			return true;
		case R.id.item6:
			geoTabMapView.tagKeyCurrent = "railway";
			geoTabMapView.tagValueCurrent = "station";
			refreshMap();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
    }
    
    public void refreshMap(){		
    	
    	//set scales and size of circles
    	mapController.setZoom(geoTabMapView.getMapScale());
//    	Log.w("geoTabMapView.getMapScale()", ""+geoTabMapView.getMapScale());
//       	Log.w("geoTabMapView.getMapScaleQuery()", ""+geoTabMapView.getMapScaleQuery());
    	
		nodeRadiusInMeter = geoTabMapView.convertRadiusToMeters(this.mapCenter); 	

		//clean POI callback and previous drawings
		geoTabMapView.callback.pois.clear();
		circleOverlay.clear();
		
		//get the tile
		long tileX = MercatorProjection.longitudeToTileX( mapCenter.getLongitude(), (byte) geoTabMapView.getMapScale());
		long tileY = MercatorProjection.latitudeToTileY( mapCenter.getLatitude() , (byte) geoTabMapView.getMapScale());
		
		for (int i = 0; i < 4 ; i++  )
			{
			for (int j = 0; j < 4 ; j++)
				{
				Tile tile = new Tile( (tileX+i) , (tileY+j), (byte) (geoTabMapView.getMapScale() ) );
				Tile tileOpposite = new Tile( (tileX-i) , (tileY-j), (byte) (geoTabMapView.getMapScale() ) );
				Tile tileOppositeBis = new Tile( (tileX+i) , (tileY-j), (byte) (geoTabMapView.getMapScale() ) );
				Tile tileOppositeTer = new Tile( (tileX-i) , (tileY+j), (byte) (geoTabMapView.getMapScale() ) );
				//get POI 
				geoTabMapView.callback.pois.clear();
				geoTabMapView.mapDatabase.executeQuery(tile, geoTabMapView.callback);
				geoTabMapView.mapDatabase.executeQuery(tileOpposite, geoTabMapView.callback);
				geoTabMapView.mapDatabase.executeQuery(tileOppositeBis, geoTabMapView.callback);
				geoTabMapView.mapDatabase.executeQuery(tileOppositeTer, geoTabMapView.callback);
				
				//Draw		
				for(int k = 0; k < geoTabMapView.callback.pois.size(); k++)
					{
					circle = new OverlayCircle(new GeoPoint(geoTabMapView.callback.pois.get(k).getLatitude()*Math.pow(10, -6),
															 geoTabMapView.callback.pois.get(k).getLongitude()*Math.pow(10, -6) ), 
															 nodeRadiusInMeter , 
															 "first overlay"); 
					circleOverlay.addCircle(circle);
					}//end of for k
				}//end of for j
			}//end of for i
		
		//get tag
		allTags = geoTabMapView.mapDatabase.getMapFileInfo().poiTags;
		for (int at = 0; at<allTags.length; at++ ){
			Log.w("allTags", "key = " + allTags[at].key + "// value = " + allTags[at].value );
		}
		
		//clean 
		geoTabMapView.callback.pois.clear();
		
		
		}//end of refreshmapscale()
    
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
