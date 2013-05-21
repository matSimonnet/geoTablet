package sm.tb.geotabproj;

import java.util.ArrayList;
import java.util.List;

import org.mapsforge.core.Tag;
import org.mapsforge.map.reader.MapDatabaseCallback;


//CallBack which allows to get OSM elements of a tile
public class GeoTabMapDatabaseCallback implements MapDatabaseCallback{
	
	// Variable declarations
	public List<PointOfInterest> pois = new ArrayList<PointOfInterest>();
	public GeoTabMapView geoTabMapView;

	
	public GeoTabMapDatabaseCallback(GeoTabMapView geoTabMapView) {
		this.geoTabMapView = geoTabMapView;
	}
	
	// method which is called as many times as there is OSM element of the type : node
	@Override
	public void renderPointOfInterest(byte arg0, int latitude, int longitude, List<Tag> tags) {
		
		// create a point of interest with latitude and longitude of the current OSM element 
		PointOfInterest poi = new PointOfInterest(latitude, longitude);
//		Log.i("POSITIONS = ","Latitude = " + poi.getLatitude() + "// longitude = " + poi.getLongitude() );
		
		// for each tag of the current OSM element, we add the tags to the poi
		for (int i = 0; i < tags.size(); i++)	
		{
			// each key and value of the current OSM element are added to the poi
			poi.addTag(tags.get(i).key, tags.get(i).value);
//			Log.i("renderPointOfInterest", "key = " + tags.get(i).key.toString() + " // value = " + tags.get(i).value.toString());

			// if tags are selected, the current poi is added to the pois list
			if (       tags.get(i).key.equals(geoTabMapView.tagKeyCurrent) 
					&& tags.get(i).value.equals(geoTabMapView.tagValueCurrent)
				)
			{
				pois.add(poi);
//				for (int iP = 0; iP < pois.size(); iP++) Log.i("pois" + i , pois.get(iP).getTags().toString() + "" );
			}//end of if			
		}//end of for
	}

	@Override
	public void renderWaterBackground() {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderWay(byte arg0, float[] arg1, List<Tag> arg2, float[][] arg3) {
		// TODO Auto-generated method stub		
	}

}
