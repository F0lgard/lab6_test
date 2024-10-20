package com.example.lab4;

import com.example.lab4.model.User;
import com.example.lab4.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
public class RepositoryTest {

    @Autowired
    UserRepository underTest;

    @BeforeEach
    void setUp() {
        User user1 = new User("1", "Alice Johnson", "CODE123", "alice@gmail.com", "password1");
        User user2 = new User("2", "Bob Smith", "CODE456", "bob@urk.net", "password2");
        User user3 = new User("3", "Charlie Brown", "CODE789", "charlie@gmail.com", "password3");
        underTest.saveAll(List.of(user1, user2, user3));
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void testSetShouldContain3Records() {
        List<User> users = underTest.findAll();
        assertEquals(3, users.size());
    }

    @Test
    void shouldAssignIdToNewUser() {
        User newUser = new User("David Green", "CODE999", "david@example.com", "password4");
        underTest.save(newUser);

        User userFromDb = underTest.findByCode("CODE999").orElse(null);
        assertNotNull(userFromDb);
        assertNotNull(userFromDb.getId());
        assertFalse(userFromDb.getId().isEmpty());
        assertEquals(24, userFromDb.getId().length());
    }

    @Test
    void shouldFindByCode() {
        User user = underTest.findByCode("CODE456").orElse(null);
        assertNotNull(user);
        assertEquals("Bob Smith", user.getName());
    }

    @Test
    void shouldDeleteUserById() {
        User user = underTest.findByCode("CODE789").orElse(null);
        assertNotNull(user);

        underTest.deleteById(user.getId());

        Optional<User> deletedUser = underTest.findById(user.getId());
        assertTrue(deletedUser.isEmpty());
    }

    @Test
    void shouldFindUsersWithGmailDomain() {
        List<User> users = underTest.findAll().stream()
                .filter(user -> user.getEmail().endsWith("@gmail.com"))
                .toList();

        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(user -> user.getEmail().endsWith("@gmail.com")));
    }
}
