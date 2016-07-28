package com.igeak.test.geakapidemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.igeak.android.common.ConnectionResult;
import com.igeak.android.common.api.GeakApiClient;
import com.igeak.android.common.api.ResultCallback;
import com.igeak.android.wearable.MessageApi;
import com.igeak.android.wearable.MessageEvent;
import com.igeak.android.wearable.Node;
import com.igeak.android.wearable.NodeApi;
import com.igeak.android.wearable.Wearable;

public class CardActivity extends Activity implements GeakApiClient.OnConnectionFailedListener,
        GeakApiClient.ConnectionCallbacks, NodeApi.NodeListener, MessageApi.MessageListener {

    private static final String TAG = "CardActivity";
    private static final String START_ACTIVITY_PATH = "/start-card";
    private static final String DEFAULT_NODE = "default_node";

    private Button mStartCardBtn;
    private TextView mCardTv;
    private GeakApiClient mGeakApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        mStartCardBtn = (Button) findViewById(R.id.start_card);
        mCardTv = (TextView) findViewById(R.id.card_text);
        mStartCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button Click");
                Wearable.MessageApi.sendMessage(
                        mGeakApiClient, DEFAULT_NODE, START_ACTIVITY_PATH, new byte[0])
                        .setResultCallback(
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
        Log.d(TAG, "onResume...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.MessageApi.removeListener(mGeakApiClient, this);
        Wearable.NodeApi.removeListener(mGeakApiClient, this);
        mGeakApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Wearable.MessageApi.addListener(mGeakApiClient, this);
        Wearable.NodeApi.addListener(mGeakApiClient, this);
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
}