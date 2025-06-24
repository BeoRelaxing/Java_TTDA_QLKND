/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Models.Room;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Beo
 */
public class RoomDAO {

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Rooms";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Room r = new Room();
                r.setRoomId(rs.getInt("room_id"));
                r.setResortId(rs.getInt("resort_id"));
                r.setRoomNumber(rs.getString("room_number"));
                r.setRoomType(rs.getString("room_type"));
                r.setPricePerNight(rs.getDouble("price_per_night"));
                r.setStatus(rs.getString("status"));
                r.setCapacity(rs.getInt("capacity"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addRoom(Room room) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO Rooms (resort_id, room_number, room_type, price_per_night, status, capacity) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, room.getResortId());
            ps.setString(2, room.getRoomNumber());
            ps.setString(3, room.getRoomType());
            ps.setDouble(4, room.getPricePerNight());
            ps.setString(5, room.getStatus());
            ps.setInt(6, room.getCapacity());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRoom(Room room) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Rooms SET room_number = ?, room_type = ?, price_per_night = ?, status = ?, capacity = ? WHERE room_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getRoomType());
            ps.setDouble(3, room.getPricePerNight());
            ps.setString(4, room.getStatus());
            ps.setInt(5, room.getCapacity());
            ps.setInt(6, room.getRoomId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String[]> getRoomStatusHistory(int roomId) {
        List<String[]> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT status, updated_at FROM Room_Status_History WHERE room_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new String[]{rs.getString("status"), rs.getString("updated_at")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // đếm
    public int getRoomCount() {
        int count = 0;
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM Rooms";
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

    // chuc nang khach hang
    public List<Room> searchRooms(int resortId, String roomType, double maxPrice, String status) {
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT * FROM Rooms WHERE 1=1");

            // Tạo câu lệnh SQL động dựa trên các tiêu chí tìm kiếm
            if (resortId > 0) {
                sql.append(" AND resort_id = ?");
            }
            if (roomType != null && !roomType.isEmpty()) {
                sql.append(" AND room_type = ?");
            }
            if (maxPrice > 0) {
                sql.append(" AND price_per_night <= ?");
            }
            if (status != null && !status.isEmpty()) {
                sql.append(" AND status = ?");
            }

            PreparedStatement ps = conn.prepareStatement(sql.toString());

            int index = 1;
            // Gán giá trị cho PreparedStatement
            if (resortId > 0) {
                ps.setInt(index++, resortId);
            }
            if (roomType != null && !roomType.isEmpty()) {
                ps.setString(index++, roomType);
            }
            if (maxPrice > 0) {
                ps.setDouble(index++, maxPrice);
            }
            if (status != null && !status.isEmpty()) {
                ps.setString(index++, status);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Room r = new Room();
                r.setRoomId(rs.getInt("room_id"));
                r.setResortId(rs.getInt("resort_id"));
                r.setRoomNumber(rs.getString("room_number"));
                r.setRoomType(rs.getString("room_type"));
                r.setPricePerNight(rs.getDouble("price_per_night"));
                r.setStatus(rs.getString("status"));
                r.setCapacity(rs.getInt("capacity"));
                rooms.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public boolean isRoomAvailable(int roomId, Date checkInDate, Date checkOutDate) {
        boolean available = true;
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Bookings WHERE room_id = ? AND (check_in_date < ? AND check_out_date > ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, roomId);
            ps.setDate(2, new java.sql.Date(checkInDate.getTime()));
            ps.setDate(3, new java.sql.Date(checkOutDate.getTime()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                available = false; // Nếu có kết quả, nghĩa là phòng đã được đặt trong khoảng thời gian đó
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return available;
    }
// Lay phong theo id khu nghi duong 

    public List<Room> getRoomsByResortName(String resortName) {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT r.* FROM Rooms r "
                + "JOIN Resorts res ON r.resort_id = res.resort_id "
                + "WHERE res.Name = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, resortName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setRoomType(rs.getString("room_type"));
                room.setPricePerNight(rs.getDouble("price_per_night"));
                room.setStatus(rs.getString("status"));
                room.setCapacity(rs.getInt("capacity"));
                room.setResortId(rs.getInt("resort_id"));
                rooms.add(room);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rooms;
    }

    public boolean updateRoomStatus(int roomId, String status) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Rooms SET status = ? WHERE room_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Room getRoomById(int roomId) {
        Room room = null;
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Rooms WHERE room_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setResortId(rs.getInt("resort_id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setRoomType(rs.getString("room_type"));
                room.setPricePerNight(rs.getDouble("price_per_night"));
                room.setStatus(rs.getString("status"));
                room.setCapacity(rs.getInt("capacity"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return room;
    }

}
