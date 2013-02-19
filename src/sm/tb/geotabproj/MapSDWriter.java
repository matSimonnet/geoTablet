package sm.tb.geotabproj;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.util.Log;

public class MapSDWriter {
	
	MapSDWriter(){	
	}
	
	public static void write(InputStream in, String folder, String map  ){
		//check if the map does exist
        if ( !new File(Environment.getExternalStorageDirectory().getPath() + "/" + folder + "/" + map + ".map").exists() )
        {	        	
    		try 
    		{
    			//creates folder to put maps
    			new File(Environment.getExternalStorageDirectory().getPath()+ "/" + folder + "/").mkdir();
    			OutputStream out = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/" + folder + "/" + map + ".map"));
    		 
    			int read = 0;
    			int i = 0;
    			byte[] bytes = new byte[1024];
    		 
    			while ((read = in.read(bytes)) != -1) 
    			{
    				out.write(bytes, 0, read);
    				i++;
    				Log.i("MapSDWriter","i = " + i );
    			}
    		 
    			//closes streams
    			in.close();
    			out.flush();
    			out.close();
    			Log.i("MapSDWriter","New file created!" );
    			
		    } catch (IOException e) 
		    	{
    			e.printStackTrace();
    		    }
    		
    		}
    		else Log.i("MapSDWriter", "map already created");

	}
	
	
	
	
	
	

}
