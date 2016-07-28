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
import com.igeak.android.wearable.Node;
import com.igeak.android.wearable.NodeApi;
import com.igeak.android.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NodeFragment extends Fragment implements View.OnClickListener,
        GeakApiClient.OnConnectionFailedListener {

    private static final int REQUEST_RESOLVE_ERROR = 1000;
    private final String TAG = "NodeFragment";

    private GeakApiClient mGeakApiClient;
    private Button mCheckBtn;
    private TextView mShowTv;
    private boolean mResolvingError = false;
    private NodeApi.NodeListener mNodeListener;
    private String mConnectdNode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_node, container, false);
        mCheckBtn = (Button) v.findViewById(R.id.check);
        mShowTv = (TextView) v.findViewById(R.id.text);
        mCheckBtn.setOnClickListener(this);
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
        mGeakApiClient = new GeakApiClient.Builder(getActivity())
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GeakApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the Data Layer API
                        Wearable.NodeApi.addListener(mGeakApiClient, mNodeListener);
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
            case R.id.check: {
                new StartCheckTask().execute();
                break;
            }
        }
    }

    @Override //OnConnectionFailedListener
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
        }
    }

    private Collection<String> getNodes() {
        Set<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGeakApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    private class StartCheckTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                if (node != null) {
                    mConnectdNode = "The connected node is " + node;
                    Log.d(TAG, "The connected node is " + node);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mShowTv.setText(mConnectdNode);
        }
    }

}