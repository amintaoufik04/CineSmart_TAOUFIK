package com.example.cinesmart_taoufik;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.cinesmart_taoufik.models.Movie;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ImageView posterImageView = findViewById(R.id.detail_movie_poster);
        TextView titleTextView = findViewById(R.id.detail_movie_title);
        TextView ratingTextView = findViewById(R.id.detail_movie_rating);
        TextView overviewTextView = findViewById(R.id.detail_movie_overview);
        Button btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        Movie movie = (Movie) getIntent().getSerializableExtra("movie");

        if (movie != null) {
            titleTextView.setText(movie.getTitle());
            ratingTextView.setText("Rating: " + movie.getVoteAverage());
            overviewTextView.setText(movie.getOverview());
            Glide.with(this)
                    .load(movie.getPosterPath())
                    .into(posterImageView);
        }
    }
}
