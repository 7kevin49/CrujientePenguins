package com.example.crujientepenguins;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crujientepenguins.Api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.crujientepenguins.databinding.PostLoginBinding;
import com.example.crujientepenguins.pojos.PointsAvailable;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostLogin extends Fragment {

    private PostLoginBinding binding;
    private String sessionToken;
    private Api api;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        try {
            sessionToken = ((MainActivity) getActivity()).sessionToken.getToken();
        } catch (NullPointerException e) {
            Toast.makeText(getParentFragment().getContext(), "Warning: An Error Has Occured", Toast.LENGTH_LONG).show();
        }

        api = Api.getInstance();

        binding = PostLoginBinding.inflate(inflater, container, false);




//        points[0].updatePoints();
//        binding.progressBar2.setProgress(points[0].getPoints());

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final PointsAvailable[] points = {new PointsAvailable()};
        api.getPoints(sessionToken, new Callback<PointsAvailable>() {
            @Override
            public void onResponse(Call<PointsAvailable> call, Response<PointsAvailable> response) {
                points[0] = response.body();
//                assert points[0] != null;
                points[0].updatePoints();
                binding.progressBar2.setProgress(points[0].getPoints()/10);
                String str = binding.textViewPoints.getText() + " " + points[0].getPoints();
                System.out.println(str);
//                TextView textView = (TextView) findViewById(R.id.textViewPoints);
//                        container.findViewById( R.id.textViewPoints);

//                TextView text = (TextView) inflater.findViewById(R.id.textViewPoints);
                binding.textViewPoints.setText(str);
//                binding.textViewPoints.setText(str);
            }

            @Override
            public void onFailure(Call<PointsAvailable> call, Throwable t) {
                Toast.makeText(getContext(), "Error: Points could not be retrieved...", Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
