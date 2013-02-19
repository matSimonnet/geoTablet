package sm.tb.geotabproj;

import java.util.List;

import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.Projection;
import org.mapsforge.core.GeoPoint;
import org.mapsforge.core.MercatorProjection;
import org.mapsforge.core.Tag;
import org.mapsforge.core.Tile;
import org.mapsforge.map.reader.MapDatabase;

import android.content.Context;
import android.graphics.Point;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.MotionEvent;


public class GeoTabMapView extends MapView{
	
	//caution : treshold should be relative to the scale
	final int nodeRadiusTreshold = 100;
	String lastAnnounce = "";
	MapDatabase mapDatabase;
	private GeoTabMapDatabaseCallback callback = null;
	public TextToSpeech tts = null; 
	
	//View Scale
	public float viewScale = (float)2.5;
	//Tile Scale
	public int mapScale = 18;
	
	public GeoTabMapView(Context context) {
		super(context);	
		callback = new GeoTabMapDatabaseCallback(this);
		mapDatabase = this.getMapDatabase();
		
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		tts = new TextToSpeech(getContext(), onInitListener);
	}
	

	@Override
	public boolean onTouchEvent (MotionEvent event){
		super.onTouchEvent(event);
	
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
//			Log.i("action", "MotionEvent.ACTION_DOWN");
			Projection projection = this.getProjection();
			long tileY = MercatorProjection.latitudeToTileY( projection.fromPixels((int)event.getX(0), (int)event.getY(0)).getLatitude(), (byte) mapScale);
			long tileX = MercatorProjection.longitudeToTileX( projection.fromPixels((int)event.getX(0), (int)event.getY(0)).getLongitude(), (byte) mapScale);
			Tile tile = new Tile(tileX, tileY, (byte) mapScale);
			this.mapDatabase.executeQuery(tile, this.callback);
			PointOfInterest nearestPOI = getNearestPOI(this.callback.pois , projection.fromPixels((int)event.getX(0), (int)event.getY(0)));		
			if (nearestPOI != null) 
			{
				List<Tag> tags = nearestPOI.getTags();
				for (int i = 0; i < tags.size(); i++)
				{
					if (tags.get(i).key.equals("name"))
					{
						tts.speak(""+tags.get(i).value, TextToSpeech.QUEUE_FLUSH, null);
					}
//					else
//						tts.stop();
					//Log.i( "nearestPOI.getTags()" , "key = "+ tags.get(i).key + " ; value = " + tags.get(i).value);
					//Toast.makeText(getContext(), "Key = "+ tags.get(i).key , Toast.LENGTH_SHORT).show() ; //+ " value = " + tags.get(i).value
				}
			}
			break;
		
		case MotionEvent.ACTION_UP:
//			Log.i("action", "MotionEvent.ACTION_UP");
			tts.stop();
				break;
				
		case MotionEvent.ACTION_POINTER_DOWN:
//			Log.i("action", "ACTION_POINTER_DOWN");	
				break;
		
		case MotionEvent.ACTION_POINTER_UP:
//			Log.i("action", "ACTION_POINTER_UP");	
				break;
				
		case MotionEvent.ACTION_MOVE:
//			Log.i("action", "MotionEvent.ACTION_MOVE");
			projection = this.getProjection();
			tileY = MercatorProjection.latitudeToTileY( projection.fromPixels((int)event.getX(0), (int)event.getY(0)).getLatitude(), (byte) mapScale);
			tileX = MercatorProjection.longitudeToTileX( projection.fromPixels((int)event.getX(0), (int)event.getY(0)).getLongitude(), (byte) mapScale);
			tile = new Tile(tileX, tileY, (byte) mapScale);
			this.mapDatabase.executeQuery(tile, this.callback);
			//Log.i("getNearestPOI", "this.callback.pois.size = " + this.callback.pois.size());
			nearestPOI = getNearestPOI(this.callback.pois , projection.fromPixels((int)event.getX(0), (int)event.getY(0)));		
			
			//>>>>>>>>>>>>>>>>>>>>>>>>> LE BON ALGO POUR LES ANNONCES
			if (nearestPOI != null) {
				List<Tag> tags = nearestPOI.getTags();
				for (int i = 0; i < tags.size(); i++){
					if ( tags.get(i).key.equals("name") && !lastAnnounce.equals(""+tags.get(i).value)){ 
						tts.speak(""+tags.get(i).value, TextToSpeech.QUEUE_FLUSH, null);
						lastAnnounce = ""+tags.get(i).value;
					}
//					Log.i( "nearestPOI.getTags()" , "key = "+ tags.get(i).key + " ; value = " + tags.get(i).value);
//					Toast.makeText(getContext(), "Key = "+ tags.get(i).key , Toast.LENGTH_SHORT).show() ; //+ " value = " + tags.get(i).value
				}
			}
			else 
			{
				lastAnnounce = "";
				tts.stop();
			}
			
			this.callback.pois.clear();
				break;
				// LE BON ALGO POUR LES ANNONCES <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
			
		default:
			break;
		} 
	
