package com.nixsolutions.ponarin.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import com.nixsolutions.ponarin.utils.UserFormUtils;
import com.nixsolutions.ponarin.utils.UserUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = ("classpath:/app-context-test.xml"))
@WebAppConfiguration
public class PersonManageControllerTest {
    @Mock
    private RoleDao roleDao;

    @Mock
    private UserDao userDao;

    @Mock
    private UserUtils userUtils;

    @InjectMocks
    private PersonManageController personManageController;

    private MockMvc mockMvc;

    private UserFormUtils userFormUtils = new UserFormUtils();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(personManageController)
                .build();
    }

    @Test
    public void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/admin/create")).andExpect(status().isOk())
                .andExpect(model().attributeExists("userForm"))
                .andExpect(view().name(View.FROM_CREATE));
    }

    @Test
    public void testCreate() throws Exception {
        when(userUtils.isLoginExists(anyString())).thenReturn(false);
        when(userUtils.isEmailExists(any(UserForm.class))).thenReturn(false);
        when(userUtils.getUserByForm(any(UserForm.class), any(Role.class)))
                .thenReturn(new User());
        when(roleDao.findByName(anyString())).thenReturn(new Role());

        mockMvc.perform(post("/admin/create")
                .params(userFormUtils.createParamsUserForm()))
                .andExpect(status().isFound())
                .andExpect(model().attributeExists("userForm"))
                .andExpect(redirectedUrl("/")).andExpect(model().hasNoErrors());

        verify(userDao, times(1)).create(any(User.class));
    }

    @Test
    public void testCreateInvalidForm() throws Exception {
        MultiValueMap<String, String> parametersForm = userFormUtils
                .createParamsUserForm();
        parametersForm.get("password").set(0, "");

        mockMvc.perform(post("/admin/create").params(parametersForm))
                .andExpect(status().isOk()).andExpect(model().hasErrors())
                .andExpect(
                        model().attributeHasFieldErrors("userForm", "password"))
                .andExpect(view().name(View.FROM_CREATE));
    }

    @Test
    public void testCreateLoginExists() throws Exception {
        when(userUtils.isLoginExists(anyString())).thenReturn(true);

        mockMvc.perform(post("/admin/create")
                .params(userFormUtils.createParamsUserForm()))
                .andExpect(status().isOk()).andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("userForm", "login"))
                .andExpect(view().name(View.FROM_CREATE));
    }

    @Test
    public void testCreateEmailExists() throws Exception {
        when(userUtils.isEmailExists(any(UserForm.class))).thenReturn(true);

        mockMvc.perform(post("/admin/create")
                .params(userFormUtils.createParamsUserForm()))
                .andExpect(status().isOk()).andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("userForm", "email"))
                .andExpect(view().name(View.FROM_CREATE));
    }

    @Test
    public void testShowEdit() throws Exception {
        when(userDao.findById(anyInt())).thenReturn(new User());
        when(userUtils.getFormByUser(any(User.class)))
                .thenReturn(new UserForm());

        mockMvc.perform(get("/admin/edit").param("person_id", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userForm"))
                .andExpect(view().name(View.FROM_EDIT));

        verify(userDao, times(1)).findById(1);
    }

    @Test
    public void testEdit() throws Exception {
        when(userUtils.isEmailExists(any(UserForm.class))).thenReturn(false);
        when(userUtils.getUserByForm(any(UserForm.class), any(Role.class)))
                .thenReturn(new User());
        when(roleDao.findByName(anyString())).thenReturn(new Role());
        when(userDao.findByLogin(anyString())).thenReturn(new User());

        mockMvc.perform(post("/admin/edit")
                .params(userFormUtils.createParamsUserForm()))
                .andExpect(status().isFound())
                .andExpect(model().attributeExists("userForm"))
                .andExpect(redirectedUrl("/")).andExpect(model().hasNoErrors());

        verify(userDao, times(1)).update(any(User.class));
    }

    @Test
    public void testEditInvalidForm() throws Exception {
        MultiValueMap<String, String> parametersForm = userFormUtils
                .createParamsUserForm();
        parametersForm.get("birthDay").set(0, "");

        mockMvc.perform(post("/admin/edit").params(parametersForm))
                .andExpect(status().isOk()).andExpect(model().hasErrors())
                .andExpect(
                        model().attributeHasFieldErrors("userForm", "birthDay"))
                .andExpect(view().name(View.FROM_EDIT));
    }

    @Test
    public void testEditEmailExists() throws Exception {
        when(userUtils.isEmailExists(any(UserForm.class))).thenReturn(true);

        mockMvc.perform(post("/admin/edit")
                .params(userFormUtils.createParamsUserForm()))
                .andExpect(status().isOk()).andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("userForm", "email"))
                .andExpect(view().name(View.FROM_EDIT));
    }

    @Test
    public void testDelete() throws Exception {
        when(userDao.findById(anyLong())).thenReturn(new User());

        mockMvc.perform(get("/admin/delete").param("person_id", "1"))
                .andExpect(status().isFound()).andExpect(redirectedUrl("/"));

        verify(userDao, times(1)).findById(1);
        verify(userDao, times(1)).remove(any(User.class));
    }
}