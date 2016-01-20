package com.nixsolutions.ponarin.web_service.soap.impl;

import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;

import com.nixsolutions.ponarin.dao.UserDao;
import com.nixsolutions.ponarin.entity.User;
import com.nixsolutions.ponarin.web_service.soap.SoapUserService;

@WebService(endpointInterface = "com.nixsolutions.ponarin.web_service.soap.SoapUserService")
public class SoapUserServiceImpl implements SoapUserService {

    @Autowired
    private UserDao userDao;

    @Override
    public void create(User user) {
        userDao.create(user);

    }

    @Override
    public void update(User user) {
        userDao.update(user);

    }

    @Override
    public void remove(User user) {
        userDao.remove(user);

    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public User findById(long id) {
        return userDao.findById(id);
    }

    @Override
    public User findByLogin(String login) {
        return userDao.findByLogin(login);
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }
}
