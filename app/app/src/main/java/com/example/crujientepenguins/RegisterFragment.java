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
import com.example.crujientepenguins.databinding.RegisterFragmentBinding;
import com.example.crujientepenguins.pojos.LoginProfile;
import com.example.crujientepenguins.pojos.LoginToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private RegisterFragmentBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = RegisterFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonRegister.setOnClickListener(view1 -> {
            String username = binding.editTextTextPersonName2.getText().toString();
            String password = binding.editTextTextPassword.getText().toString();
            String confirmPassword = binding.editTextTextPassword2.getText().toString();
            if (!password.equals(confirmPassword)) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Passwords don't match!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            LoginProfile login = new LoginProfile(username, password);
            Api.getInstance().register(login, new Callback<Void>() {
                @Override
                public void onResponse
                        (@NonNull Call<Void> call, @NonNull Response<Void> response){
                    NavHostFragment.findNavController(RegisterFragment.this)
                            .navigate(R.id.action_RegisterFragment_to_FirstFragment);
                }

                @Override
                public void onFailure (@NonNull Call <Void> call, @NonNull Throwable t){
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Failed",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.buttonSecond.setOnClickListener(view12 ->
                NavHostFragment.findNavController(RegisterFragment.this)
                .navigate(R.id.action_RegisterFragment_to_FirstFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}