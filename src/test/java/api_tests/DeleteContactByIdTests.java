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

public class DeleteContactByIdTests implements BaseApi {

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
            if (response.code() == 200)
                token = GSON.fromJson(response.body().string(), TokenDto.class);
            else
                throw new RuntimeException("Login failed! " + response.code());
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
            contactId = message.split("ID: ")[1].trim(); // вытаскиваем ID
            System.out.println("New contact ID: " + contactId);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed: " + e.getMessage());
        }
    }

    @Test
    public void deleteContactByIdPositiveTest() {
        Request deleteRequest = new Request.Builder()
                .url(BASE_URL + "/v1/contacts/" + contactId)
                .addHeader(AUTH, "Bearer " + token.getToken())
                .delete()
                .build();

        try (Response response = OK_HTTP_CLIENT.newCall(deleteRequest).execute()) {
            String resp = response.body().string();
            System.out.println("Delete contact response: " + resp);
            Assert.assertEquals(response.code(), 200, "Contact deletion failed!");
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Exception during delete contact: " + e.getMessage());
        }
    }

    @Test
    public void deleteNonExistentContactNegativeTest() {
        String fakeId = "00000000";
        Request request = new Request.Builder()
                .url(BASE_URL + DELETE_CONTACT_BY_ID + "/" + fakeId)
                .addHeader(AUTH, token.getToken())
                .delete()
                .build();

        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            String resp = response.body().string();
            System.out.println("Delete non-existent contact response: " + resp);
            Assert.assertEquals(response.code(), 403, "Expected 403 for non-existent or unauthorized contact");
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Exception during delete non-existent contact: " + e.getMessage());
        }
    }

    @Test
    public void deleteNonExistentContact() {
        String fakeId = "00000000-0000-0000-0000-000000000000"; // несуществующий ID
        Request request = new Request.Builder()
                .url(BASE_URL + DELETE_CONTACT_BY_ID + "/" + fakeId)
                .addHeader(AUTH, token.getToken())
                .delete()
                .build();

        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            String resp = response.body().string();
            System.out.println("Delete non-existent contact response: " + resp);
            Assert.assertEquals(response.code(), 404, "Expected 404 for non-existent contact");
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Exception during delete non-existent contact: " + e.getMessage());
        }
    }

    @Test
    public void deleteAlreadyDeletedContactNegativeTest() {
        try (Response firstDelete = OK_HTTP_CLIENT.newCall(
                new Request.Builder()
                        .url(BASE_URL + DELETE_CONTACT_BY_ID + "/" + contactId)
                        .addHeader(AUTH, token.getToken())
                        .delete()
                        .build()
        ).execute()) {
            int firstCode = firstDelete.code();
            String resp = firstDelete.body().string();
            System.out.println("First delete response: " + resp);
            Assert.assertTrue(firstCode == 200 || firstCode == 403,
                    "Unexpected status code when deleting contact: " + firstCode);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        Request secondDelete = new Request.Builder()
                .url(BASE_URL + DELETE_CONTACT_BY_ID + "/" + contactId)
                .addHeader(AUTH, token.getToken())
                .delete()
                .build();
        try (Response response = OK_HTTP_CLIENT.newCall(secondDelete).execute()) {
            String resp = response.body().string();
            System.out.println("Second delete response: " + resp);
            Assert.assertEquals(response.code(), 403,
                    "Expected 403");
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void deleteContactWithoutAuth() {
        Request request = new Request.Builder()
                .url(BASE_URL + DELETE_CONTACT_BY_ID + "/" + contactId)
                .delete()
                .build();

        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            String resp = response.body().string();
            System.out.println("Delete contact without auth response: " + resp);
            Assert.assertEquals(response.code(), 403, "Expected 403 when deleting without token");
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}