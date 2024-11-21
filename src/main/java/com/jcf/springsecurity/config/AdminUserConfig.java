package com.jcf.springsecurity.config;

import com.jcf.springsecurity.entities.Role;
import com.jcf.springsecurity.entities.User;
import com.jcf.springsecurity.repository.RoleRepository;
import com.jcf.springsecurity.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public AdminUserConfig(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());

        var userAdmin = userRepository.findByUsername("admin");

        userAdmin.ifPresentOrElse(
                user -> {
                    System.out.println("Admin " +  user.getUsername()  + " já existe");
                },
                () -> {
                    var user = new User();
                    user.setUsername("admin");
                    var password = "123";
                    user.setPassword(passwordEncoder.encode(password));
                    user.setRoles(Set.of(roleAdmin));
                    userRepository.save(user);
                }
        );


    }
}
