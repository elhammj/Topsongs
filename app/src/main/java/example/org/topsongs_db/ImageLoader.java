package example.org.topsongs_db;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ImageLoader {

	public static int downloadedImageCount =0;
	//Create Map (collection) to store image and image url in key value pair
	private Map<ImageView, TopSong> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, TopSong>());
	ExecutorService executorService;

	//handler to display images in UI thread
	Handler handler = new Handler();

	private TextView imageCountTV;
	public ImageLoader(Context context, TextView imageCountTV){
		// Creates a thread pool that reuses a fixed number of
		// threads operating off a shared unbounded queue.
		executorService=Executors.newFixedThreadPool(5);
		this.imageCountTV = imageCountTV;

	}

	// default image show in list (Before online image download)
	int stub_id=0;

	public void DisplayImage(TopSong topSong, ImageView imageView)
	{
		stub_id =R.drawable.temp_img;
		//Store image and url in Map
		imageViews.put(imageView, topSong);

		//queue Photo to download from url
		queuePhoto(topSong, imageView);
		//Before downloading image show default image
		imageView.setImageResource(stub_id);


	}

	private void queuePhoto(TopSong topsong, ImageView imageView)
	{
		// Store image and url in PhotoToLoad object
		PhotoToLoad p = new PhotoToLoad(topsong, imageView);

		// pass PhotoToLoad object to PhotosLoader runnable class
		// and submit PhotosLoader runnable to executers to run runnable
		// Submits a PhotosLoader runnable task for execution  

		executorService.submit(new PhotosLoader(p));
	}

	//Task for the queue
	private class PhotoToLoad
	{
		public TopSong topSong;
		public ImageView imageView;
		public PhotoToLoad(TopSong topSong, ImageView i){
			this.topSong=topSong;
			imageView=i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad){
			this.photoToLoad=photoToLoad;
		}

		@Override
		public void run() {
			try{

				// download image from web url
				Bitmap bmp = getBitmap(photoToLoad.topSong);

				photoToLoad.topSong.setImageData(bmp);
				// set image data in Memory Cache
				DatabaseManager.getInstance().saveImageData(photoToLoad.topSong);

				// Get bitmap to display
				BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);

				// Causes the Runnable bd (BitmapDisplayer) to be added to the message queue. 
				// The runnable will be run on the thread to which this handler is attached.
				// BitmapDisplayer run method will call
				handler.post(bd);

			}catch(Throwable th){
				th.printStackTrace();
			}
		}
	}

	private Bitmap getBitmap(TopSong topSong)
	{

		// Download image file from web
		try {

			Bitmap bitmap=null;
			try {
				// Download the image
				URL url = new URL(topSong.getImage());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream is = connection.getInputStream();
				// Decode image to get smaller image to save memory
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inSampleSize=1;
				bitmap = BitmapFactory.decodeStream(is, null, options);
				is.close();
			}
			catch(IOException e){
				return null;
			}
			return bitmap;

		} catch (Throwable ex) {
			return null;
		}
	}


	//Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable
	{
		Bitmap bitmap;
		PhotoToLoad photoToLoad;
		public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
		public void run()
		{
			if(downloadedImageCount < 25) {
				++downloadedImageCount;
				imageCountTV.setText("Image Downloaded: " + ImageLoader.downloadedImageCount);
			}

			// Show bitmap on UI
			if(bitmap!=null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageResource(stub_id);
		}
	}

}
