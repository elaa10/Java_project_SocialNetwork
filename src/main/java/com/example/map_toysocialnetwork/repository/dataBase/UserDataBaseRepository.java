package com.example.map_toysocialnetwork.repository.dataBase;

import com.example.map_toysocialnetwork.domain.Friendship;
import com.example.map_toysocialnetwork.domain.Tuple;
import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.domain.validators.FriendshipValidator;
import com.example.map_toysocialnetwork.domain.validators.Validator;
import com.example.map_toysocialnetwork.repository.Repository;
import com.example.map_toysocialnetwork.repository.UserRepository;
import com.example.map_toysocialnetwork.utils.paging.Page;
import com.example.map_toysocialnetwork.utils.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDataBaseRepository implements UserRepository {
    private String url;
    private String user;
    private String password;
    private Validator<User> validator;

    public UserDataBaseRepository(String url, String user, String password, Validator<User> validator) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.validator = validator;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public Optional<User> findOne(Long id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return Optional.of(extractEntity(resultSet));
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error" + e.getMessage());
        }
        return Optional.empty();
    }

    private User extractEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        User u = new User(username, password,firstName,lastName);
        u.setId(id);
        return u;
    }

    public Optional<User> findOneUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return Optional.of(extractEntity(resultSet));
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error" + e.getMessage());
        }
        return Optional.empty();
    }

    public Iterable<User> findAll() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try(Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {
            while(resultSet.next()) {
                users.add(extractEntity(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error" + e.getMessage());
        }
        return users;
    }

    public Optional<User> save(User user) {
        validator.validate(user);
        for(User u : findAll()) {
            if(u.getUsername().equals(user.getUsername())) {
                return Optional.of(user);
            }
        }
        String query = "INSERT INTO users (username, password, first_name, last_name) VALUES (?,?,?,?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // statement.setLong(1, user.getId());
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getFirstName());
            statement.setString(4, user.getLastName());
            int rowInserted = statement.executeUpdate();
            return rowInserted == 1 ? Optional.empty() : Optional.of(user);
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error" + e.getMessage());
        }
    }

    public Optional<User> delete(Long id) {
        Optional<User> user = findOne(id);
        if(user.isPresent()) {
            String query = "DELETE FROM users WHERE id = ?";
            try(Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1,id);
                statement.executeUpdate();
            }catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Database error" + e.getMessage());
            }
        }
        return user;
    }

    @Override
    public Optional<User> update(User user) {
        validator.validate(user);
        String query = "UPDATE users SET first_name = ?, last_name = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setLong(3, user.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated == 1 ? Optional.empty() : Optional.of(user);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    @Override
    public Page<User> findAllOnPage(Pageable pageable) {
        return null;
    }


    private int count(Connection connection, Long user_id) throws SQLException {
        String query = "SELECT COUNT(*) AS total " +
                "FROM friendships " +
                "WHERE user_id1 = ? OR user_id2 = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, user_id);
            statement.setLong(2, user_id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }
        }
        return 0;
    }

    private List<User> findAllOnPage(Connection connection, Pageable pageable, Long user_id) throws SQLException {
        List<User> usersOnPage = new ArrayList<>();
        String sql = """
        SELECT u.id, u.username, u.password, u.first_name, u.last_name
        FROM users u
        INNER JOIN friendships f ON (u.id = f.user_id1 AND f.user_id2 = ?) 
                                  OR (u.id = f.user_id2 AND f.user_id1 = ?)
        ORDER BY u.id
        LIMIT ? OFFSET ?;
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, user_id); // pentru prima coloană
            statement.setLong(2, user_id); // pentru a doua coloană
            statement.setInt(3, pageable.getPageSize()); // limita de utilizatori pe pagină
            statement.setInt(4, pageable.getPageNumber() * pageable.getPageSize()); // offset-ul bazat pe numărul paginii

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    usersOnPage.add(extractEntity(resultSet));
                }
            }
        }

        return usersOnPage;
    }

    @Override
    public Page<User> findAllOnPage(Pageable pageable, Long user_id) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            int totalNumberOfUsers = count(connection, user_id);
            List<User> usersOnPage;
            if (totalNumberOfUsers > 0) {
                usersOnPage = findAllOnPage(connection, pageable, user_id);
            } else {
                usersOnPage = new ArrayList<>();
            }
            return new Page<>(usersOnPage, totalNumberOfUsers);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
