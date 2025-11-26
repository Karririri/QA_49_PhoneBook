package api_tests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dto.TokenDto;
import dto.User;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.BaseApi;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GetAllContactsTests implements BaseApi {

    TokenDto token;

    @BeforeClass
    public void login() {
        User user = new User("karinabalatsky0201@gmail.com", "Abcr@8uf");
        RequestBody requestBody = RequestBody.create(GSON.toJson(user), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + LOGIN)
                .post(requestBody)
                .build();
        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            if (response.code() == 200)
                token = GSON.fromJson(response.body().string(), TokenDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getAllContactsPositiveTest() {
        Request request = new Request.Builder()
                .url(BASE_URL + GET_ALL_CONTACTS)
                .addHeader(AUTH, token.getToken())
                .get()
                .build();
        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            String json = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            JsonArray contactsArray = jsonObject.getAsJsonArray("contacts");
            System.out.println("Status code: " + response.code());
            System.out.println("Total contacts: " + contactsArray.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getAllContactsNegativeTest() {
        String wrongToken = "wrong token";
        Request request = new Request.Builder()
                .url(BASE_URL + GET_ALL_CONTACTS)
                .addHeader(AUTH, wrongToken)
                .get()
                .build();
        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            String json = response.body().string();
            System.out.println("Status code: " + response.code());
            assertEquals(response.code(), 401);
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            assertTrue(jsonObject.has("error") || jsonObject.has("message"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
