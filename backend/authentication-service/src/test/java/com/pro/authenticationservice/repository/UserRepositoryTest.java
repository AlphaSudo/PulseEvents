package com.pro.authenticationservice.repository;


import com.pro.authenticationservice.model.User;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.cloud.config.uri=",                // if you have a URI defined elsewhere
        }
)

// use the in-memory test database instead of any real one
@AutoConfigureTestDatabase(replace = Replace.ANY)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByUsername should return the user when found")
    void whenFindByUsername_thenReturnUser() {
        // given
        User user = new User();
        user.setUsername("john");
        user.setPassword("secret");
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findByUsername("john");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("john");
    }

    @Test
    @DisplayName("findByUsername should return empty when user not found")
    void whenFindByUsernameNotExist_thenReturnEmpty() {
        Optional<User> found = userRepository.findByUsername("does_not_exist");
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("existsByUsername should return true when user exists")
    void whenExistsByUsername_thenReturnTrue() {
        // given
        User user = new User();
        user.setUsername("alice");
        user.setPassword("pwd");
        entityManager.persistAndFlush(user);

        // when
        boolean exists = userRepository.existsByUsername("alice");

        // then
        assertTrue(exists);
    }

    @Test
    @DisplayName("existsByUsername should return false when user does not exist")
    void whenExistsByUsername_thenReturnFalse() {
        boolean exists = userRepository.existsByUsername("unknown");
        assertFalse(exists);
    }
}
