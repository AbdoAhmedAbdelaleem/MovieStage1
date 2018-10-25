package com.example.abdo.moviesstage1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.appcompat.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = BuildConfig.API_Key;
    public String mostPopularityUri = "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY + "&language=en-US&page=1";
    public String topRatedURi = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY + "&language=en-US&page=1%20Code%20Generation";
    public String currentURi;
    private GridAdapter adapter;
    GridView gridView = null;
    Menu sortMenu = null;
    Context context;
    private ArrayList<MovieEntry> entries = new ArrayList<>();
    ProgressBar progressBar;
    RelativeLayout emptyGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        currentURi = mostPopularityUri;
        gridView = (GridView) findViewById(R.id.gridView);

        emptyGridView = (RelativeLayout) findViewById(R.id.emptyGridView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        entries = new ArrayList<>();
        new DataFetcher().execute(currentURi);
        adapter = new GridAdapter(this, entries);
        gridView.setAdapter(adapter);
        SetGridViewClick();
    }

    public void OnClickRetryButton(View view) {
        new DataFetcher().execute(currentURi);
    }

    private void AdjustMenuItemsVisibility() {
        MenuItem sortByPopularityItem = sortMenu.findItem(R.id.sortByPopularity);
        MenuItem sortByTopRatedItem = sortMenu.findItem(R.id.sortByTopRated);
        sortByPopularityItem.setVisible(!sortByPopularityItem.isVisible());
        sortByTopRatedItem.setVisible(!sortByTopRatedItem.isVisible());
    }

    private void SetGridViewClick() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //this using class inhert from application
//                Intent intent = new Intent(MainActivity.this, MovieDeatails.class);
//                SendDataHelper dataHelper = (SendDataHelper) getApplication();
//                dataHelper.setMovieEntry(entries.get(position));
//                startActivity(intent);

                MovieEntry dataToSend = entries.get(position);
                Intent i = new Intent(MainActivity.this, MovieDeatails.class);
                i.putExtra(MovieDeatails.DATA_EXTRA_KEY, dataToSend); // using the (String name, Parcelable value) overload!
                startActivity(i); // dataToSend is now passed

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menus, menu);
        sortMenu = menu;
        menu.findItem(R.id.sortByPopularity).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortByPopularity:
                currentURi = mostPopularityUri;
                break;
            case R.id.sortByTopRated:
                currentURi = topRatedURi;
                break;
        }
        AdjustMenuItemsVisibility();
        new DataFetcher().execute(currentURi);
        return true;
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo internetInfo = connectivityManager.getActiveNetworkInfo();
        return internetInfo != null && internetInfo.isConnectedOrConnecting();
    }

    private void DisplayData() {
        gridView.setVisibility(View.VISIBLE);
        emptyGridView.setVisibility(View.INVISIBLE);
        adapter = new GridAdapter(getBaseContext(), entries);
        gridView.setAdapter(adapter);
    }

    private void DisplayErrorFetchingData() {
        gridView.setVisibility(View.INVISIBLE);
        emptyGridView.setVisibility(View.VISIBLE);
    }

    class DataFetcher extends AsyncTask<String, Void, ArrayList<MovieEntry>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<MovieEntry> doInBackground(String... strings) {
            ArrayList<MovieEntry> moviesEntries = new ArrayList<>();
            if (strings.length <= 0 || !isOnline())
                return moviesEntries;
            try {
                String urlImage = "http://image.tmdb.org/t/p/w500/";
                String urlBackground = "http://image.tmdb.org/t/p/w1000/";
                URL moviesURL = NetworkUtils.getURL(strings[0]);
                String jsonFileDataString = NetworkUtils.ReadDataFromURL(moviesURL);
                JSONObject moviesJson = new JSONObject(jsonFileDataString);
                JSONArray moviesArray = moviesJson.getJSONArray("results");
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject currentArrayObject = moviesArray.getJSONObject(i);
                    long movieID = currentArrayObject.getLong("id");
                    String title = currentArrayObject.getString("title");
                    String movieOverview = currentArrayObject.getString("overview");
                    String releaseDate = currentArrayObject.getString("release_date");
                    String moviePosterPath = currentArrayObject.getString("poster_path");
                    double voteAverage = currentArrayObject.getDouble("vote_average");
                    String backdrop_path = currentArrayObject.getString("backdrop_path");
                    backdrop_path = urlBackground + backdrop_path;
                    if (moviePosterPath == null || moviePosterPath == "" || moviePosterPath.equalsIgnoreCase("null"))
                        continue;
                    moviePosterPath = urlImage + moviePosterPath;
                    // Bitmap posterIMG= Picasso.with(context).load(moviePosterPath).get();
                    // Bitmap backdropIMG= Picasso.with(context).load(backdrop_path).get();
                    if (title == null || title == "")
                        title = "";
                    if (releaseDate == null || releaseDate == "")
                        releaseDate = "";
                    if (movieOverview == null || movieOverview == "")
                        movieOverview = "";
                    MovieEntry movieEntry = new MovieEntry(movieID, title, moviePosterPath, movieOverview, releaseDate, voteAverage, backdrop_path, null, null);
                    moviesEntries.add(movieEntry);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return moviesEntries;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieEntry> movieEntries) {
            super.onPostExecute(movieEntries);
            progressBar.setVisibility(View.INVISIBLE);
            entries=movieEntries;
            if (movieEntries.size() == 0) {
                DisplayErrorFetchingData();
                return;
            }
            else
                DisplayData();


        }
    }
}
