package net.ddns.smartfridge.smartfridgev2.vista.fragmentos;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.ddns.smartfridge.smartfridgev2.R;

/**
 * Fragment para la parte de programar Menú
 */
public class MainPm extends Fragment {


    /**
     * Constructor
     */
    public MainPm() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_pm, container, false);
    }

}
