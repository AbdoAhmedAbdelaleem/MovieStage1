package com.example.abdo.moviesstage1;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDeatails extends AppCompatActivity {

    public static final String DATA_EXTRA_KEY="myDataKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);
        TextView OvervieTextView= (TextView) findViewById(R.id.OverView);
        TextView releaseDateTextview= (TextView) findViewById(R.id.RealeaseDate);
        TextView VoteAverageTextView= (TextView) findViewById(R.id.VoteAverage);
        Intent intent=getIntent();
        if(intent.hasExtra(DATA_EXTRA_KEY)) {
            MovieEntry object = (MovieEntry) intent.getParcelableExtra(DATA_EXTRA_KEY);
            MovieEntry movieEntry = object;
            if (movieEntry != null) {
                OvervieTextView.setText(movieEntry.getMovieOverview());
                releaseDateTextview.setText(movieEntry.getMovieRelease_date());
                VoteAverageTextView.setText(movieEntry.getMovieVoteAverage() + "");
                ImageView backDropPathImgView = (ImageView) findViewById(R.id.BackDopPathImgView);
                ImageView posterImgView = (ImageView) findViewById(R.id.posterImgView);
                Picasso.with(this).load(movieEntry.moviePosterPath).into(posterImgView);
                Picasso.with(this).load(movieEntry.getMovieBackdropPath()).into(backDropPathImgView);
            }
        }
    }

}
