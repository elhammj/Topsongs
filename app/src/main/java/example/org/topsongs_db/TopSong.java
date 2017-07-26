package example.org.topsongs_db;

import android.graphics.Bitmap;

/**
 * The Class TopSong.
 * this class contains the attribute which is required to display the top songs from top albums
 */
public class TopSong {

	private int id;

	/** The song. */
	private String song;

	/** The image. */
	private Bitmap imageData;

	private String image;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSong() {
		return song;
	}

	public void setSong(String song) {
		this.song = song;
	}

	public Bitmap getImageData() {
		return imageData;
	}

	public void setImageData(Bitmap imageData) {
		this.imageData = imageData;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
