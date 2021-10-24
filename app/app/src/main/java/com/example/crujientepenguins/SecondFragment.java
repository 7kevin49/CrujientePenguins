package com.example.crujientepenguins;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.crujientepenguins.databinding.FragmentSecondBinding;
import com.example.crujientepenguins.pojos.LoginProfile;
import com.example.crujientepenguins.pojos.LoginToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (((MainActivity) getActivity()).sessionToken != null) {
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_PostLogin);
        }

        binding.buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.editTextTextPersonName2.getText().toString();
                String password = binding.editTextTextPassword.getText().toString();
                LoginProfile login = new LoginProfile(username, password);
                Api.getInstance().login(login, new Callback<LoginToken>() {
                    @Override
                    public void onResponse
                            (@NonNull Call<LoginToken> call, @NonNull Response<LoginToken> response){
                        ((MainActivity) getActivity()).sessionToken = response.body();
                        Toast.makeText(getActivity().getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();

                        NavHostFragment.findNavController(SecondFragment.this)
                                .navigate(R.id.action_SecondFragment_to_PostLogin);
                    }

                    @Override
                    public void onFailure (@NonNull Call <LoginToken> call, @NonNull Throwable t){
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}