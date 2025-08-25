package com.example.map_toysocialnetwork.repository.dataBase;

import com.example.map_toysocialnetwork.domain.Friendship;
import com.example.map_toysocialnetwork.domain.Tuple;
import com.example.map_toysocialnetwork.domain.validators.FriendshipValidator;
import com.example.map_toysocialnetwork.domain.validators.Validator;
import com.example.map_toysocialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipDataBaseRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private String url;
    private String user;
    private String password;
    private Validator<Friendship> validator;

    public FriendshipDataBaseRepository(String url, String user, String password, Validator<Friendship> validator) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.validator = validator;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public Optional<Friendship> findOne(Tuple<Long, Long> id) {
        String query = "SELECT * FROM friendships WHERE user_id1 = ? AND user_id2 = ?";
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
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

    private Friendship extractEntity(ResultSet resultSet) throws SQLException {
        Long id1 = resultSet.getLong("user_id1");
        Long id2 = resultSet.getLong("user_id2");
        LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
        Friendship p = new Friendship(new Tuple<>(id1, id2));
        p.setDate(date);
        return p;
    }

    public Iterable<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();
        String query = "SELECT * FROM friendships";
        try(Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {
            while(resultSet.next()) {
                friendships.add(extractEntity(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error" + e.getMessage());
        }
        return friendships;
    }

    public Optional<Friendship> save(Friendship friendship) {
        validator.validate(friendship);
        String query = "INSERT INTO friendships (user_id1, user_id2, date) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, friendship.getId().getLeft());
            statement.setLong(2, friendship.getId().getRight());
            statement.setTimestamp(3, Timestamp.valueOf(friendship.getDate()));
            int rowInserted = statement.executeUpdate();
            return rowInserted == 1 ? Optional.empty() : Optional.of(friendship);
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error" + e.getMessage());
        }
    }

    public Optional<Friendship> delete(Tuple<Long, Long> id) {
        Optional<Friendship> friendship = findOne(id);
        if(friendship.isPresent()) {
            String query = "DELETE FROM friendships WHERE user_id1 = ? AND user_id2 = ?";
            try(Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, id.getLeft());
                statement.setLong(2, id.getRight());
                statement.executeUpdate();
            }catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Database error" + e.getMessage());
            }
        }
        return friendship;
    }

    @Override
    public Optional<Friendship> update(Friendship friendship) {
        validator.validate(friendship);
        String query = "UPDATE friendships SET date = ? WHERE user_id1 = ? AND user_id2 = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setTimestamp(1, Timestamp.valueOf(friendship.getDate()));
            statement.setLong(2, friendship.getId().getLeft());
            statement.setLong(3, friendship.getId().getRight());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated == 1 ? Optional.empty() : Optional.of(friendship);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

}
