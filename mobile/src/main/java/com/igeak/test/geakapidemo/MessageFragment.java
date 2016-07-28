package com.igeak.test.geakapidemo;

import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Collection;
import java.util.HashSet;

public class MessageFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_RESOLVE_ERROR = 1000;
    private static final String START_ACTIVITY_PATH = "/start-nao";
    private final String TAG = "MessageFragment";

    private Button mControlBtn;
    private TextView mMessageTv;
    private boolean mResolvingError = false;
    private NodeApi.NodeListener mNodeListener;
    private MessageApi.MessageListener mMessageListener;
    private GeakApiClient mGeakApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        mControlBtn = (Button) v.findViewById(R.id.control_button);
        mMessageTv = (TextView) v.findViewById(R.id.message);
        mControlBtn.setOnClickListener(this);
        mNodeListener = new NodeApi.NodeListener() {
            @Override
            public void onPeerConnected(final Node peer) {
                Log.d(TAG, "onPeerConnected: " + peer);
            }

            @Override
            public void onPeerDisconnected(final Node peer) {
                Log.d(TAG, "onPeerDisconnected: " + peer);
            }
        };
        mMessageListener = new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                Log.d(TAG, "Received Message From Phone!");
            }
        };
        mGeakApiClient = new GeakApiClient.Builder(getActivity())
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GeakApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the Data Layer API
                        Wearable.NodeApi.addListener(mGeakApiClient, mNodeListener);
                        Wearable.MessageApi.addListener(mGeakApiClient, mMessageListener);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GeakApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        if (mResolvingError) {
                            // Already attempting to resolve an error.
                            return;
                        } else if (result.hasResolution()) {
                            try {
                                mResolvingError = true;
                                Log.d(TAG, "Failing in Connecting");
                                result.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
                            } catch (IntentSender.SendIntentException e) {
                                // There was an error with the resolution intent. Try again.
                                mGeakApiClient.connect();
                                Log.d(TAG, "Try Again");
                            }
                        } else {
                            mResolvingError = false;
                            Wearable.NodeApi.removeListener(mGeakApiClient, mNodeListener);
                            Wearable.MessageApi.removeListener(mGeakApiClient, mMessageListener);
                        }
                    }
                })
                .build();
        if (!mResolvingError) {
            mGeakApiClient.connect();
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.control_button: {
                Log.d(TAG, "Button Clicked");
                new StartWearableActivityTask().execute();
            }
        }
    }

    private void sendStartActivityMessage(String node) {
        Wearable.MessageApi.sendMessage(
                mGeakApiClient, node, START_ACTIVITY_PATH, new byte[0]).setResultCallback(
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

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGeakApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node);
            }
            return null;
        }
    }

}