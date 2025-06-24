/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Models.Service;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Beo
 */
public class ServiceDAO {

    public List<Service> getAllServices() {
        List<Service> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM services";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Service s = new Service();
                s.setServiceId(rs.getInt("service_id"));
                s.setResortId(rs.getInt("resort_id"));
                s.setName(rs.getString("name"));
                s.setDescription(rs.getString("description"));
                s.setPrice(rs.getDouble("price"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addService(Service service) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO services (resort_id, name, description, price) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, service.getResortId());
            ps.setString(2, service.getName());
            ps.setString(3, service.getDescription());
            ps.setDouble(4, service.getPrice());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updateService(Service service) {
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "UPDATE services SET resort_id = ?, name = ?, description = ?, price = ? WHERE service_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, service.getResortId());
        ps.setString(2, service.getName());
        ps.setString(3, service.getDescription());
        ps.setDouble(4, service.getPrice());
        ps.setInt(5, service.getServiceId());

        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;  // true nếu cập nhật thành công (có ít nhất 1 dòng bị thay đổi)
    } catch (Exception e) {
        e.printStackTrace();
        return false; // lỗi xảy ra thì trả về false
    }
}


    public void deleteService(int serviceId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM services WHERE service_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, serviceId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // đếm 
    public int getServiceCount() {
        int count = 0;
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM Services";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<Service> getServicesByResortName(String resortName) {
    List<Service> services = new ArrayList<>();
    String query = "SELECT s.* FROM Services s " +
                   "JOIN Resorts res ON s.resort_id = res.resort_id " +
                   "WHERE res.Name = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        
        stmt.setString(1, resortName);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Service service = new Service();
            service.setServiceId(rs.getInt("service_id"));
            service.setResortId(rs.getInt("resort_id"));
            service.setName(rs.getString("name"));
            service.setDescription(rs.getString("description"));
            service.setPrice(rs.getDouble("price"));
            services.add(service);
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    return services;
}


    // tim kiem 
    public List<Service> searchServicesByResort(String resortName, String name, Double minPrice, Double maxPrice) {
    List<Service> list = new ArrayList<>();
    StringBuilder query = new StringBuilder(
        "SELECT s.* FROM Services s JOIN Resorts r ON s.resort_id = r.resort_id WHERE r.name = ?"
    );
    if (name != null && !name.isEmpty()) query.append(" AND s.name LIKE ?");
    if (minPrice != null) query.append(" AND s.price >= ?");
    if (maxPrice != null) query.append(" AND s.price <= ?");

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(query.toString())) {

        int index = 1;
        ps.setString(index++, resortName);
        if (name != null && !name.isEmpty()) ps.setString(index++, "%" + name + "%");
        if (minPrice != null) ps.setDouble(index++, minPrice);
        if (maxPrice != null) ps.setDouble(index++, maxPrice);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Service s = new Service();
            s.setServiceId(rs.getInt("service_id"));
            s.setResortId(rs.getInt("resort_id"));
            s.setName(rs.getString("name"));
            s.setDescription(rs.getString("description"));
            s.setPrice(rs.getDouble("price"));
            list.add(s);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}



}
