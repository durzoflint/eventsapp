package droidsector.tech.eventsapp.EventsActivityFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import droidsector.tech.eventsapp.R;

/**
 * Created by Abhinav on 12-Mar-18.
 */

public class MyEventsFragment extends Fragment {
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_events, container, false);
        return rootView;
    }
}
