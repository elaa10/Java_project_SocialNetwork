package com.example.map_toysocialnetwork.repository.dataBase;

import com.example.map_toysocialnetwork.domain.Friendship;
import com.example.map_toysocialnetwork.domain.Message;
import com.example.map_toysocialnetwork.domain.Tuple;
import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.domain.validators.Validator;
import com.example.map_toysocialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDataBaseRepository implements Repository<Long, Message> {
    private String url;
    private String user;
    private String password;
    private Validator<Message> validator;
    private UserDataBaseRepository userDataBaseRepository;

    public MessageDataBaseRepository(String url, String user, String password, Validator<Message> validator, UserDataBaseRepository userDataBaseRepository) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.validator = validator;
        this.userDataBaseRepository = userDataBaseRepository;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public Optional<Message> findOne(Long id) {
        String query = "SELECT * FROM messages WHERE id = ?";
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

    private Message extractEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Long from = resultSet.getLong("from_id");
        Long to = resultSet.getLong("to_id");
        String message = resultSet.getString("message");
        LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
        Long idReply = resultSet.getLong("id_reply");
        User from_user = userDataBaseRepository.findOne(from).get();
        User to_user = userDataBaseRepository.findOne(to).get();
        Message m = new Message(from_user, to_user, message, idReply);
        m.setId(id);
        m.setDate(date);
        return m;
    }

    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages";
        try(Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {
            while(resultSet.next()) {
                messages.add(extractEntity(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error" + e.getMessage());
        }
        return messages;
    }

    public Optional<Message> save(Message message) {
        validator.validate(message);
        String query = "INSERT INTO messages (from_id, to_id, message, date, id_reply) VALUES (?,?,?,?,?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // statement.setLong(1, user.getId());
            statement.setLong(1, message.getFrom().getId());
            statement.setLong(2, message.getTo().getId());
            statement.setString(3, message.getMessage());
            statement.setTimestamp(4, Timestamp.valueOf(message.getDate()));
            statement.setLong(5, message.getIdReply());
            int rowInserted = statement.executeUpdate();
            return rowInserted == 1 ? Optional.empty() : Optional.of(message);
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error" + e.getMessage());
        }
    }

    public Optional<Message> delete(Long id) {
        Optional<Message> message = findOne(id);
        if(message.isPresent()) {
            String query = "DELETE FROM messages WHERE id = ?";
            try(Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1,id);
                statement.executeUpdate();
            }catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Database error" + e.getMessage());
            }
        }
        return message;
    }

    @Override
    public Optional<Message> update(Message message) {
        validator.validate(message);
        String query = "UPDATE messages SET message = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, message.getFrom().getId());
            statement.setLong(2, message.getTo().getId());
            statement.setString(3, message.getMessage());
            statement.setTimestamp(4, Timestamp.valueOf(message.getDate()));
            statement.setLong(5, message.getIdReply());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated == 1 ? Optional.empty() : Optional.of(message);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }
}

