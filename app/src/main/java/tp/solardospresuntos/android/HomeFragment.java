package tp.solardospresuntos.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tp.solardospresuntos.android.databinding.FragmentHomeBinding;
import tp.solardospresuntos.android.utils.listener.DebouncedOnClickListener;

/**
 * Created by filiperodrigues on 18/05/17.
 */

public class HomeFragment extends Fragment {
    private FragmentHomeBinding mBinding;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mBinding = FragmentHomeBinding.bind(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupUI();
    }

    private void setupUI() {
        mBinding.homeTakePhoto.setOnClickListener(new DebouncedOnClickListener() {
            @Override
            public void onDebouncedClick(View v) {
                Log.e("Click", "Camera");
            }
        });

        mBinding.homeAddPhoto.setOnClickListener(new DebouncedOnClickListener() {
            @Override
            public void onDebouncedClick(View v) {
                Log.e("Click", "Add Photo");
            }
        });

        mBinding.homeOpenGallery.setOnClickListener(new DebouncedOnClickListener() {
            @Override
            public void onDebouncedClick(View v) {
                Log.e("Click", "System Gallery");
            }
        });
    }

}
