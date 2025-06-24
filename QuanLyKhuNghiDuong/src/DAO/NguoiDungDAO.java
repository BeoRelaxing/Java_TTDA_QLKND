package DAO;

import Models.NguoiDung;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.DBConnection;

public class NguoiDungDAO {

     // Đăng nhập
    public NguoiDung getUserByLogin(String username, String passwordHash) {
        NguoiDung user = null;
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM Users WHERE name = ? AND password_hash = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = new NguoiDung(
                    rs.getInt("user_id"),
                    rs.getString("name"),  
                    rs.getString("email"),
                    rs.getString("password_hash"),  
                    rs.getString("phone"),
                    rs.getString("role"),
                    rs.getTimestamp("created_at")
                );
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return user;
    }


    // Đăng ký (insert user)
    public boolean insertUser(NguoiDung user) {
    String sql = "INSERT INTO Users (name, email, password_hash, phone, role, created_at) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, user.getUsername());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setString(4, user.getPhone());
        ps.setString(5, user.getRole());
        ps.setTimestamp(6, new Timestamp(user.getCreatedAt().getTime()));

        ps.executeUpdate();
        return true;

    } catch (SQLIntegrityConstraintViolationException ex) {
        System.err.println("Lỗi: Trùng tên người dùng hoặc email: " + ex.getMessage());
        return false;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    // Lấy danh sách tất cả users
    public List<NguoiDung> getAllUsers() {
        List<NguoiDung> users = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM Users";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                NguoiDung user = new NguoiDung(
                    rs.getInt("user_id"),
                    rs.getString("name"),  // Chỉnh sửa từ 'username' thành 'name'
                    rs.getString("email"),
                    rs.getString("password_hash"),  // Chỉnh sửa từ 'password' thành 'password_hash'
                    rs.getString("phone"),
                    rs.getString("role"),
                    rs.getTimestamp("created_at")
                );
                users.add(user);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return users;
    }

    public boolean updateUser(NguoiDung user) {
    try {
        Connection conn = DBConnection.getConnection();
        // ✅ Bổ sung cập nhật mật khẩu
        String sql = "UPDATE Users SET name = ?, email = ?, phone = ?, role = ?, password_hash = ? WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPhone());
        ps.setString(4, user.getRole());
        ps.setString(5, user.getPassword());  // Cập nhật mật khẩu
        ps.setInt(6, user.getId());

        int result = ps.executeUpdate();

        ps.close();
        conn.close();

        return result > 0;
    } catch (Exception ex) {
        ex.printStackTrace();
        return false;
    }
}


    // Xóa user
    public boolean deleteUser(int userId) {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "DELETE FROM Users WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            int result = ps.executeUpdate();

            ps.close();
            conn.close();

            return result > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Hàm kiểm tra username hoặc email tồn tại
    public boolean checkUserExists(String username, String email) {
        boolean exists = false;
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM Users WHERE name = ? OR email = ?";  // 'name' thay vì 'username'
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                exists = true;
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return exists;
    }

    // Lấy số lượng khách hàng
    public int getCustomerCount() {
        int count = 0;
        try {
            // Kết nối database
            java.sql.Connection conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM Users WHERE role = 'customer'";  // Cập nhật lại SQL với cột 'role'
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            java.sql.ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1); // Lấy kết quả đếm
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    // Lấy danh sách khách hàng
    public List<NguoiDung> getAllCustomers() {
        List<NguoiDung> customers = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE role = 'customer'";  // SQL truy vấn người dùng có vai trò 'customer'

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NguoiDung user = new NguoiDung(
                    rs.getInt("user_id"),
                    rs.getString("name"),  // 'name' thay vì 'username'
                    rs.getString("email"),
                    rs.getString("password_hash"),  // 'password' thay vì 'password_hash'
                    rs.getString("phone"),
                    rs.getString("role"),
                    rs.getTimestamp("created_at")
                );
                customers.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Xử lý lỗi
        }

        return customers;
    }
    
    public NguoiDung getUserById(int userId) {
    NguoiDung user = null;
    try {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            user = new NguoiDung(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone"),
                rs.getString("role"),
                rs.getTimestamp("created_at")
            );
        }
        rs.close();
        ps.close();
        conn.close();
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    return user;
}

    // Hàm dùng để test
    public boolean deleteUserByEmail(String email) {
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "DELETE FROM Users WHERE email = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, email);
        int rows = ps.executeUpdate();
        return rows > 0;
    } catch (Exception ex) {
        ex.printStackTrace();
        return false;
    }
}

    
}

