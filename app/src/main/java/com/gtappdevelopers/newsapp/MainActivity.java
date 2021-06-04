package com.gtappdevelopers.newsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements CategoryRVAdapter.CategoryClickInterface {

    private RecyclerView newsRV, categoriesRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles> articles;
    private ArrayList<CategoryRVModal> categoryRVModals;
    private CategoryRVAdapter categoryRVAdapter;
    private NewsRVAapter newsRVAapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsRV = findViewById(R.id.idRVNews);
        categoriesRV = findViewById(R.id.idRVCategory);
        loadingPB = findViewById(R.id.idPBLoading);
        loadingPB.setVisibility(View.VISIBLE);
        articles = new ArrayList<>();
        categoryRVModals = new ArrayList<>();
        newsRVAapter = new NewsRVAapter(articles, this);
        categoryRVAdapter = new CategoryRVAdapter(categoryRVModals, this, this::onCategoryClick);
        newsRV.setLayoutManager(new LinearLayoutManager(this));
        newsRV.setAdapter(newsRVAapter);
        categoriesRV.setAdapter(categoryRVAdapter);
        getCategories();
        getNewsByRetrofit("All");
        newsRVAapter.notifyDataSetChanged();

    }

    private void getCategories() {
        categoryRVModals.add(new CategoryRVModal("All", "https://images.unsplash.com/photo-1565453006698-a17d83b9e2af?ixid=MnwxMjA3fDB8MHxzZWFyY2h8MjB8fG5ld3NwYXBlcnxlbnwwfHwwfHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"));
        categoryRVModals.add(new CategoryRVModal("Technology", "https://images.unsplash.com/photo-1504164996022-09080787b6b3?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"));
        categoryRVModals.add(new CategoryRVModal("Science", "https://images.unsplash.com/photo-1567427018141-0584cfcbf1b8?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"));
        categoryRVModals.add(new CategoryRVModal("Sports", "https://images.unsplash.com/photo-1530549387789-4c1017266635?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"));
        categoryRVModals.add(new CategoryRVModal("General", "https://images.unsplash.com/photo-1513151233558-d860c5398176?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"));
        categoryRVModals.add(new CategoryRVModal("Business", "https://images.unsplash.com/photo-1444653614773-995cb1ef9efa?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"));
        categoryRVModals.add(new CategoryRVModal("Entertainment", "https://images.unsplash.com/photo-1499364615650-ec38552f4f34?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"));
        categoryRVModals.add(new CategoryRVModal("Health", "https://images.unsplash.com/photo-1527613426441-4da17471b66d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"));
        categoryRVAdapter.notifyDataSetChanged();
    }

    private void getNewsByRetrofit(String category) {
        loadingPB.setVisibility(View.VISIBLE);
        articles.clear();
        String categoryURL = "http://newsapi.org/v2/top-headlines?country=in&category=" + category + "&apiKey=Enter your api key";
        String url = "http://newsapi.org/v2/top-headlines?country=in&excludeDomains=stackoverflow.com&sortBy=publishedAt&language=en&apiKey=Enter your api key";
        String BASE_URL = "http://newsapi.org/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<NewsModal> call;
        if (category.equals("All")) {
            call = retrofitAPI.getALlNews(url);
        } else {
            call = retrofitAPI.getALlNews(categoryURL);
        }

        call.enqueue(new Callback<NewsModal>() {
            @Override
            public void onResponse(Call<NewsModal> call, retrofit2.Response<NewsModal> response) {
                NewsModal newsModal = response.body();
                loadingPB.setVisibility(View.GONE);
                ArrayList<Articles> articlesArrayList = newsModal.getArticles();
                for (int i = 0; i < articlesArrayList.size(); i++) {
                    articles.add(new Articles(articlesArrayList.get(i).getTitle(), articlesArrayList.get(i).getDescription(), articlesArrayList.get(i).getUrlToImage(), articlesArrayList.get(i).getUrl(), articlesArrayList.get(i).getContent()));
                }
                newsRVAapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<NewsModal> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fail to get response..", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onCategoryClick(int position) {
        String category = categoryRVModals.get(position).getCategory();
        getNewsByRetrofit(category);
    }
}