package com.example.aleksandrp.weatherthreecities;

import android.net.ConnectivityManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


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
    private ProgressBar mProgressBar;

    private final String
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
            LoadParams mLoadParams = new LoadParams(id);
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


    private class LoadParams extends AsyncTask<Object, Object, Object> {

        private String mId, mRequest, TAG = "TAG";

        public LoadParams(String mId) {
            this.mId = mId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
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
            mProgressBar.setVisibility(View.GONE);
            Log.i(TAG, "START" + mRequest);
            if (!mRequest.isEmpty()) {
                setParamssToUi();
                Log.i(TAG, "FINISH" + mRequest);
            }
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
        private void setParamssToUi() {
            String city = "";
            if (mId.equals(ID_DNEPR)) {
                city = getString(R.string.dnepr);
            } else if (mId.equals(ID_NIKOPOL)) {
                city = getString(R.string.nikopol);
            } else city = getString(R.string.kiev);

            //        String[] mParams = getParamsFromApi(id);

            mCity.setText(city);
//        mTemperature.setText(mParams[0]);
//        mMinTemp.setText(mParams[1]);
//        mMaxTemp.setText(mParams[2]);
//        mState.setText(mParams[3]);
//
//        Picasso.with(MainActivity.this)
//                .load(mParams[4])
//                .into(mIcon);
        }


        /**
         * @return responce from server
         */
        String doRequest() throws UnsupportedEncodingException {

            String data = URLEncoder.encode("id", "UTF-8")
                    + "=" + URLEncoder.encode(mId, "UTF-8");

            data += "&" + URLEncoder.encode("APPID", "UTF-8")
                    + "=" + URLEncoder.encode(getString(R.string.api_weather_key), "UTF-8");

            String text = "";
            BufferedReader reader = null;


            try {
                URL url = new URL(getString(R.string.general_url) + data);
                HttpURLConnection connect = (HttpURLConnection) url.openConnection();
                connect.setRequestMethod("GET");
                connect.setRequestProperty("Content-length", "0");
                connect.setUseCaches(false);
                connect.setAllowUserInteraction(false);
//                connect.setConnectTimeout(3000);
//                connect.setReadTimeout(3000);
                connect.connect();

                int status = connect.getResponseCode();

                switch (status) {
                    case 200:
                    case 201:
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
//код обработки ошибки
            } catch (IOException ex) {
//код обработки ошибки
            }


//            // Send data
//            try {
//                // Defined URL  where to send data
//                URL url = new URL(getString(R.string.general_url));
//
//                // Send POST data request
//                URLConnection conn = url.openConnection();
//                conn.setDoOutput(true);
//                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//                wr.write(data);
//                wr.flush();
//
//                // Get the server response
//                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                StringBuilder sb = new StringBuilder();
//                String line;
//
//                // Read Server Response
//                while ((line = reader.readLine()) != null) {
//                    // Append server response in string
//                    sb.append(line + "\n");
//                }
//                text = sb.toString();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            } finally {
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (Exception ex) {
//                        System.out.println("do request = " + ex.toString());
//                        ex.printStackTrace();
//                    }
//                }
//            }
            return text;
        }
    }
}