	return true;
	}
	
	// Recuperation du POI le plus proche
	public PointOfInterest getNearestPOI(List<PointOfInterest> pois, GeoPoint origine) {
	    double distanceMin = 0;
	    PointOfInterest poiNearest = null;
	    
	    // Get Projection
	    Projection projection = this.getProjection();
	    
	    // Valeur en pixel de origine
	    Point posOrigine = new Point();
		projection.toPixels(origine, posOrigine);
		//Log.i("Dist","" + posOrigine.x + " : "+ posOrigine.y);
	    
		if (pois.size() > 0) 
		{
			// Le premier POI est le plus proche
			poiNearest = pois.get(0);
			
			// Cree un GeoPoint a partir de poiNearest;
			GeoPoint geoPOI = new GeoPoint(poiNearest.getLatitude()* Math.pow(10, -6), poiNearest.getLongitude()* Math.pow(10, -6));
			Point posPOI = new Point();
			projection.toPixels(geoPOI, posPOI);
		    
			// calcul distance
			distanceMin = this.Distance(posOrigine.x,posOrigine.y,posPOI.x,posPOI.y);
			//Log.i("Dist", " 0 : " + distanceMin + "("+posPOI.x+" / "+posPOI.y+")");
			
			for (int i = 1; i < pois.size(); i++)
			{
				// En fait non c'est peut celui ci qui est le plus proche
				PointOfInterest poi = pois.get(i);
				
				// Cree un GeoPoint a partir de poi; 
				geoPOI = new GeoPoint(poi.getLatitude()* Math.pow(10, -6), poi.getLongitude()* Math.pow(10, -6));
				projection.toPixels(geoPOI, posPOI);
				
				// Calcul la distance
				double distance = this.Distance(posOrigine.x,posOrigine.y,posPOI.x,posPOI.y);
				//Log.i("Dist", " "+iP+" : " + distance + "("+posPOI.x+" / "+posPOI.y+")");
//				List<Tag> tags = poi.getTags();
//				for (int iT = 0; iT < tags.size(); iT++)
//				{
//					Log.i("Dist",""+tags.get(iT).key + " / "+ tags.get(iT).value);
//				}
//				
				// Si distance inferieur, c'etait bien lui le plus proche
				if (distance < distanceMin)
				{
					distanceMin = distance;
					poiNearest = poi;
				}
			}
			
			//Log.i("Dist", " MinDist : " + distanceMin);
		}
		else 
			return null;
		
//		List<Tag> tags = poiNearest.getTags();
//		for (int iT = 0; iT < tags.size(); iT++)
//		{
//			Log.i("Dist",""+tags.get(iT).key + " / "+ tags.get(iT).value);
//		}
		
		if (distanceMin > (nodeRadiusTreshold/viewScale))
			poiNearest = null;
		
		return poiNearest;
			
	}

	// Fonction pour avoir la distance entre 2 points
	public double Distance(double x1, double y1, double x2, double y2) {
		double distance;
		distance = Math.sqrt( ((x2-x1) * (x2-x1)) + ((y2-y1) * (y2-y1)) );
		return distance;
	}
	
}
