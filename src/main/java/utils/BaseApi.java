package utils;

import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public interface BaseApi {
    String BASE_URL = "https://contactapp-telran-backend.herokuapp.com";
    String REGISTRATION = "/v1/user/registration/usernamepassword";
    String LOGIN = "/v1/user/login/usernamepassword";
    String ADD_NEW_CONTACT = "/v1/contacts";
    String GET_ALL_CONTACTS = "/v1/contacts";
    String UPDATE_CONTACT = "/v1/contacts";
    String DELETE_CONTACT_BY_ID = "/v1/contacts/{id}";
    Gson GSON = new Gson();
    MediaType JSON = MediaType.get("application/json");

    OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();

    String AUTH = "Authorization";
}
