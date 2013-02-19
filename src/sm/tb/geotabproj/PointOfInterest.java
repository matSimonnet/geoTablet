package sm.tb.geotabproj;

import java.util.ArrayList;

import org.mapsforge.core.Tag;

public class PointOfInterest {

	private double Latitude;
	private double Longitude;
	private ArrayList<Tag> tags;
	
	public PointOfInterest(double latitude, double longitude) {
		this.Latitude = latitude;
		this.Longitude = longitude;
		this.tags = new ArrayList<Tag>();
	}

	public double getLatitude() {
		return Latitude;
	}

	public void setLatitude(double latitude) {
		Latitude = latitude;
	}

	public double getLongitude() {
		return Longitude;
	}

	public void setLongitude(double longitude) {
		Longitude = longitude;
	}

	public ArrayList<Tag> getTags() {
		return tags;
	}

	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
	}

	public boolean add(Tag object) {
		return tags.add(object);
	}

	public int size() {
		return tags.size();
	}
	
	public void addTag(String key, String value)
	{
		tags.add(new Tag(key,value));	
	}
	
	
}
