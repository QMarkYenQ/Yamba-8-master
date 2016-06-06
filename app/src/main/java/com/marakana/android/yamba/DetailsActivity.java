package com.marakana.android.yamba;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Check if this activity was created before
		if (savedInstanceState == null) {
			// Create a fragment
			DetailsFragment fragment = new DetailsFragment();
			getFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, fragment,
							fragment.getClass().getSimpleName()).commit();
		}
	}
}
