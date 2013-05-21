package sm.tb.geotabproj;

import java.util.List;

import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.Projection;
import org.mapsforge.android.maps.overlay.ArrayCircleOverlay;
import org.mapsforge.android.maps.overlay.OverlayCircle;
import org.mapsforge.core.GeoPoint;
import org.mapsforge.core.MercatorProjection;
import org.mapsforge.core.Tag;
import org.mapsforge.core.Tile;
import org.mapsforge.map.reader.MapDatabase;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.MotionEvent;


public class GeoTabMapView extends MapView{
	
	// variables declarations
	final int nodeRadiusTreshold = 80; // -> radius treshold to launch vocal announce of a touched node  
	public TextToSpeech tts = null; // -> to speak
	public MapDatabase mapDatabase; // -> contains the method to collect OSM elements (executequery())
	public GeoTabMapDatabaseCallback geoTabMapDatabaseCallback = null; // -> implements MapDatabaseCallback and contains point of interest
	public String tagKeyCurrent = ""; // -> selected key to announce
	public String tagValueCurrent = ""; // -> selected value to announce
	public float viewScale = (float)1.0; // -> scale of the view to zoom in and avoid a finger to touch too many object at the same time
	private int fingerNodeRadius; // -> radius of the finger track draw
	private String lastAnnounce = ""; // -> to be able not repeating the same announce	
	private boolean out = false; // -> to know if we are in or out
	private ArrayCircleOverlay circleOverlay; // -> list of layers containing circles 
	private OverlayCircle circle; // -> circles to fill circle overlay 
	private int mapScale = 8; // -> scale of the map

	
	public GeoTabMapView(Context context) {
		super(context);	
		
		// create callback
		geoTabMapDatabaseCallback = new GeoTabMapDatabaseCallback(this);
		// create dataBase
		mapDatabase = this.getMapDatabase();
		
		// create listener for TTS
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		// create TTS
		tts = new TextToSpeech(getContext(), onInitListener);
		
		// create circle overlay 
	    Paint circleDefaultPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
	    circleDefaultPaintFill.setColor(Color.YELLOW);
	    circleDefaultPaintFill.setAlpha(60);

	    Paint circleDefaultPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
	    circleDefaultPaintOutline.setStyle(Paint.Style.STROKE);
	    circleDefaultPaintOutline.setColor(Color.RED);
	    circleDefaultPaintOutline.setAlpha(60);
	    circleDefaultPaintOutline.setStrokeWidth(10);
	    
	    circleOverlay = new ArrayCircleOverlay(circleDefaultPaintFill,circleDefaultPaintOutline);
	    this.getOverlays().add(circleOverlay);
	}

