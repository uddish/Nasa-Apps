package com.example.uddishverma22.nasaapps;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gelitenight.waveview.library.WaveView;
import com.github.mikephil.charting.charts.PieChart;

import org.json.JSONException;
import org.json.JSONObject;

import me.itangqi.waveloadingview.WaveLoadingView;

public class PieChartActivity extends AppCompatActivity {

    WaveView waveView;
    WaveLoadingView mWaveLoadingView;

    String url = "https://desolate-coast-42616.herokuapp.com/soil";
    String waterUrl = "https://desolate-coast-42616.herokuapp.com/soil/activate";
    String waterUrlDisconnect = "https://desolate-coast-42616.herokuapp.com/soil/deactivate";
    JSONObject jsonObject;
    String soilValue = null;

    Animation fadeInOne, fadeOutOne, fadeOutTwo, fadeOutThree;

    JsonObjectRequest request;
    private Runnable runnable;
    Handler handler;
    RequestQueue requestQueue;

    ImageView waterBtn, arrow1, arrow2, arrow3;

    TextView temp, soilState;

    public static final String TAG = "PieChartActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        mWaveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);

        temp = (TextView) findViewById(R.id.temperature);
        soilState = (TextView) findViewById(R.id.soil_state);
        waterBtn = (ImageView) findViewById(R.id.water_btn);

        arrow1 = (ImageView) findViewById(R.id.arrow_one);
        arrow2 = (ImageView) findViewById(R.id.arrow_two);
        arrow3 = (ImageView) findViewById(R.id.arrow_three);

        //Animating the arrows
        fadeInOne = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_one);
        fadeOutOne = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_one);
        fadeOutTwo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_two);
        fadeOutThree = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_three);
        arrow1.startAnimation(fadeOutOne);
        arrow2.startAnimation(fadeOutTwo);
        arrow3.startAnimation(fadeOutThree);

        waterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateWater();
            }
        });

        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Nunito-Regular.ttf");
        Typeface tf1 = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Regular.ttf");
        temp.setTypeface(tf1);
        soilState.setTypeface(tf);

        mWaveLoadingView.startAnimation();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                sendRequest();

                handler.postDelayed(runnable, 5000);
            }
        };
        handler.post(runnable);

    }

    private void activateWater() {

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, waterUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: water function activated");

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.toString());
            }
        });
        requestQueue.add(request);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, waterUrlDisconnect, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "onResponse: water function activated");

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.toString());
                    }
                });
                requestQueue.add(request);
            }
        }, 5000);
    }

    private void sendRequest() {

        request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Integer soilVal = response.getInt("value");
                            Log.d(TAG, "onResponse: VALUE " + soilVal);
                            if (soilVal == 0) {
                                mWaveLoadingView.setProgressValue(15);
                                mWaveLoadingView.setWaveColor(Color.RED);
                                Toast.makeText(PieChartActivity.this, "Sensor is not connected", Toast.LENGTH_SHORT).show();
                            } else if (soilVal == 1) {
                                soilState.setText("Dry Soil");
                                waterBtn.setVisibility(View.VISIBLE);
                                mWaveLoadingView.setProgressValue(15);
//                                mWaveLoadingView.setCenterTitle("Dry");
                            } else if (soilVal == 2) {
                                soilState.setText("Humid");
                                mWaveLoadingView.setProgressValue(50);
                                waterBtn.setVisibility(View.VISIBLE);
                            } else if (soilVal == 3) {
                                soilState.setText("Wet Soil");
                                waterBtn.setVisibility(View.INVISIBLE);
                                mWaveLoadingView.setProgressValue(80);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.toString());

            }
        });
        requestQueue.add(request);
    }
}
