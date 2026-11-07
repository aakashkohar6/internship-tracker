package com.internshiptracker;

import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserRepository {

    private final DataSource dataSource;

    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash FROM users WHERE username = ?";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1,username);

            try(ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    int id = resultSet.getInt("id");
                    String foundUsername = resultSet.getString("username");
                    String passwordHash = resultSet.getString("password_hash");
                    User user = new User(id, foundUsername,passwordHash);
                    return user;
                }
            }
        }
        return null;
    }

    public void save(String username, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?);";

        try(Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, username);
            statement.setString(2,passwordHash);

            try {
                statement.executeUpdate();
            }
            catch (SQLException exception){
                if(exception.getErrorCode() == 1062){
                    throw new DuplicateUsernameException("username already taken try something else");
                }
                throw exception;
            }
        }
    }
}