	@Override
	public boolean onTouchEvent (MotionEvent event){
		super.onTouchEvent(event);
		
		// create action relative to touch events
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		
		// actions to do depending on touch events
		switch (action) {
		
		// when the finger touch the view
		case MotionEvent.ACTION_DOWN:
			
			// Explain the user what to do whether no data is selected 
			if (tagKeyCurrent.equals("")) 
			{
				tts.speak("sélectionner les données à afficher dans le menu en haut à droite", TextToSpeech.QUEUE_FLUSH, null);
			}
			// check if the contact is about to go out of map (does not work if viewscale is not 1.0)
			outOfMap(event.getX(), event.getY());
			
			// get the touch tile
			Projection projection = this.getProjection();
			long tileY = MercatorProjection.latitudeToTileY( projection.fromPixels((int)event.getX(0), (int)event.getY(0)).getLatitude(), (byte) mapScale);
			long tileX = MercatorProjection.longitudeToTileX( projection.fromPixels((int)event.getX(0), (int)event.getY(0)).getLongitude(), (byte) mapScale);
			Tile tile = new Tile(tileX, tileY, (byte) mapScale);
			
			// look for the nearest and announce only the name if the distance is under a treshold 
			this.mapDatabase.executeQuery(tile, this.geoTabMapDatabaseCallback);
			PointOfInterest nearestPOI = getNearestPOI(this.geoTabMapDatabaseCallback.pois , projection.fromPixels((int)event.getX(0), (int)event.getY(0)));		
			if (nearestPOI != null) 
			{
				List<Tag> tags = nearestPOI.getTags();
				for (int i = 0; i < tags.size(); i++)
				{
					if (tags.get(i).key.equals("name"))
					{
						tts.speak(""+tags.get(i).value, TextToSpeech.QUEUE_FLUSH, null);
					}
				}
			}
			
			break; // end of Motion.Event.ACTION_DOWN
		
		// when the finger leave the view
		case MotionEvent.ACTION_UP:
			// stop TTS
			tts.stop();
			// reset a potential out of map
			out = false;
			// make pois list empty
			this.geoTabMapDatabaseCallback.pois.clear();
				break;// end of Motion.Event.ACTION_DOWN
				
		case MotionEvent.ACTION_MOVE:
			// check if the contact is about to go out of map (does not work if viewscale is not 1.0)
			outOfMap(event.getX(), event.getY());
			
			// get the touch tile
			projection = this.getProjection();
			tileY = MercatorProjection.latitudeToTileY( projection.fromPixels((int)event.getX(0), (int)event.getY(0)).getLatitude(), (byte) mapScale);
			tileX = MercatorProjection.longitudeToTileX( projection.fromPixels((int)event.getX(0), (int)event.getY(0)).getLongitude(), (byte) mapScale);
			tile = new Tile(tileX, tileY, (byte) mapScale);
			
			// look for the nearest and announce only the name if the distance is under a treshold 
			this.mapDatabase.executeQuery(tile, this.geoTabMapDatabaseCallback);
			nearestPOI = getNearestPOI(this.geoTabMapDatabaseCallback.pois , projection.fromPixels((int)event.getX(0), (int)event.getY(0)));		
			if (nearestPOI != null) 
			{
				List<Tag> tags = nearestPOI.getTags();
				for (int i = 0; i < tags.size(); i++)
				{
					if ( tags.get(i).key.equals("name") && !lastAnnounce.equals(""+tags.get(i).value))
					{ 
						tts.speak(""+tags.get(i).value, TextToSpeech.QUEUE_FLUSH, null);
						lastAnnounce = ""+tags.get(i).value;
					}
				}
			}
			else 
			{
				lastAnnounce = "";
					tts.stop();
			}
		
			// make pois list empty
			this.geoTabMapDatabaseCallback.pois.clear();
			
			// draw finger course but use a lot of memory
			circle = new OverlayCircle(	new GeoPoint(
					projection.fromPixels((int)event.getX(0), (int)event.getY(0)).getLatitude(), 
					projection.fromPixels((int)event.getX(0), (int)event.getY(0)).getLongitude()), 
					this.fingerNodeRadius , "first overlay"); //radios in meter
			circleOverlay.addCircle(circle);
			
				break; // end of Motion.Event.ACTION_DOWN
				
		//for multitouch		
		case MotionEvent.ACTION_POINTER_DOWN:
//			Log.i("action", "ACTION_POINTER_DOWN");	
				break;
		
		//for multitouch
		case MotionEvent.ACTION_POINTER_UP:
//			Log.i("action", "ACTION_POINTER_UP");	
				break;
			
		default:
			break;
		} 
	
	return true;
	}
	
