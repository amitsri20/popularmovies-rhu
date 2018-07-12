package udacity.popular_movie_stage1;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {


    TextView name, overview, vote_average,release_date,author,content;
    private cardAdapter adapter;
    favourite db;
    ImageView poster;
    Bitmap mIcon_val;
    Toolbar toolbar;
    MainActivity mainActivity;
    private ImageButton button, favourite;
    String Name, Overview, Vote, Release,img,id, key;
    private List<cardModel> modelList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        db = new favourite(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Movie Details");
        setSupportActionBar(toolbar);

        adapter = new cardAdapter(getApplicationContext(),modelList);


        name = (TextView)findViewById(R.id.name);
        overview = (TextView)findViewById(R.id.overview);
        vote_average = (TextView)findViewById(R.id.movieTitle);
        release_date = (TextView)findViewById(R.id.exp);
        poster = (ImageView)findViewById(R.id.cardImg);
        button =(ImageButton) findViewById(R.id.play);
        author = (TextView)findViewById(R.id.author);
        content = (TextView)findViewById(R.id.content);
        favourite = (ImageButton)findViewById(R.id.bookmark);



        id = getIntent().getStringExtra("id");
        Name = getIntent().getStringExtra("title");
        img = getIntent().getStringExtra("poster_path");
        Release = getIntent().getStringExtra("release_date");
        Vote = getIntent().getStringExtra("vote_average");
        Overview = getIntent().getStringExtra("overview");


        // Read Particular user using ID
//        readSingleUser();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchYoutubeVideo(getApplicationContext());
            }
        });


        favourite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isFavourite = readState();

                if (isFavourite) {
                    favourite.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                    Toast.makeText(getApplicationContext(),"Add To Favourite",Toast.LENGTH_LONG).show();
                    isFavourite = false;
                    saveState(isFavourite);

                } else {
                    favourite.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                    isFavourite = true;
                    saveState(isFavourite);

                }

            }
        });



        getData();
        getReview();

        Log.e("id",":::::"+id);
        name.setText(Name);


        URL newurl = null;
        try {
            newurl = new URL(img);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


        poster.setImageBitmap(mIcon_val);
        vote_average.setText("Rating :- "+Vote);
        release_date.setText("Date :- "+Release);
        overview.setText(Overview);
    }


    private void getAllUsers(){
        Log.d("Reading: ", "Reading all user..");
        List<FavouriteModel> user = db.getAllUsers();

        for (FavouriteModel data : user) {
            String log = "Id: "+data.getId()+" , Name: " + data.getTitle() + " , Phone: " + data.getIsFavourite();
            // Writing user to log
            Log.e("USER: ", log);

        }
    }

//    private void readSingleUser(){
//        Log.d("Reading: ", "Reading single user.. UserID:3");
//        FavouriteModel singleUser = db.getUser(3);
//        String log = "Id: "+singleUser.getID()+" , Name: " + singleUser.getName() + " , Phone: " + singleUser.getPhoneNumber();
//        Log.e("USER: ", log);
//    }


    private void saveState(boolean isFavourite) {
        SharedPreferences aSharedPreferences = this.getSharedPreferences(
                "Favourite", Context.MODE_PRIVATE);
        SharedPreferences.Editor aSharedPreferencesEdit = aSharedPreferences
                .edit();
        aSharedPreferencesEdit.putBoolean("State", isFavourite);
        aSharedPreferencesEdit.commit();
    }

    private boolean readState() {
        getAllUsers();
        SharedPreferences aSharedPreferences = this.getSharedPreferences(
                "Favourite", Context.MODE_PRIVATE);
        return aSharedPreferences.getBoolean("State", true);
    }




    public void getData(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api.themoviedb.org/3/movie/"+id+"/videos?api_key=3a19341c015b47b8cb85017aaca0675d&language=en-US",
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
                                for(int j=0;j<jsonObject.length(); j++) {

                                    card.setKey(jsonObject.getString("key"));


                                    key = card.getKey();
                                }
                                Log.e("KEY","::::::"+key);
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

                        Log.e("ERROR",">>>>>>"+error.getMessage());
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




    public void getReview(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api.themoviedb.org/3/movie/"+id+"/reviews?api_key=3a19341c015b47b8cb85017aaca0675d&language=en-US&page=1",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        modelList.clear();
                        try {
                            JSONObject object = new JSONObject(response);

                            JSONArray jsonArray = object.getJSONArray("results");

                            for(int i=0; i<jsonArray.length() ;i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                cardModel card = new cardModel();

                                    card.setAuthor(jsonObject.getString("author"));
                                    card.setContent(jsonObject.getString("content"));

                                modelList.add(card);

                                author.setText("Author :- "+card.getAuthor());
                                content.setText(card.getContent());

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

                        Log.e("ERROR",">>>>>>"+error.getMessage());
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


    public void watchYoutubeVideo(Context context){
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + key)));
            } catch (android.content.ActivityNotFoundException anfe) {
                viewInBrowser(context, "https://www.youtube.com/watch?v=" + key);
            }
        }

        public static void viewInBrowser(Context context, String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (null != intent.resolveActivity(context.getPackageManager())) {
                context.startActivity(intent);
            }
        }

    }




//            holder.favButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
//@Override
//public void onCheckedChanged(CompoundButton favButton, boolean isChecked){
//        if (isChecked)
//        favButton.setBackgroundDrawable(ContextCompat.getDrawable(favButton.getContext(),R.mipmap.ic_launcher));
//
//        else
//        favButton.setBackgroundDrawable(ContextCompat.getDrawable(favButton.getContext(), R.mipmap.ic_cart));
//        }
//        });
//        }


