package droidsector.tech.eventsapp.AddNewEventActivityFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import droidsector.tech.eventsapp.AddNewEventActivity;
import droidsector.tech.eventsapp.R;

public class BasicInfoFragment extends Fragment {
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_basic_info, container, false);
        Button next = rootView.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = AddNewEventActivity.id;
                ViewPager viewPager = getActivity().findViewById(id);
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
        });
        return rootView;
    }
}
