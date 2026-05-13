package com.example.cinesmart_taoufik;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinesmart_taoufik.adapters.ChatAdapter;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatbotActivity extends AppCompatActivity {

    private static final String OLLAMA_URL = "http://10.0.2.2:11434/api/generate";
    private static final String MODEL = "tinyllama:1.1b";

    private RecyclerView chatRecyclerView;
    private EditText chatInput;
    private Button btnSend;
    private ProgressBar progressBar;
    private ChatAdapter chatAdapter;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        chatInput = findViewById(R.id.chat_input);
        btnSend = findViewById(R.id.btn_send);
        progressBar = findViewById(R.id.chat_progress_bar);

        findViewById(R.id.btn_back_fav).setOnClickListener(v -> finish());

        client = new OkHttpClient.Builder()
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        chatAdapter = new ChatAdapter(new ArrayList<>());
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        btnSend.setOnClickListener(v -> sendMessage());

        chatInput.setOnClickListener(v -> {
            if (!chatInput.isEnabled()) {
                Toast.makeText(this, "Veuillez attendre la réponse...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String message = chatInput.getText().toString().trim();
        if (message.isEmpty()) return;

        chatAdapter.addMessage(message, true);
        chatInput.setText("");
        chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);

        progressBar.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);
        chatInput.setEnabled(false);

        new Thread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("model", MODEL);
                json.put("prompt", message);
                json.put("stream", false);

                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                        .url(OLLAMA_URL)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body() != null ? response.body().string() : "";

                JSONObject responseJson = new JSONObject(responseBody);
                String botReply = responseJson.optString("response", "Désolé, je n'ai pas pu répondre.");

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnSend.setEnabled(true);
                    chatInput.setEnabled(true);
                    chatAdapter.addMessage(botReply, false);
                    chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                });

            } catch (IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnSend.setEnabled(true);
                    chatInput.setEnabled(true);
                    chatAdapter.addMessage("Erreur de connexion à Ollama : " + e.getMessage(), false);
                    chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnSend.setEnabled(true);
                    chatInput.setEnabled(true);
                    chatAdapter.addMessage("Erreur : " + e.getMessage(), false);
                    chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                });
            }
        }).start();
    }
}
