package example.org.topsongs_db;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * The Class AlbumSong.
 * this class contains the attribute required to parse the response from apis 
 */
public class AlbumSong  {

	/** The feed. */
	private Feed feed;

	/**
	 * Gets the feed.
	 *
	 * @return the feed
	 */
	public Feed getFeed() {
		return feed;
	}

	/**
	 * Sets the feed.
	 *
	 * @param feed the new feed
	 */
	public void setFeed(Feed feed) {
		this.feed = feed;
	}
}


class Feed  {

	private List<Entry> entry;

	public List<Entry> getEntry() {
		return entry;
	}

	public void setEntry(List<Entry> entry) {
		this.entry = entry;
	}
}

class Entry {

	Map<String, String> title;

	/** The label. */
	String label;

	String artist;

	@SerializedName("im:image")
	Image [] images;

	private Id id;

	public Map<String, String> getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the title
	 */
	public void setTitle(Map<String, String> title) {
		this.title = title;
	}

	public String getLabel() {
		return title.get("label").substring(0, title.get("label").lastIndexOf("-")).trim();
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getArtist() {
		return title.get("label").substring(title.get("label").lastIndexOf("-")+1).trim();
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public Image[] getImages() {
		return images;
	}

	public void setImages(Image[] images) {
		this.images = images;
	}

	public Id getId() {
		return id;
	}

	public void setId(Id id) {
		this.id = id;
	}

	class Image {

		private  String label;

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}
	}

	class Id {

		private Attributes attributes;

		public Attributes getAttributes() {
			return attributes;
		}

		public void setAttributes(Attributes attributes) {
			this.attributes = attributes;
		}
	}

	class Attributes {

		@SerializedName("im:id")
		private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}
}