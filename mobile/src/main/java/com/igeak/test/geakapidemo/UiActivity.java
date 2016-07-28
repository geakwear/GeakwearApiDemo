package com.igeak.test.geakapidemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.igeak.android.common.ConnectionResult;
import com.igeak.android.common.api.GeakApiClient;
import com.igeak.android.common.api.ResultCallback;
import com.igeak.android.wearable.MessageApi;
import com.igeak.android.wearable.MessageEvent;
import com.igeak.android.wearable.Wearable;

public class UiActivity extends Activity implements GeakApiClient.OnConnectionFailedListener,
        GeakApiClient.ConnectionCallbacks, MessageApi.MessageListener {

    private static final String START_ACTIVITY_PATH = "/start-ui";
    private static final String DEFAULT_NODE = "default_node";

    private Button mStartUiBtn;
    private GeakApiClient mGeakApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);
        mStartUiBtn = (Button) findViewById(R.id.start_ui);
        mStartUiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wearable.MessageApi.sendMessage(
                        mGeakApiClient, DEFAULT_NODE, START_ACTIVITY_PATH, new byte[0])
                        .setResultCallback(
                                new ResultCallback<MessageApi.SendMessageResult>() {
                                    @Override
                                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                    }
                                }
                        );
            }
        });

        mGeakApiClient = new GeakApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
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
        mGeakApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Wearable.MessageApi.addListener(mGeakApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onMessageReceived(MessageEvent event) {

    }
}
