package com.example.cinesmart_taoufik;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinesmart_taoufik.adapters.MovieAdapter;
import com.example.cinesmart_taoufik.api.AuthApi;
import com.example.cinesmart_taoufik.models.FavoriteRequest;
import com.example.cinesmart_taoufik.models.Movie;
import com.example.cinesmart_taoufik.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        sessionManager = new SessionManager(this);
        progressBar = findViewById(R.id.progress_bar_fav);
        recyclerView = findViewById(R.id.recycler_view_fav);
        findViewById(R.id.btn_back_fav).setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdapter(new ArrayList<>(), movie -> {
            Intent intent = new Intent(FavoritesActivity.this, MovieDetailActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        authApi = retrofit.create(AuthApi.class);

        loadFavorites();
    }

    private void loadFavorites() {
        String userId = sessionManager.getUserId();
        String token = sessionManager.getToken();
        if (userId == null || token == null) {
            Toast.makeText(this, "Session expirée", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Log.d("FavDebug", "Appel favoris pour ID: " + userId);
        Log.d("FavDebug", "Token utilisé: " + token);

        authApi.getFavorites("Bearer " + token, userId).enqueue(new Callback<List<FavoriteRequest>>() {
            @Override
            public void onResponse(Call<List<FavoriteRequest>> call, Response<List<FavoriteRequest>> response) {
                progressBar.setVisibility(View.GONE);
                
                // AJOUT : Log de l'URL pour déboguer
                Log.d("FavDebug", "URL appelée : " + call.request().url());
                Log.d("FavDebug", "Code de réponse : " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> favoriteMovies = new ArrayList<>();
                    for (FavoriteRequest fav : response.body()) {
                        String path = fav.getPosterPath() != null ? fav.getPosterPath().replace("https://image.tmdb.org/t/p/w500", "") : "";
                        favoriteMovies.add(new Movie(
                                fav.getMovieId(),
                                fav.getTitle(),
                                path,
                                "No overview available",
                                0.0
                        ));
                    }
                    adapter.setMovies(favoriteMovies);
                } else {
                    String errorMsg = "Erreur " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " : " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String url = call.request().url().toString();
                    android.util.Log.e("FavDebug", "404 Error URL: " + url);
                    Toast.makeText(FavoritesActivity.this, "Erreur 404 sur l'URL : " + url, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteRequest>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(FavoritesActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
