package DAO;

import Models.Resort;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResortDAO {

    public List<Resort> getAllResorts() {
        List<Resort> list = new ArrayList<>();
        String sql = "SELECT * FROM Resorts";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Resort> searchResorts(String keyword) {
        List<Resort> list = new ArrayList<>();
        String sql = "SELECT * FROM Resorts WHERE name LIKE ? OR location LIKE ? OR type LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Resort getResortById(int id) {
        String sql = "SELECT * FROM Resorts WHERE resort_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addResort(Resort resort) {
        String sql = "INSERT INTO Resorts (name, location, type, description, price_range, amenities, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, resort.getName());
            stmt.setString(2, resort.getLocation());
            stmt.setString(3, resort.getType());
            stmt.setString(4, resort.getDescription());
            stmt.setString(5, resort.getPriceRange());
            stmt.setString(6, resort.getAmenities());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateResort(Resort resort) {
        String sql = "UPDATE Resorts SET name=?, location=?, type=?, description=?, price_range=?, amenities=? WHERE resort_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, resort.getName());
            stmt.setString(2, resort.getLocation());
            stmt.setString(3, resort.getType());
            stmt.setString(4, resort.getDescription());
            stmt.setString(5, resort.getPriceRange());
            stmt.setString(6, resort.getAmenities());
            stmt.setInt(7, resort.getResortId());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteResort(int id) {
        String sql = "DELETE FROM Resorts WHERE resort_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Resort mapResultSet(ResultSet rs) throws SQLException {
        Resort r = new Resort();
        r.setResortId(rs.getInt("resort_id"));
        r.setName(rs.getString("name"));
        r.setLocation(rs.getString("location"));
        r.setType(rs.getString("type"));
        r.setDescription(rs.getString("description"));
        r.setPriceRange(rs.getString("price_range"));
        r.setAmenities(rs.getString("amenities"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
    // đếm 
    public int countResorts() {
    String sql = "SELECT COUNT(*) FROM Resorts";
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        if (rs.next()) {
            return rs.getInt(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0;
}
    // custommer 
    // 1. search
     public List<Resort> searchResortsWithoutIdAndCreatedAt(String keyword) {
        List<Resort> list = new ArrayList<>();
        String sql = "SELECT name, location, type, description, price_range, amenities FROM Resorts WHERE name LIKE ? OR location LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Resort resort = new Resort();
                resort.setName(rs.getString("name"));
                resort.setLocation(rs.getString("location"));
                resort.setType(rs.getString("type"));
                resort.setDescription(rs.getString("description"));
                resort.setPriceRange(rs.getString("price_range"));
                resort.setAmenities(rs.getString("amenities"));
                list.add(resort);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
}
