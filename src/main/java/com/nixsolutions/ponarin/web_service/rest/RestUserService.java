package com.nixsolutions.ponarin.web_service.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.nixsolutions.ponarin.dao.UserDao;
import com.nixsolutions.ponarin.entity.User;
import com.nixsolutions.ponarin.utils.UserUtils;

@RestController
@RequestMapping("/users")
public class RestUserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserUtils userUtils;

    @RequestMapping(method = RequestMethod.POST, headers = "Content-Type=application/json")
    public ResponseEntity<Void> createUser(@RequestBody User user) {
        if (userUtils.isLoginExists(user.getLogin())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        if (userUtils.isEmailExists(userUtils.getFormByUser(user))) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        userDao.create(user);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Content-Type=application/json")
    public ResponseEntity<Void> updateUser(@PathVariable long id,
            @RequestBody User user) {
        if (userUtils.isEmailExists(userUtils.getFormByUser(user))) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        User dbUser = userDao.findById(id);

        if (dbUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        user.setId(id);
        userDao.update(user);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        User user = userDao.findById(id);

        if (user != null) {
            userDao.remove(user);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json")
    public @ResponseBody User[] findAll() {
        List<User> userList = userDao.findAll();
        User[] usersForReturn = new User[userList.size()];
        return userList.toArray(usersForReturn);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    public @ResponseBody User findById(@PathVariable long id) {
        return userDao.findById(id);
    }
}
