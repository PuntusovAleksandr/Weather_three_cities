package com.edu.aleksandrp.weatherthreecities.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.edu.aleksandrp.weatherthreecities.MainActivity;
import com.edu.aleksandrp.weatherthreecities.R;

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

/**
 * Created by AleksandrP on 21.05.2016.
 */
public class LoadParams extends AsyncTask<Object, Object, Object> {

    private String mId, mRequest, TAG = "TAG";
    private Context mContext;
    MainActivity mMainActivity;

    public LoadParams(Context mContext, String mId) {
        this.mContext = mContext;
        this.mId = mId;
        this.mMainActivity = (MainActivity) mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mMainActivity.mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            mRequest = doRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        mMainActivity.mProgressBar.setVisibility(View.GONE);
        Log.i(TAG, "START" + mRequest);
        if (!mRequest.isEmpty()) {
            setParamsToUi();
            Log.i(TAG, "FINISH" + mRequest);
        }
    }


    private void setParamsToUi() {
        String city = "";
        if (mId.equals(MainActivity.ID_DNEPR)) {
            city = mContext.getString(R.string.dnepr);
        } else if (mId.equals(MainActivity.ID_NIKOPOL)) {
            city = mContext.getString(R.string.nikopol);
        } else city = mContext.getString(R.string.kiev);

        String[] mParams = new String[0];
        try {
            mParams = getParamsFromApi();
        } catch (JSONException mE) {
            mE.printStackTrace();
        }

        mMainActivity.setParamsFromLoader(city, mParams);
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
    private String[] getParamsFromApi() throws JSONException {
        JSONObject jObj = new JSONObject(mRequest);
        JSONObject jItem = jObj.getJSONArray("list").getJSONObject(0);
        JSONObject mMain = jItem.getJSONObject("main");
        JSONArray jWeather = jItem.getJSONArray("weather");

        String temperature = translateToCelso(mMain.getString("temp"));
        String temp_min = translateToCelso(mMain.getString("temp_min"));
        String temp_max = translateToCelso(mMain.getString("temp_max"));
        String description = jWeather.getJSONObject(0).getString("description");
        String icon = jWeather.getJSONObject(0).getString("icon");

        String[] params = new String[5];
        params[0] = temperature;
        params[1] = temp_min;
        params[2] = temp_max;
        params[3] = description;
        params[4] = mContext.getString(R.string.url_icon) + icon + ".png";
        return params;
    }

    private String translateToCelso(String mTemp) {
        int mValue = (int) (Float.parseFloat(mTemp) - 273);
        String mValueOf;
        if (mValue < 0) {
            mValueOf = "-";
        } else if (mValue > 0) {
            mValueOf = "+";
        } else mValueOf = "";
        mValueOf = mValueOf + String.valueOf(mValue);
//        mValueOf = Math.round((int) (Float.parseFloat(mTemp) - 273.15)) + "�C";
        return mValueOf;
    }

    /**
     * @return responce from server
     */
    String doRequest() throws UnsupportedEncodingException {

        String data = URLEncoder.encode("id", "UTF-8")
                + "=" + URLEncoder.encode(mId, "UTF-8");

        data += "&" + URLEncoder.encode("APPID", "UTF-8")
                + "=" + URLEncoder.encode(mContext.getString(R.string.api_weather_key), "UTF-8");

        String text = "";
        BufferedReader reader = null;

        // Send data
        try {
            // Defined URL  where to send data
            URL url = new URL(mContext.getString(R.string.general_url) + data);
            // Send GGT data request
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET");
            connect.setRequestProperty("Content-length", "0");
            connect.setUseCaches(false);
            connect.setAllowUserInteraction(false);
            connect.setConnectTimeout(3000);
            connect.setReadTimeout(3000);
            connect.connect();

            // Get the server response
            int status = connect.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    // Read Server Response
                    reader = new BufferedReader(new InputStreamReader(
                            connect.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    text = sb.toString();
                    reader.close();
            }
        } catch (MalformedURLException ex) {

        } catch (IOException ex) {

        }

        return text;
    }
}
