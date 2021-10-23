package com.example.crujientepenguins;

import retrofit2.Call;
import com.google.gson.Gson;

public class UserRepo {

    private UserService userService;
    public Gson gson;

    public UserRepo(
            UserService userService
    ) {
        this.userService = userService;
        this.gson = new Gson();
    }

//    public Call<UserProfile> getUserById(UserProfile user) {
//        String json = gson.toJson(user);
//        return userService.getUser(json);
//    }

    public Call<LoginProfile> getUserToken(LoginProfile loginProfile) {
        String json = gson.toJson(loginProfile);
        return userService.getUserToken(json);
    }
}
