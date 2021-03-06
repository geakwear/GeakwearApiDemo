package com.igeak.test.geakapidemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class StepActivity extends Activity implements GeakApiClient.ConnectionCallbacks,
        GeakApiClient.OnConnectionFailedListener, NodeApi.NodeListener, MessageApi.MessageListener {
    private static final Uri STEP_URI = Uri.parse("content://com.igeak.ticwear.steps");
    private static final String DEFAULT_NODE = "default_node";
    private static final String TAG = "StepActivity";

    private ContentResolver mResolver;
    private int mSteps;
    private ContentObserver mObserver;
    private TextView mStepTv;
    private GeakApiClient mGeakApiClient;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mStepTv.setText(getString(R.string.step_count) + msg.what);
            sendMessagetoPhone();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        init();
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

    private void init() {
        mStepTv = (TextView) findViewById(R.id.step_tv);

        mResolver = this.getContentResolver();
        mObserver = new ContentObserver(mHandler) {
            @Override
            public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                mSteps = fetchSteps();
                Message.obtain(mHandler, mSteps).sendToTarget();
            }
        };
        mGeakApiClient = new GeakApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mSteps = fetchSteps();
        mStepTv.setText(getString(R.string.step_count) + mSteps);
        registerContentObserver();
    }

    private int fetchSteps() {
        int steps = 0;
        Cursor cursor = mResolver.query(STEP_URI, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    steps = cursor.getInt(0);
                }
            } finally {
                cursor.close();
            }
        }
        return steps;
    }

    private void registerContentObserver() {
        mResolver.registerContentObserver(STEP_URI, true, mObserver);
    }

    private void unregisterContentObserver() {
        mResolver.unregisterContentObserver(mObserver);
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
        unregisterContentObserver();
    }

    private void sendMessagetoPhone() {
        byte[] data = String.valueOf(mSteps).getBytes();
        Wearable.MessageApi.sendMessage(
                mGeakApiClient, DEFAULT_NODE, "Steps", data).setResultCallback(
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
