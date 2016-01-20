package com.nixsolutions.ponarin.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nixsolutions.ponarin.consatnt.View;
import com.nixsolutions.ponarin.dao.RoleDao;
import com.nixsolutions.ponarin.dao.UserDao;
import com.nixsolutions.ponarin.entity.Role;
import com.nixsolutions.ponarin.entity.User;
import com.nixsolutions.ponarin.form.UserForm;
import com.nixsolutions.ponarin.utils.Captcha;
import com.nixsolutions.ponarin.utils.UserFormUtils;
import com.nixsolutions.ponarin.utils.UserUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = ("classpath:/app-context-test.xml"))
@WebAppConfiguration
public class RegistrationControllerTest {
    @Mock
    private RoleDao roleDao;

    @Mock
    private UserDao userDao;

    @Mock
    private UserUtils userUtils;

    @Mock
    private Captcha captcha;

    @InjectMocks
    private RegistrationController registrationController;

    private MockMvc mockMvc;

    private UserFormUtils userFormUtils = new UserFormUtils();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(registrationController)
                .build();
    }

    @Test
    public void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/registration")).andExpect(status().isOk())
                .andExpect(model().attributeExists("userForm"))
                .andExpect(view().name(View.FORM_REG));
    }

    @Test
    public void testRegistration() throws Exception {
        when(userUtils.isLoginExists(anyString())).thenReturn(false);
        when(userUtils.isEmailExists(any(UserForm.class))).thenReturn(false);
        when(userUtils.getUserByForm(any(UserForm.class), any(Role.class)))
                .thenReturn(new User());
        when(roleDao.findByName(anyString())).thenReturn(new Role());
        when(captcha.isValid(anyString(), anyString(), anyString()))
                .thenReturn(true);

        mockMvc.perform(post("/registration")
                .params(userFormUtils.createParamsUserForm()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userForm"))
                .andExpect(view().name(View.FROM_REG_SUCCESS))
                .andExpect(model().hasNoErrors());
    }

    @Test
    public void testRegistrationInvalidForm() throws Exception {
        MultiValueMap<String, String> parametersForm = userFormUtils
                .createParamsUserForm();
        parametersForm.get("firstName").set(0, "     ");

        mockMvc.perform(post("/registration").params(parametersForm))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors()).andExpect(model()
                        .attributeHasFieldErrors("userForm", "firstName"))
                .andExpect(view().name(View.FORM_REG));
    }

    @Test
    public void testRegistrationLoginExists() throws Exception {
        when(userUtils.isLoginExists(anyString())).thenReturn(true);

        mockMvc.perform(post("/registration")
                .params(userFormUtils.createParamsUserForm()))
                .andExpect(status().isOk()).andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("userForm", "login"))
                .andExpect(view().name(View.FORM_REG));
    }

    @Test
    public void testRegistrationEmailExists() throws Exception {
        when(userUtils.isEmailExists(any(UserForm.class))).thenReturn(true);

        mockMvc.perform(post("/registration")
                .params(userFormUtils.createParamsUserForm()))
                .andExpect(status().isOk()).andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("userForm", "email"))
                .andExpect(view().name(View.FORM_REG));
    }

    @Test
    public void testRegistrationIncorrectCaptcha() throws Exception {
        when(captcha.isValid(anyString(), anyString(), anyString()))
                .thenReturn(false);
        when(userUtils.isLoginExists(anyString())).thenReturn(false);
        when(userUtils.isEmailExists(any(UserForm.class))).thenReturn(false);

        mockMvc.perform(post("/registration")
                .params(userFormUtils.createParamsUserForm()))
                .andExpect(status().isOk())
                .andExpect(view().name(View.FORM_REG));
    }
}
