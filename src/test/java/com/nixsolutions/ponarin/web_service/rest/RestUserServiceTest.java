package com.nixsolutions.ponarin.web_service.rest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.nixsolutions.ponarin.entity.Role;
import com.nixsolutions.ponarin.entity.User;

public class RestUserServiceTest {
    private static final String BASE_URL = "http://10.10.34.83:8080/WebServiceProject/users/";
    private static final String ID_URL = "http://10.10.34.83:8080/WebServiceProject/users/{id}";
    private static final String NON_EXISTS_LOGIN = "nonExistsLogin";
    private static final String NON_EXISTS_EMAIL = "non_exists_email@email.ua";
    private static final long NON_EXISTS_ID = -1L;

    private RestTemplate restTemplate;
    private User testUser;

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
        testUser = getDefaultUser();
        saveUser(testUser);
        testUser.setId(getIdByLogin(testUser.getLogin()));
    }

    @After
    public void tearDown() {
        restTemplate.delete(ID_URL, testUser.getId());
    }

    @Test
    public void testCreate() {
        tearDown();
        User user = getDefaultUser();

        testUser = user;

        ResponseEntity<User> responseEntity = restTemplate
                .postForEntity(BASE_URL, user, User.class);

        testUser.setId(getIdByLogin(user.getLogin()));

        assertEquals("Http status code must be " + HttpStatus.CREATED,
                HttpStatus.CREATED, responseEntity.getStatusCode());

        User actualUser = restTemplate.getForObject(ID_URL, User.class,
                testUser.getId());

        assertEquals("Users must equals", testUser, actualUser);
    }

    @Test(expected = HttpClientErrorException.class)
    public void testCreateWithLoginDublicate() {
        testUser.setEmail(NON_EXISTS_EMAIL);
        restTemplate.postForObject(BASE_URL, testUser, User.class);
    }

    @Test(expected = HttpClientErrorException.class)
    public void testCreateWithEmailDublicate() {
        testUser.setLogin(NON_EXISTS_LOGIN);
        restTemplate.postForObject(BASE_URL, testUser, User.class);
    }

    @Test
    public void testUpdate() {
        testUser.setPassword(testUser.getPassword() + "12erph6");
        testUser.setEmail(NON_EXISTS_EMAIL);
        testUser.setFirstName("updated first name");
        testUser.setLastName("updated last name");
        testUser.setBirthDay(new Date());

        Role role = new Role();
        role.setId(2);
        role.setName("User");
        testUser.setRole(role);

        restTemplate.put(ID_URL, testUser, testUser.getId());

        User actualUser = restTemplate.getForObject(ID_URL, User.class,
                testUser.getId());
        assertEquals("Users must equals", testUser, actualUser);
    }

    @Test(expected = HttpClientErrorException.class)
    public void testUpdateNonExistsId() {
        User user = getDefaultUser();
        user.setId(NON_EXISTS_ID);

        restTemplate.put(ID_URL, testUser, user.getId());
    }

    @Test(expected = HttpClientErrorException.class)
    public void testUpdateEmailDublicate() {
        User[] users = restTemplate.getForObject(BASE_URL, User[].class);
        testUser.setEmail(users[0].getEmail());

        restTemplate.put(ID_URL, testUser, testUser.getId());
    }

    @Test
    public void testDelete() {
        restTemplate.delete(ID_URL, testUser.getId());
        try {
            getIdByLogin(testUser.getLogin());
            fail("User must not exists");
        } catch (RuntimeException ex) {
        }
    }

    @Test
    public void testDeletWithNonExistsId() {
        try {
            restTemplate.delete(ID_URL, NON_EXISTS_ID);
        } catch (Exception ex) {
            fail("Wrong exception");
        }
    }

    @Test
    public void testFindAll() {
        ResponseEntity<User[]> responseEntity = restTemplate
                .getForEntity(BASE_URL, User[].class);
        User[] userList = responseEntity.getBody();
        assertNotNull("User list must not be empty", userList);
        assertTrue("Size must be grate or equals 2", userList.length >= 2);
    }

    @Test
    public void testFindById() {
        ResponseEntity<User> responseEntity = restTemplate.getForEntity(ID_URL,
                User.class, testUser.getId());
        User user = responseEntity.getBody();
        assertNotNull("User must not be null", user);
        assertEquals("Users must equals", testUser, user);
    }

    @Test
    public void testFindByIdWitnNonExistsId() {
        ResponseEntity<User> responseEntity = restTemplate.getForEntity(ID_URL,
                User.class, -1);
        assertNull("User must be null", responseEntity.getBody());
    }

    private User getDefaultUser() {
        User user = new User();

        user.setLogin("testLogin");
        user.setPassword("Q!q1q1");
        user.setEmail("testEmail@mail.ru");
        user.setFirstName("First Name");
        user.setLastName("Last Name");
        user.setBirthDay(new Date());

        Role role = new Role();
        role.setId(1);
        role.setName("Admin");
        user.setRole(role);

        return user;
    }

    private void saveUser(User user) {
        restTemplate.postForEntity(BASE_URL, user, User.class);
    }

    private long getIdByLogin(String login) {
        User[] userList = restTemplate.getForObject(BASE_URL, User[].class);
        for (User user : userList) {
            if (user.getLogin().equals(login)) {
                return user.getId();
            }
        }
        throw new RuntimeException(
                "User with login " + login + " not found in db");
    }
}
