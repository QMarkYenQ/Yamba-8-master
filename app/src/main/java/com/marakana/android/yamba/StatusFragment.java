package com.marakana.android.yamba;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;

import java.util.List;

public class StatusFragment extends Fragment {
	// TAG = StatusFragment
    private static final String TAG = StatusFragment.class.getSimpleName();

	private Button mButtonTweet;
	private EditText mTextStatus;
	private TextView mTextCount;
	private int mDefaultColor;

	@Override
	public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
			Bundle savedInstanceState )
	{
		View v = inflater.inflate( R.layout.fragment_status, null, false );

        //  Button
		mButtonTweet = (Button) v.findViewById(R.id.status_button_tweet);

        //  EditText
		mTextStatus = (EditText) v.findViewById(R.id.status_text);
        //  TextView
		mTextCount = (TextView) v.findViewById(R.id.status_text_count);

        // setOnClickListener( )
		mButtonTweet.setOnClickListener(new OnClickListener(){
            //onClick()
			@Override
			public void onClick(View v) {
			String status = mTextStatus.getText().toString();
			PostTask postTask = new PostTask();
			postTask.execute( status );
			Log.d( TAG, "onClicked" );
			}

		});
        //
        mTextCount.setText(Integer.toString(140));
        mDefaultColor = mTextCount.getTextColors().getDefaultColor();

        // addTextChangedListener()
		mTextStatus.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				int count = 140 - s.length();
				mTextCount.setText(Integer.toString(count));
				if (count < 50) {
					mTextCount.setTextColor(Color.RED);
				} else {
					mTextCount.setTextColor(mDefaultColor);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});

		Log.d(TAG, "onCreated");
		return v;
	}
    //  PostTask()
	class PostTask extends AsyncTask<String, Void, String>
	{
		private ProgressDialog progress;

		@Override
		protected void onPreExecute()
		{
			progress =
				ProgressDialog.show(
					getActivity(),
					"Posting",
					"Please wait..."
				);
			progress.setCancelable(true);
		}
		// Executes on a non-UI thread
        // doInBackground()
        @Override
		protected String doInBackground(String... params)
		{
			try {
				SharedPreferences prefs
					= PreferenceManager.getDefaultSharedPreferences(
						getActivity()
					);
				String username = prefs.getString("username", "");
				String password = prefs.getString("password", "");
				// Check that username and password are not empty
				// If empty, Toast a message to set login info and bounce to
				// SettingActivity
				// Hint: TextUtils.
				if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
				{
					//  SettingsActivity
					getActivity().startActivity(
						new Intent(
							getActivity(),
							SettingsActivity.class
						)
					);
					return "Please update your username and password";
				}

				// class YambaClient - postStatus()
				YambaClient cloud =
					new YambaClient(
						username,
						password,
						"http://yamba.newcircle.com/api"
					);
				cloud.postStatus(params[0]);
				Log.d(TAG, "Successfully posted to the cloud: " + params[0]);
				return "Successfully posted";
			} catch (Exception e) {
				Log.e(TAG, "Failed to post to the cloud", e);
				e.printStackTrace();
				return "Failed to post";
			}
		}
		// Called after doInBackground() on UI thread
        //onPostExecute()
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			if (getActivity() != null && result != null)
			{
                Toast.makeText(
					getActivity(),
					result,
					Toast.LENGTH_LONG
				).show();
            }
		}
	}
}
