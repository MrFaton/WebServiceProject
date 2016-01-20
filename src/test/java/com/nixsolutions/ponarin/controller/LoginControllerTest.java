package com.nixsolutions.ponarin.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nixsolutions.ponarin.consatnt.View;
import com.nixsolutions.ponarin.dao.UserDao;
import com.nixsolutions.ponarin.entity.User;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = ("classpath:/app-context-test.xml"))
@WebAppConfiguration
public class LoginControllerTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private LoginController loginController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testMainPageAdmin() throws Exception {
        List<User> userList = new ArrayList<>();
        when(userDao.findAll()).thenReturn(userList);

        mockMvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(model().attributeExists("personLogin", "userList"))
                .andExpect(view().name(View.PAGE_ADMIN));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testMainPageUser() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(model().attributeExists("personLogin"))
                .andExpect(view().name(View.PAGE_USER));
    }

    @Test
    @WithMockUser(roles = "BAD")
    public void testMainpageWithBadRole() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(view().name(View.PAGE_LOGIN));
    }

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk())
                .andExpect(view().name(View.PAGE_LOGIN));
    }

    @Test
    public void testLoginWithParams() throws Exception {
        mockMvc.perform(get("/login").param("error", "errorVal").param("logout",
                "logoutVal")).andExpect(status().isOk())
                .andExpect(model().attributeExists("error", "msg"));
    }
}
