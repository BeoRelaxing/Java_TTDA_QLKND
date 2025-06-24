package DAO;
import Models.ServiceBooking;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceBookingDAO {
    public List<ServiceBooking> getAllServiceBookings() {
        List<ServiceBooking> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM service_bookings";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ServiceBooking sb = new ServiceBooking(
                        rs.getInt("service_booking_id"),
                        rs.getInt("booking_id"),
                        rs.getInt("service_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("total_price")
                );
                list.add(sb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateServiceBooking(ServiceBooking sb) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE service_bookings SET booking_id = ?, service_id = ?, quantity = ?, total_price = ? WHERE service_booking_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, sb.getBookingId());
            ps.setInt(2, sb.getServiceId());
            ps.setInt(3, sb.getQuantity());
            ps.setDouble(4, sb.getTotalPrice());
            ps.setInt(5, sb.getServiceBookingId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean addServiceBooking(ServiceBooking sb) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO service_bookings (booking_id, service_id, quantity, total_price) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, sb.getBookingId());
            ps.setInt(2, sb.getServiceId());
            ps.setInt(3, sb.getQuantity());
            ps.setDouble(4, sb.getTotalPrice());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getBookingStatus(int bookingId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT status FROM bookings WHERE booking_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getServicePrice(int serviceId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT price FROM services WHERE service_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, serviceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("price");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public boolean deleteServiceBooking(int serviceBookingId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM service_bookings WHERE service_booking_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, serviceBookingId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<ServiceBooking> getServiceBookingsByUserId(int userId) {
        List<ServiceBooking> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT sb.* FROM service_bookings sb " +
                        "JOIN bookings b ON sb.booking_id = b.booking_id " +
                        "WHERE b.user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ServiceBooking sb = new ServiceBooking(
                        rs.getInt("service_booking_id"),
                        rs.getInt("booking_id"),
                        rs.getInt("service_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("total_price")
                );
                list.add(sb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}