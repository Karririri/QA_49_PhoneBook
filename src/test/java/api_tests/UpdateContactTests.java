package api_tests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dto.Contact;
import dto.TokenDto;
import dto.User;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.BaseApi;
import utils.ContactFactory;

import java.io.IOException;

public class UpdateContactTests implements BaseApi {

    TokenDto token;
    String contactId;

    @BeforeClass
    public void loginAndCreateContact() {
        User user = new User("karinabalatsky0201@gmail.com", "Abcr@8uf");
        RequestBody requestBody = RequestBody.create(GSON.toJson(user), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + LOGIN)
                .post(requestBody)
                .build();
        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            if (response.code() == 200) {
                token = GSON.fromJson(response.body().string(), TokenDto.class);
            } else {
                throw new RuntimeException("Login failed! Status code: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Contact contact = ContactFactory.positiveContact();
        RequestBody createBody = RequestBody.create(GSON.toJson(contact), JSON);
        Request createRequest = new Request.Builder()
                .url(BASE_URL + ADD_NEW_CONTACT)
                .addHeader(AUTH, "Bearer " + token.getToken())
                .post(createBody)
                .build();
        try (Response createResponse = OK_HTTP_CLIENT.newCall(createRequest).execute()) {
            String resp = createResponse.body().string();
            System.out.println("Create contact response: " + resp);
            JsonObject jsonObject = JsonParser.parseString(resp).getAsJsonObject();
            String message = jsonObject.get("message").getAsString();
            contactId = message.split("ID: ")[1].trim();
            System.out.println("New contact ID: " + contactId);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed: " + e.getMessage());
        }
    }


    @Test  //failed 403
    public void updateContactPositiveTest() {
        Contact updatedContact = new Contact();
        updatedContact.setName("Fgsgs");
        updatedContact.setLastName("Ggggg");
        updatedContact.setEmail("ffgdf55@mail.com");
        updatedContact.setPhone("9876543210");
        updatedContact.setAddress("Jerusalem");
        updatedContact.setDescription("test");

        RequestBody updateBody = RequestBody.create(GSON.toJson(updatedContact), JSON);
        Request updateRequest = new Request.Builder()
                .url(BASE_URL + UPDATE_CONTACT + "/" + contactId)
                .addHeader(AUTH,token.getToken())
                .put(updateBody)
                .build();

        try (Response response = OK_HTTP_CLIENT.newCall(updateRequest).execute()) {
            String resp = response.body().string();
            System.out.println("Update contact response: " + resp);
            Assert.assertEquals(response.code(), 200, "Contact update failed!");
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Exception during update contact: " + e.getMessage());
        }
    }
}
