package com.internshiptracker;

import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class InternshipApplicationRepository {
    public InternshipApplication findById(int id) throws SQLException{
        String sql = "SELECT * FROM internship_applications WHERE id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1,id);

            try(ResultSet resultSet = statement.executeQuery();) {
                if (resultSet.next()) {
                    return mapRowToApplication(resultSet);
                }
            }
        }
        return  null;
    }

    public ArrayList<InternshipApplication> findPage
            (ApplicationStatus status, String company,int page, int size) throws SQLException{
        int offset = page * size;
        StringBuilder sql = new StringBuilder("SELECT * FROM internship_applications ");

        if(status != null){
            sql.append(" WHERE status = ?");
        }
        if(company != null){
            if(status == null){
                sql.append(" WHERE company LIKE ?");
            }
            else{
                sql.append(" AND company LIKE ?");
            }
        }

        sql.append(" ORDER BY id DESC LIMIT ? OFFSET ?");
        String sqlString = sql.toString();

        ArrayList<InternshipApplication> applications = new ArrayList<>();

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlString)
        )
        {
            int parameterIndex = 1;
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

    public int count(ApplicationStatus status, String company) throws  SQLException{
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) AS total FROM internship_applications");

        if(status != null){
            sql.append(" WHERE status = ?");
        }
        if(company != null){
            if(status == null){
                sql.append(" WHERE company LIKE ?");
            }
            else{
                sql.append(" AND company LIKE ?");
            }
        }
        String sqlString = sql.toString();
        int count = 0;
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlString)
        ){
            int parameterIndex = 1;
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

        String location = resultSet.getString("location");

        InternshipApplication application = new
                InternshipApplication(applicationId, company,position,status,location);
        return application;
    }

    public void save(InternshipApplication application) throws SQLException{
        String sql =
                "INSERT INTO internship_applications "+ "(company, position, status, location) " +
                 "VALUES(?,?,?,?)";
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            statement.setString(1, application.getCompany());
            statement.setString(2,application.getPosition());
            statement.setString(3, application.getStatus().name());
            statement.setString(4, application.getLocation());

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

    public boolean updateStatus(int id, ApplicationStatus newStatus) throws SQLException {
        String sql = "UPDATE internship_applications " +
                "SET status = ? " + "WHERE id = ? ";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, newStatus.name());
            statement.setInt(2,id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean deleteById(int id) throws SQLException{
        String sql = "DELETE FROM internship_applications " + "WHERE id = ?";
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1,id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
