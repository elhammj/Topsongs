package example.org.topsongs_db;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AppActivity extends AppCompatActivity {
    /**
     * The top songs.
     */
    private List<TopSong> topSongs = new ArrayList<TopSong>();

    /**
     * The songs list.
     */
    private RecyclerView songsList;

    /**
     * The adapter.
     */
    private SongListAdapter adapter;

    private TextView imageCountTV;

    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        songsList = (RecyclerView) findViewById(R.id.song_list);
        imageCountTV = (TextView) findViewById(R.id.tv_image_count);
        new FetchTask().execute();

        databaseManager = DatabaseManager.getInstance();

        refreshRecyclerView();
    }


    /**
     * The Class SongListAdapter.
     */
    class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.Holder> {

        /**
         * The Class Holder.
         */
         class Holder extends RecyclerView.ViewHolder  {
            /**
             * The image tv.
             */
            private ImageView songIV;

            /**
             * The song tv.
             */
            private TextView songTV;

            public Holder(View itemView) {
                super(itemView);
                songTV = (TextView) itemView.findViewById(R.id.tv_song);
                songIV = (ImageView) itemView.findViewById(R.id.imageView);
            }

        }
        private ImageLoader imageLoader;
        public  SongListAdapter(){
            imageLoader = new ImageLoader(getApplicationContext(), imageCountTV);
            topSongs = DatabaseManager.getInstance().getTopSongsList();

        }

        @Override
        public int getItemCount() {
            return topSongs.size();
        }


        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.song_detail_row, parent, false);

            Holder holder = new Holder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.songTV.setText(topSongs.get(position).getSong());
            if(topSongs.get(position).getImageData() != null) {
                holder.songIV.setImageBitmap(topSongs.get(position).getImageData());
                if(ImageLoader.downloadedImageCount < 25) {
                    ++ImageLoader.downloadedImageCount;
                    imageCountTV.setText("Image Downloaded: " + ImageLoader.downloadedImageCount);
                }
            }
            else {
                imageLoader.DisplayImage(topSongs.get(position), holder.songIV);
            }
        }

    }

    /**
     * The Class FetchTask.
     * perform the background network task
     */
    class FetchTask extends AsyncTask<Void, Void, Void> {

        /**
         * perform the background operation
         */
        @Override
        protected Void doInBackground(Void... params) {
            //AlbumSong album = getTopAlbums();
            AlbumSong song = getTopSongs();
            topSongs = getTopSongsList(song);
            DatabaseManager.getInstance().saveSongList(topSongs);
            return null;
        }

        /**
         * executed when execution of doIn background finishes.
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            refreshRecyclerView();
        }
    }

    private void refreshRecyclerView(){
        adapter = new SongListAdapter();
        songsList.setAdapter(adapter);
        songsList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AppActivity.this);
        songsList.setLayoutManager(mLayoutManager);
        adapter.notifyDataSetChanged();
    }

    /**
     * Gets the top songs list.
     *
     * @param song the song
     * @return the top songs list
     */
    private List<TopSong> getTopSongsList(AlbumSong song) {
        List<TopSong> topSongList = new ArrayList<TopSong>();
        if (song != null) {
            for (Entry songEntry : song.getFeed().getEntry()) {
                TopSong topSong = new TopSong();
                topSong.setId(songEntry.getId().getAttributes().getId());
                topSong.setSong(songEntry.getLabel());
                topSong.setImage(songEntry.getImages() != null && songEntry.getImages().length > 0 ? songEntry.getImages()[songEntry.getImages().length-1].getLabel() : "");
                topSongList.add(topSong);
            }
        }
        return topSongList;
    }

    /**
     * Gets the top songs.
     *
     * @return the top songs
     */
    public AlbumSong getTopSongs() {
        AlbumSong song = null;
        StringBuffer stringBuffer = new StringBuffer("");
        try {
            URL url = new URL("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=25/json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                InputStream is = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(is));
                String readLine = null;
                while (((readLine = bufferedReader.readLine()) != null)) {
                    stringBuffer.append(readLine);
                }
                conn.disconnect();
                is.close();
                song = new Gson().fromJson(stringBuffer.toString(), AlbumSong.class);
            }
        } catch (Exception e) {
        }
        return song;
    }

    @Override
    protected void onDestroy() {
        ImageLoader.downloadedImageCount =0;
        super.onDestroy();
    }

}
