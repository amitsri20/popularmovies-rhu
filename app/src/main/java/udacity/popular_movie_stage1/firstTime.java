package udacity.popular_movie_stage1;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rk on 6/7/17.
 */

public class firstTime {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    private int PRIVATE_MODE = 0;

    public firstTime(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("PopularMovie",PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void setFirstTime(boolean firstTime){
        editor.putBoolean("isFirstTimeLaunched",firstTime);
        editor.commit();
    }

    public boolean isFirstTime(){
        return sharedPreferences.getBoolean("isFirstTimeLaunched",true);
    }
}
