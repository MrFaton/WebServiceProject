package com.nixsolutions.ponarin.web_service.soap;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.nixsolutions.ponarin.entity.Role;
import com.nixsolutions.ponarin.entity.User;

@WebService
public class SoapUserService {
    
    @WebMethod
    public User getUser() {
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
}
