package com.internshiptracker;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class InternshipApplicationRepository {
    private final DataSource dataSource;

    public InternshipApplicationRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public InternshipApplication findById(int id, int userId) throws SQLException{
        String sql = "SELECT * FROM internship_applications WHERE id = ? AND user_id = ?";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1,id);
            statement.setInt(2,userId);

            try(ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToApplication(resultSet);
                }
            }
        }
        return  null;
    }

    public ArrayList<InternshipApplication> findPage
            (ApplicationStatus status, String company,int page, int size, int userId) throws SQLException{
        int offset = page * size;
        StringBuilder sql = new StringBuilder("SELECT * FROM internship_applications where user_id = ?");

        if(status != null){
            sql.append(" AND status = ?");
        }
        if(company != null){
            sql.append(" AND company LIKE ?");
        }

        sql.append(" ORDER BY id DESC LIMIT ? OFFSET ?");
        String sqlString = sql.toString();

        ArrayList<InternshipApplication> applications = new ArrayList<>();

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlString)
        )
        {
            statement.setInt(1, userId);
            int parameterIndex = 2;
            if(status != null) {
                statement.setString(parameterIndex++, status.name());
            }
            if(company != null) {
                statement.setString(parameterIndex++,"%" + company + "%");
            }
            statement.setInt(parameterIndex++,size);
            statement.setInt(parameterIndex,offset);

            try(ResultSet resultSet = statement.executeQuery()){
                while(resultSet.next()){
                    InternshipApplication application = mapRowToApplication(resultSet);
                    applications.add(application);
                }
            }
        }
        return applications;
    }

    public int count(ApplicationStatus status, String company, int userId) throws  SQLException{
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS total FROM internship_applications WHERE user_id = ?");

        if(status != null){
            sql.append(" AND status = ?");
        }
        if(company != null){
            sql.append(" AND company LIKE ?");
        }
        String sqlString = sql.toString();
        int count = 0;
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlString)
        ){
            statement.setInt(1,userId);
            int parameterIndex = 2;
            if(status != null){
                statement.setString(parameterIndex++,status.name());
            }
            if(company != null){
                statement.setString(parameterIndex,"%" + company + "%");
            }
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    count = resultSet.getInt("total");
                }
            }
        }
        return count;
    }

    private InternshipApplication mapRowToApplication(ResultSet resultSet) throws SQLException{

        int applicationId = resultSet.getInt("id");
        String company = resultSet.getString("company");
        String position = resultSet.getString("position");

        ApplicationStatus status =
                ApplicationStatus.valueOf(resultSet.getString("status"));
        LocalDate applicationDate = resultSet.getObject("application_date", LocalDate.class);

        String location = resultSet.getString("location");

        InternshipApplication application = new
                InternshipApplication(applicationId, company,position,status,location, applicationDate);
        return application;
    }

    public void save(InternshipApplication application, int userId) throws SQLException{
        String sql =
                "INSERT INTO internship_applications "+
                        "(company, position, status, location, application_date, user_id) " +
                 "VALUES(?,?,?,?,?,?)";
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS))
        {
            statement.setString(1, application.getCompany());
            statement.setString(2,application.getPosition());
            statement.setString(3, application.getStatus().name());
            statement.setString(4, application.getLocation());
            statement.setObject(5, application.getApplicationDate());
            statement.setInt(6, userId);

            statement.executeUpdate();

            try(ResultSet generatedKeys = statement.getGeneratedKeys()){
                if(generatedKeys.next()){
                    int generatedId = generatedKeys.getInt(1);
                    application.setId(generatedId);
                }
                else {
                    throw new SQLException("Failed to retrieve generated application ID.");
                }
            }
        }
    }

    public boolean updateStatus(int id, ApplicationStatus newStatus, int userId) throws SQLException {
        String sql = "UPDATE internship_applications " +
                "SET status = ? " + "WHERE id = ? AND user_id = ? ";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, newStatus.name());
            statement.setInt(2,id);
            statement.setInt(3,userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean deleteById(int id, int userId) throws SQLException{
        String sql = "DELETE FROM internship_applications " + "WHERE id = ? AND user_id = ?";
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1,id);
            statement.setInt(2,userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
