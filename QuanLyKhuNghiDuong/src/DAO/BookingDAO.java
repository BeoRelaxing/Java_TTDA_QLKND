/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Models.Booking;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Beo
 */
public class BookingDAO {
    // Lấy danh sách tất cả các booking

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Bookings";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking(
                        rs.getInt("booking_id"),
                        rs.getInt("user_id"),
                        rs.getInt("room_id"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date"),
                        rs.getDouble("total_price"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    // Xác nhận đặt phòng (duyệt trạng thái)
    public boolean confirmBooking(int bookingId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Bookings SET status = 'checkedin' WHERE booking_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hủy đặt phòng
    public boolean cancelBooking(int bookingId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Bookings SET status = 'Cancelled' WHERE booking_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //
    public boolean addBooking(Booking booking) {
        String query = "INSERT INTO dbo.Bookings (user_id, room_id, check_in_date, check_out_date, total_price, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getRoomId());
            stmt.setDate(3, new java.sql.Date(booking.getCheckInDate().getTime()));
            stmt.setDate(4, new java.sql.Date(booking.getCheckOutDate().getTime()));
            stmt.setDouble(5, booking.getTotalPrice());
            stmt.setString(6, booking.getStatus());
            stmt.setTimestamp(7, new java.sql.Timestamp(booking.getCreatedAt().getTime()));
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isRoomBooked(int roomId, Date checkInDate, Date checkOutDate) {
        String query = "SELECT COUNT(*) FROM Bookings WHERE room_id = ? AND status = 'checkedin' AND "
                + "NOT (check_out_date <= ? OR check_in_date >= ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            stmt.setDate(2, new java.sql.Date(checkInDate.getTime()));
            stmt.setDate(3, new java.sql.Date(checkOutDate.getTime()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Trả về true nếu có bản ghi với status = 'checkedin' trong khoảng thời gian
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Booking getBookingById(int bookingId) {
        String query = "SELECT * FROM Bookings WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Booking(
                        rs.getInt("booking_id"),
                        rs.getInt("user_id"),
                        rs.getInt("room_id"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date"),
                        rs.getDouble("total_price"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE Bookings SET status = ? WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM Bookings WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking(
                        rs.getInt("booking_id"),
                        rs.getInt("user_id"),
                        rs.getInt("room_id"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date"),
                        rs.getDouble("total_price"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> getCheckedInBookingsByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Bookings WHERE user_id = ? AND status = 'checkedin'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking(
                        rs.getInt("booking_id"),
                        rs.getInt("user_id"),
                        rs.getInt("room_id"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date"),
                        rs.getDouble("total_price"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean deleteBooking(int bookingId) {
        String sql = "DELETE FROM Bookings WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
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
        return null; // Trả về null nếu không tìm thấy
    }

    public int getNumberOfNights(int bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking == null) {
            return 0;
        }
        if (booking.getCheckInDate() == null || booking.getCheckOutDate() == null) {
            return 0;
        }
        long diffMillis = booking.getCheckOutDate().getTime() - booking.getCheckInDate().getTime();
        int nights = (int) (diffMillis / (1000 * 60 * 60 * 24));
        return nights > 0 ? nights : 0;
    }

    public List<Booking> getBookingsByRoomId(int roomId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Bookings WHERE room_id = ? AND status IN (?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            stmt.setString(2, "Pending");
            stmt.setString(3, "checkedin");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking(
                            rs.getInt("booking_id"),
                            rs.getInt("user_id"),
                            rs.getInt("room_id"),
                            rs.getDate("check_in_date"),
                            rs.getDate("check_out_date"),
                            rs.getDouble("total_price"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at")
                    );
                    bookings.add(booking);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }

}
