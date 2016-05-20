package com.example.aleksandrp.weatherthreecities;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.aleksandrp.weatherthreecities.api.LoadParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private ImageView mIcon;
    private TextView mCity, mTemperature, mMinTemp, mMaxTemp, mState;
    public ProgressBar mProgressBar;

    public static final String
            ID_DNEPR = "709930",
            ID_NIKOPOL = "700051",
            ID_KIEV = "703448";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUi();

        setParams(ID_DNEPR);
    }

    /**
     * init ui
     */
    private void setUi() {
        mIcon = (ImageView) findViewById(R.id.iv_icon_weather);
        mCity = (TextView) findViewById(R.id.tv_title_city);
        mTemperature = (TextView) findViewById(R.id.tv_value_temperature);
        mMinTemp = (TextView) findViewById(R.id.tv_min_temp);
        mMaxTemp = (TextView) findViewById(R.id.tv_max_temp);
        mState = (TextView) findViewById(R.id.tv_state);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    private void setParams(String id) {
        if (isInternetOn()) {
            LoadParams mLoadParams = new LoadParams(MainActivity.this, id);
            mLoadParams.execute();
        }
    }


    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet
            return true;

        } else if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            Snackbar.make(mIcon, R.string.check_internet, Snackbar.LENGTH_SHORT).show();

            return false;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.dnepr:
                setParams(ID_DNEPR);
                break;
            case R.id.nikopol:
                setParams(ID_NIKOPOL);
                break;
            case R.id.kiev:
                setParams(ID_KIEV);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * method return params
     * params:
     * 0 - temperature
     * 1 - minTemp
     * 2 - maxTenp
     * 3 - state
     * 4 - path to icon
     *
     * @return
     */
    public void setParamsFromLoader(String mCity, String[] mParams) {
        this.mCity.setText(mCity);
        mTemperature.setText(mParams[0]);
        mMinTemp.setText(mParams[1]);
        mMaxTemp.setText(mParams[2]);
        mState.setText(mParams[3]);

        Picasso.with(MainActivity.this)
                .load(mParams[4])
                .into(mIcon);
    }

}