	// Get the nearest POI
	public PointOfInterest getNearestPOI(List<PointOfInterest> pois, GeoPoint origine) {
	    
		double distanceMin = 0;
	    PointOfInterest poiNearest = null;
	    
	    // Get Projection
	    Projection projection = this.getProjection();
	    
	    // origin pixel value
	    Point posOrigine = new Point();
		projection.toPixels(origine, posOrigine);
	    
		if (pois.size() > 0) 
		{
			// the first POI is the nearest
			poiNearest = pois.get(0);
			
			// Create a GeoPoint from poiNearest;
			GeoPoint geoPOI = new GeoPoint(poiNearest.getLatitude()* Math.pow(10, -6), poiNearest.getLongitude()* Math.pow(10, -6));
			Point posPOI = new Point();
			projection.toPixels(geoPOI, posPOI);
		    
			// calculate distance
			distanceMin = this.Distance(posOrigine.x,posOrigine.y,posPOI.x,posPOI.y);
			//Log.i("Dist", " 0 : " + distanceMin + "("+posPOI.x+" / "+posPOI.y+")");
			
			for (int i = 1; i < pois.size(); i++)
			{
				// actually it could be this one the nearest
				PointOfInterest poi = pois.get(i);
				
				// Create GeoPoint from poi; 
				geoPOI = new GeoPoint(poi.getLatitude()* Math.pow(10, -6), poi.getLongitude()* Math.pow(10, -6));
				projection.toPixels(geoPOI, posPOI);
				
				// Calculate distance
				double distance = this.Distance(posOrigine.x,posOrigine.y,posPOI.x,posPOI.y);
		
				// If distance is inferior, it was this one the nearest
				if (distance < distanceMin)
				{
					distanceMin = distance;
					poiNearest = poi;
				}
			}			
		}
		else 
			return null;
		
		if (distanceMin > (nodeRadiusTreshold/viewScale))
			poiNearest = null;
		
		return poiNearest;
	}

	// calculate distance between two points
	public double Distance(double x1, double y1, double x2, double y2) {
		double distance;
		distance = Math.sqrt( ((x2-x1) * (x2-x1)) + ((y2-y1) * (y2-y1)) );
		return distance;
	}
	
	//to convert pixels in meters
	public float convertRadiusToMeters(GeoPoint geo){
		
    Point circleCenter = new Point();
    Point circleBorder = new Point();
    
    // Get Projection
    Projection projection = this.getProjection();
    
    // Calculate center in pixels
    projection.toPixels(geo, circleCenter);
    
    //calculate border in pixels
    circleBorder.x = circleCenter.x + nodeRadiusTreshold; 
    circleBorder.y = circleCenter.y;
     
    //calculate geoBorder 
    GeoPoint geoBorder = new GeoPoint(	projection.fromPixels(circleBorder.x, circleBorder.y).getLatitude(), 
    									projection.fromPixels(circleBorder.x, circleBorder.y).getLongitude()); 
    
    //calculate distance in meters
    Location locationA = new Location("point A");
    locationA.setLatitude(geo.getLatitude());
    locationA.setLongitude(geo.getLongitude());
    
    Location locationB = new Location("point B");
    locationB.setLatitude(geoBorder.getLatitude());
    locationB.setLongitude(geoBorder.getLongitude());
    
    double distance = locationA.distanceTo(locationB);
    this.fingerNodeRadius = (int)((distance/10)/viewScale) ; 
    
    return (float)distance/viewScale  ; 
	}
	
	
	// Announce Out of maps
	public void outOfMap(float x, float y){
		int ratio = 20;
		float height = Geotab_activity.displaymetrics.heightPixels;
		float width = Geotab_activity.displaymetrics.widthPixels;

		if (!out){				
			if ( ( x<width/(ratio) || x>width-width/(ratio) || y<height/(ratio) || y>height-height/(ratio-12) )
					&& !tts.isSpeaking() 
					){
				tts.speak("Bord de la Carte", TextToSpeech.QUEUE_FLUSH , null);
				tts.playSilence(2000, TextToSpeech.QUEUE_ADD, null);
				out = true;
			}	
		}//end of if (!out)
		else { 
			Log.i("OutOfMap", "END ");
			out =false;
			tts.stop();
		}
		if (x>width/ratio || x<width-width/ratio || y>height/ratio || y<height-height/ratio){
		out = false;
		}
	}
	
	
	public int getMapScale() {
		return mapScale;
	}

	public void setMapScale(int mapScale) {
		this.mapScale = mapScale;
	}
	
	
}
