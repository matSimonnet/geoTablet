package sm.tb.geotabproj;

import java.io.File;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.overlay.ArrayCircleOverlay;
import org.mapsforge.android.maps.overlay.OverlayCircle;
import org.mapsforge.core.GeoPoint;
import org.mapsforge.core.MercatorProjection;
import org.mapsforge.core.Tag;
import org.mapsforge.core.Tile;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

@SuppressLint("SdCardPath")
public class Geotab_activity extends MapActivity {
	
	// Variables declarations 
	static public DisplayMetrics displaymetrics = null; // -> to get screensize in pixels
	private float nodeRadiusInMeter; //-> to get the radius of a node in meters
	private GeoTabMapView geoTabMapView; //->The MapView
	private MapController mapController; // -> To control the MapView Center and scale
	private GeoPoint mapCenter; // -> the center of the MapView
	private ArrayCircleOverlay circleOverlay; // -> the circles list 
	private OverlayCircle circle; // -> the overlay
	private Tag[] allTags = null; // -> a table of tags to pick the list of the existing objects in the tiles of the current MapView 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // get screen size for out of map announce
		displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        
    	// set screen landscape
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    
        // creates MapView instance
        geoTabMapView = new GeoTabMapView(this);
        
        // fill view
        setContentView(geoTabMapView); 
        
        // gives file to geoTabMapView
        geoTabMapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/map/africa.map"));

        // Retrieve geoTabMapView mapController  
        mapController = geoTabMapView.getController();

        // Set mapCenter in Burkina
        mapCenter = new GeoPoint(12.00, -1.53);
        mapController.setCenter(mapCenter);
   
        // Set map scale
        mapController.setZoom(geoTabMapView.getMapScale());
        
	    // Set view scale
        geoTabMapView.setScaleX(geoTabMapView.viewScale);
        geoTabMapView.setScaleY(geoTabMapView.viewScale);
        
        // create the paint objects for overlay circles
        Paint circleDefaultPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleDefaultPaintFill.setStyle(Paint.Style.FILL);
        circleDefaultPaintFill.setColor(Color.BLUE);
        circleDefaultPaintFill.setAlpha(2);
 
        Paint circleDefaultPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleDefaultPaintOutline.setStyle(Paint.Style.STROKE);
        circleDefaultPaintOutline.setColor(Color.BLUE);
        circleDefaultPaintOutline.setAlpha(128);
        circleDefaultPaintOutline.setStrokeWidth(3);
 
        // create the CircleOverlay
        circleOverlay = new ArrayCircleOverlay(circleDefaultPaintFill,circleDefaultPaintOutline);

        // add all overlays to the MapView
        geoTabMapView.getOverlays().add(circleOverlay);
        
    }

	//create action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.actionbar, menu);
    	return super.onCreateOptionsMenu(menu);	
    }
    
    //create action for action bar items
    @Override
    public boolean onOptionsItemSelected(MenuItem item){	
    	
    	switch (item.getItemId()) {
		
    	// country to display management
    	case R.id.map1:
			mapCenter = new GeoPoint(12, -1.53);
			geoTabMapView.setMapScale(8);
			refreshMap();
			return true;
		case R.id.map2:
			return true;
		case R.id.map3:
			return true;
		
		// displayed objects management
		case R.id.item1:
			geoTabMapView.tagKeyCurrent = "place";
			geoTabMapView.tagValueCurrent = "country";
			refreshMap();
			return true;
		case R.id.item2:
			geoTabMapView.tagKeyCurrent = "place";
			geoTabMapView.tagValueCurrent = "city";
			refreshMap();
			return true;
		case R.id.item3:
			geoTabMapView.tagKeyCurrent = "place";
			geoTabMapView.tagValueCurrent = "town";
			refreshMap();
			return true;
		case R.id.item4:
			geoTabMapView.tagKeyCurrent = "place";
			geoTabMapView.tagValueCurrent = "village";
			refreshMap();
			return true;
		case R.id.item5:
			geoTabMapView.tagKeyCurrent = "place";
			geoTabMapView.tagValueCurrent = "hamlet";
			refreshMap();
			return true;
			
		// scale management
		case R.id.scaleUp:
			if (geoTabMapView.getMapScale() < 18) geoTabMapView.setMapScale(geoTabMapView.getMapScale()+1);
			refreshMap();
			return true;
		case R.id.scaleDown:
			if (geoTabMapView.getMapScale() > 1)geoTabMapView.setMapScale(geoTabMapView.getMapScale()-1);
			refreshMap();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
    }
    
    public void refreshMap(){		
    	
    	// center the map
    	mapController.setCenter(mapCenter);
    	    	
    	//apply the scale of the map
    	mapController.setZoom(geoTabMapView.getMapScale());
    	
    	//adjust the radius for the node display
		nodeRadiusInMeter = geoTabMapView.convertRadiusToMeters(this.mapCenter); 

		//clean previous pois contents
		geoTabMapView.callback.pois.clear();
		
		//clean previous overlay
		circleOverlay.clear();
		
		//get the central tile
		long tileX = MercatorProjection.longitudeToTileX( mapCenter.getLongitude(), (byte) geoTabMapView.getMapScale());
		long tileY = MercatorProjection.latitudeToTileY( mapCenter.getLatitude() , (byte) geoTabMapView.getMapScale());
		
		//get the peripherical tiles
		for (int i = 0; i < 4 ; i++  )
			{
			for (int j = 0; j < 4 ; j++)
				{
				Tile tile = new Tile( (tileX+i) , (tileY+j), (byte) (geoTabMapView.getMapScale() ) );
				Tile tileOpposite = new Tile( (tileX-i) , (tileY-j), (byte) (geoTabMapView.getMapScale() ) );
				Tile tileOppositeBis = new Tile( (tileX+i) , (tileY-j), (byte) (geoTabMapView.getMapScale() ) );
				Tile tileOppositeTer = new Tile( (tileX-i) , (tileY+j), (byte) (geoTabMapView.getMapScale() ) );
				
				//Fill pois content 
				geoTabMapView.callback.pois.clear();
				geoTabMapView.mapDatabase.executeQuery(tile, geoTabMapView.callback);
				geoTabMapView.mapDatabase.executeQuery(tileOpposite, geoTabMapView.callback);
				geoTabMapView.mapDatabase.executeQuery(tileOppositeBis, geoTabMapView.callback);
				geoTabMapView.mapDatabase.executeQuery(tileOppositeTer, geoTabMapView.callback);
				
				// add pois from different tiles in the overlay 	
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
		
		//display all object in logcat
		allTags = geoTabMapView.mapDatabase.getMapFileInfo().poiTags;
		for (int at = 0; at<allTags.length; at++ ){
			Log.w("allTags", "key = " + allTags[at].key + "// value = " + allTags[at].value );
		}
		
		//clean 
		geoTabMapView.callback.pois.clear();
		
		
		}//end of refreshmapscale()
	
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
