package br.com.uchoa.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity<UserModel> create(@RequestBody UserModel user) {
        //verify if username already exists
        var users = userRepository.findByUsername(user.getUsername());
        if (users.size() > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        //criptografa a senha
        user.setPassword(BCrypt.withDefaults().hashToString(12 , user.getPassword().toCharArray()));

        var userCreated = userRepository.save(user);
        return  ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }
}
