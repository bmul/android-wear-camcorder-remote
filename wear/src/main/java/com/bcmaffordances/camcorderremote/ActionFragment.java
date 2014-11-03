package com.bcmaffordances.camcorderremote;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.view.CircledImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by bmullins on 11/2/14.
 */
public class ActionFragment extends Fragment {

    private static final String TAG = "ActionFragment";
    private static final String BUNDLE_ACTION_ICON = "actionIcon";
    private static final String BUNDLE_ACTION_TEXT = "actionText";

    private View.OnClickListener mOnClickListener;
    private TextView mActionTextView;
    private CircledImageView mActionButtonImageView;

    /**
     * Create an ActionFragment.
     *
     * This class has public setters on the text/icon resources and onClickListener
     * facilitate hot-swapping functionality. This allows the user to access a different
     * set of actions without having to use another fragment. This is especially useful
     * for situations such as changing a 'Play' action into a 'Pause' action.
     *
     * @param iconResourceId Icon resource identifier
     * @param textResourceId Text resource identifier
     * @param listener onClickListener callback
     * @return ActionFragment object
     */
    public static ActionFragment create(int iconResourceId, int textResourceId, View.OnClickListener listener) {
        ActionFragment fragment = new ActionFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_ACTION_ICON, iconResourceId);
        args.putInt(BUNDLE_ACTION_TEXT, textResourceId);
        // TODO pass listener
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        boolean attachToRoot = false;
        return inflater.inflate(R.layout.action, container, attachToRoot);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActionButtonImageView = (CircledImageView) view.findViewById(R.id.actionButtonImage);
        int imageResource = getArguments().getInt(BUNDLE_ACTION_ICON);
        mActionButtonImageView.setImageResource(imageResource);

        mActionTextView = (TextView) view.findViewById(R.id.actionText);
        int textResource = getArguments().getInt(BUNDLE_ACTION_TEXT);
        mActionTextView.setText(getString(textResource));

        View actionButton = view.findViewById(R.id.actionButton);
        actionButton.setOnClickListener(mOnClickListener);
    }

    public void setTextResource(int actionTextResourceId) {
        if (mActionTextView != null) {
            mActionTextView.setText(getString(actionTextResourceId));
        }
    }

    public void setIconResource(int actionIconResourceId) {
        if (mActionButtonImageView != null) {
            mActionButtonImageView.setImageResource(actionIconResourceId);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }
}
