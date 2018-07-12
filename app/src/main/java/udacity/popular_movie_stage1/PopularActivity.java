package udacity.popular_movie_stage1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PopularActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private cardAdapter adapter;
    cardModel card;
    Toolbar toolbar;
    Integer date;
    private String DATA_URL = "https://api.themoviedb.org/3/movie/popular?api_key=3a19341c015b47b8cb85017aaca0675d&language=en-US&page=1";
    private URL url;
    private List<cardModel> modelList= new ArrayList<>();;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new cardAdapter(getApplicationContext(),modelList);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Most Popular Movies");
        setSupportActionBar(toolbar);

//        new AsyncTaskRunner().execute("https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=3a19341c015b47b8cb85017aaca0675d");
        getData();



        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        ViewCompat.setNestedScrollingEnabled(recyclerView,true);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){

            case R.id.share:

                Intent intent = new Intent(this,PopularActivity.class);
                startActivity(intent);

            case R.id.action_settings :


                Intent top = new Intent(this,TopRatedActivity.class);
                startActivity(top);


        }
        return super.onOptionsItemSelected(item);
    }

    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int count,space;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int count, int space, boolean includeEdge) {
            this.count = count;
            this.space = space;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % count;

            if(includeEdge){
                outRect.left = space - column * space / count;
                outRect.right = (column+1)* space / count;

                if(position < count){
                    outRect.top = space;
                }
                outRect.bottom = space;
            }else{
                outRect.left = column*space / count;
                outRect.right = space - (column+1)*space/count;

                if(position > count){
                    outRect.top = space;
                }
            }

        }
    }

    private int dpToPx(int i) {
        Resources res = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, res.getDisplayMetrics()));
    }



    public void getData(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(SignIn.this,response,Toast.LENGTH_LONG).show();

                        modelList.clear();
                        try {
                            JSONObject object = new JSONObject(response);

                            JSONArray jsonArray = object.getJSONArray("results");

                            for(int i=0; i<jsonArray.length() ;i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                cardModel card = new cardModel();
                                card.setPoster_path("http://image.tmdb.org/t/p/w500/" +jsonObject.getString("poster_path"));
                                card.setOriginal_title(jsonObject.getString("title"));
                                card.setRelease_date(jsonObject.getString("release_date"));
                                card.setVote_average(jsonObject.getInt("vote_average"));
                                card.setOverview(jsonObject.getString("overview"));
                                card.setPopularity(jsonObject.getInt("popularity"));

                                modelList.add(card);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(),"Time Out"+error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
