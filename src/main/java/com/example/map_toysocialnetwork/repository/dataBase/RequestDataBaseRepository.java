package com.example.map_toysocialnetwork.repository.dataBase;

import com.example.map_toysocialnetwork.domain.Friendship;
import com.example.map_toysocialnetwork.domain.FriendshipRequest;
import com.example.map_toysocialnetwork.domain.Tuple;
import com.example.map_toysocialnetwork.domain.validators.FriendshipValidator;
import com.example.map_toysocialnetwork.domain.validators.Validator;
import com.example.map_toysocialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class RequestDataBaseRepository implements Repository<Tuple<Long, Long>, FriendshipRequest> {

    private String url;
    private String user;
    private String password;
    private Validator<FriendshipRequest> validator;

    public RequestDataBaseRepository(String url, String user, String password, Validator<FriendshipRequest> validator) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.validator = validator;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }



    public Optional<FriendshipRequest> findOne(Tuple<Long, Long> id) {
        String query = "SELECT * FROM requests WHERE user_id1 = ? AND user_id2 = ?";
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

    private FriendshipRequest extractEntity(ResultSet resultSet) throws SQLException {
        Long id1 = resultSet.getLong("user_id1");
        Long id2 = resultSet.getLong("user_id2");
        String status = resultSet.getString("status");
        LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
        FriendshipRequest p = new FriendshipRequest(new Tuple<>(id1, id2));
        p.setStatus(status);
        p.setDate(date);
        return p;
    }

    @Override
    public Iterable<FriendshipRequest> findAll() {
        List<FriendshipRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM requests";
        try(Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {
            while(resultSet.next()) {
                requests.add(extractEntity(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error" + e.getMessage());
        }
        return requests;
    }

    @Override
    public Optional<FriendshipRequest> save(FriendshipRequest request)  {
        validator.validate(request);
        String query = "INSERT INTO requests (user_id1, user_id2, status, date) VALUES (?, ?,?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, request.getId().getLeft());
            statement.setLong(2, request.getId().getRight());
            statement.setString(3, request.getStatus());
            statement.setTimestamp(4, Timestamp.valueOf(request.getDate()));
            int rowInserted = statement.executeUpdate();
            return rowInserted == 1 ? Optional.empty() : Optional.of(request);
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error" + e.getMessage());
        }
    }

    @Override
    public Optional<FriendshipRequest> delete(Tuple<Long, Long> id) {
        Optional<FriendshipRequest> request = findOne(id);
        if(request.isPresent()) {
            String query = "DELETE FROM requests WHERE user_id1 = ? AND user_id2 = ?";
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
        return request;
    }

    @Override
    public Optional<FriendshipRequest> update(FriendshipRequest request) {
        validator.validate(request);
        String query = "UPDATE requests SET status = ? WHERE user_id1 = ? AND user_id2 = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, request.getStatus());
            statement.setLong(2, request.getId().getLeft());
            statement.setLong(3, request.getId().getRight());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated == 1 ? Optional.empty() : Optional.of(request);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }
}

