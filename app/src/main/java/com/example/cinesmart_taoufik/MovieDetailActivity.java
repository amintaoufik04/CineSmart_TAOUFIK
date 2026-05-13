package com.example.cinesmart_taoufik;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.cinesmart_taoufik.api.AuthApi;
import com.example.cinesmart_taoufik.models.FavoriteRequest;
import com.example.cinesmart_taoufik.models.Movie;
import com.example.cinesmart_taoufik.utils.SessionManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        sessionManager = new SessionManager(this);
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        authApi = retrofit.create(AuthApi.class);

        ImageView posterImageView = findViewById(R.id.detail_movie_poster);
        TextView titleTextView = findViewById(R.id.detail_movie_title);
        TextView ratingTextView = findViewById(R.id.detail_movie_rating);
        TextView overviewTextView = findViewById(R.id.detail_movie_overview);
        Button btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        Button btnMap = findViewById(R.id.btn_map);
        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(MovieDetailActivity.this, NearbyCinemasActivity.class);
            startActivity(intent);
        });

        Movie movie = (Movie) getIntent().getSerializableExtra("movie");

        if (movie != null) {
            titleTextView.setText(movie.getTitle());
            ratingTextView.setText("Rating: " + movie.getVoteAverage());
            overviewTextView.setText(movie.getOverview());
            Glide.with(this)
                    .load(movie.getPosterPath())
                    .into(posterImageView);

            findViewById(R.id.btn_favorite).setOnClickListener(v -> {
                addToFavorites(movie);
            });
        }
    }

    private void addToFavorites(Movie movie) {
        String userId = sessionManager.getUserId();
        String token = sessionManager.getToken();
        if (userId == null || token == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String cleanPosterPath = movie.getPosterPath().replace("https://image.tmdb.org/t/p/w500", "");
        FavoriteRequest request = new FavoriteRequest(userId, movie.getId(), movie.getTitle(), cleanPosterPath);
        authApi.addFavorite("Bearer " + token, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MovieDetailActivity.this, "Ajouté aux favoris !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MovieDetailActivity.this, "Erreur favoris : " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this, "Erreur réseau favoris", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
