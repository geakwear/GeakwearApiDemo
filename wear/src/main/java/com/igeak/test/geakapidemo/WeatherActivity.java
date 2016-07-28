package com.igeak.test.geakapidemo;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.igeak.android.common.ConnectionResult;
import com.igeak.android.common.api.GeakApiClient;
import com.igeak.android.common.api.ResultCallback;
import com.igeak.android.wearable.MessageApi;
import com.igeak.android.wearable.MessageEvent;
import com.igeak.android.wearable.Node;
import com.igeak.android.wearable.NodeApi;
import com.igeak.android.wearable.Wearable;

public class WeatherActivity extends Activity implements GeakApiClient.ConnectionCallbacks,
        GeakApiClient.OnConnectionFailedListener, NodeApi.NodeListener, MessageApi.MessageListener {
    public static final String AUTHORITY = "com.igeak.wear.WeatherProvider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static final String TAG = "WeatherActivity";
    private static final String DEFAULT_NODE = "default_node";
    private TextView mWeatherTv;
    private GeakApiClient mGeakApiClient;

    private byte[] weatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        init();
    }

    private void init() {
        mWeatherTv = (TextView) findViewById(R.id.weather_tv);
        mGeakApiClient = new GeakApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        fetchWeatherInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGeakApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.MessageApi.removeListener(mGeakApiClient, this);
        Wearable.NodeApi.removeListener(mGeakApiClient, this);
        mGeakApiClient.disconnect();
    }

    private void fetchWeatherInfo() {
        Uri uri = Uri.withAppendedPath(CONTENT_URI, "get_current_weather");
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                int count = c.getColumnCount();
                if (count > 15) {
                    YahooWeatherInfo yahooWeatherInfo = new YahooWeatherInfo(c);
                    mWeatherTv.setText(yahooWeatherInfo.toString());
                    weatherData = yahooWeatherInfo.toString().getBytes();
                } else {
                    GeakWeatherInfo geakWeatherInfo = new GeakWeatherInfo(c);
                    mWeatherTv.setText(geakWeatherInfo.toString());
                    weatherData = geakWeatherInfo.toString().getBytes();
                }
            }
            c.close();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Wearable.MessageApi.addListener(mGeakApiClient, this);
        Wearable.NodeApi.addListener(mGeakApiClient, this);
        sendMessagetoPhone();
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: " + result);
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        Log.d(TAG, "onMessageReceived: " + event);
    }

    @Override
    public void onPeerConnected(Node node) {
        Log.d(TAG, "onPeerConncted:");
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.d(TAG, "onPeerDisconnected:");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void sendMessagetoPhone() {
        byte[] data = weatherData;
        if (data != null)
        Wearable.MessageApi.sendMessage(
                mGeakApiClient, DEFAULT_NODE, "Weathers", data).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        } else {
                            Log.d(TAG, "Success");
                        }
                    }
                }
        );
    }
}
