package droidsector.tech.eventsapp.AddNewEventActivityFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import droidsector.tech.eventsapp.R;

public class BasicInfoFragment extends Fragment {
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_basic_info, container, false);
        return rootView;
    }
}
