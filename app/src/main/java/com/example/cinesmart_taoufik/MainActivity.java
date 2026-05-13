package com.example.cinesmart_taoufik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinesmart_taoufik.adapters.MovieAdapter;
import com.example.cinesmart_taoufik.api.TmdbApi;
import com.example.cinesmart_taoufik.models.Movie;
import com.example.cinesmart_taoufik.models.MovieResponse;
import com.google.android.material.chip.ChipGroup;

import com.example.cinesmart_taoufik.utils.SessionManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "a71c7e06762048e24e3988cd4bcb22d4";
    private TmdbApi tmdbApi;
    private MovieAdapter adapter;
    private EditText searchEditText;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        if (sessionManager.getUserId() == null || sessionManager.getToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        searchEditText = findViewById(R.id.search_edit_text);
        Button searchButton = findViewById(R.id.search_button);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        ChipGroup chipGroup = findViewById(R.id.chip_group_categories);
        progressBar = findViewById(R.id.progress_bar);
        
        findViewById(R.id.btn_view_favorites).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
        });

        findViewById(R.id.btn_ask_me).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ChatbotActivity.class));
        });

        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new MovieAdapter(new ArrayList<>(), movie -> {
            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        tmdbApi = retrofit.create(TmdbApi.class);

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchMovies(query);
            }
        });

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chip_popular) {
                fetchMovies(tmdbApi.getPopularMovies(API_KEY));
            } else if (checkedId == R.id.chip_top_rated) {
                fetchMovies(tmdbApi.getTopRatedMovies(API_KEY));
            } else if (checkedId == R.id.chip_upcoming) {
                fetchMovies(tmdbApi.getUpcomingMovies(API_KEY));
            }
        });

        // Charger les films populaires par défaut
        fetchMovies(tmdbApi.getPopularMovies(API_KEY));
    }

    private void fetchMovies(Call<MovieResponse> call) {
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setMovies(response.body().getMovies());
                } else {
                    Toast.makeText(MainActivity.this, "Erreur API : " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchMovies(String query) {
        fetchMovies(tmdbApi.searchMovies(API_KEY, query));
    }
}
