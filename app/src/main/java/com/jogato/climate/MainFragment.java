package com.jogato.climate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MainFragment extends Fragment {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mOptions;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        mOptions = getResources().getStringArray(R.array.navigation_options);
        mDrawerLayout =  v.findViewById(R.id.drawer_layout);
        mDrawerList = v.findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_option_items, mOptions));
        mDrawerList.setOnItemClickListener(new DrawerOptionClickListener());

        return v;
    }

    public class DrawerOptionClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(i == 2){
                User.getInstance().getmUserAuthState().signOut();
                Toast.makeText(getActivity(),"Log out successful",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
        }
    }

}
