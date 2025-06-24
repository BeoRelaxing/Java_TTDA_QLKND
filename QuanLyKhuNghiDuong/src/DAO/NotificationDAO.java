package DAO;

// Thêm import
import Models.Notification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import utils.DBConnection;

public class NotificationDAO {

    //: Gửi cho 1 người dùng
    public boolean sendNotification(Notification notification) {
        String sql = "INSERT INTO Notifications (user_id, title, message, sent_at, is_read) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getTitle());
            ps.setString(3, notification.getMessage());
            ps.setTimestamp(4, new java.sql.Timestamp(notification.getSentAt().getTime()));
            ps.setBoolean(5, notification.isRead());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Hàm MỚI: Gửi thông báo cho tất cả user (trừ admin)
    public boolean sendNotificationToAllUsersExceptAdmin(String title, String message) {
        List<Integer> userIds = getAllUserIdsExceptAdmin();
        boolean success = true;

        for (int userId : userIds) {
            Notification notification = new Notification(userId, title, message, new Date());
            boolean result = sendNotification(notification);
            if (!result) {
                success = false;
            }
        }

        return success;
    }

    //  Hàm lấy danh sách user_id (trừ admin)
    private List<Integer> getAllUserIdsExceptAdmin() {
        List<Integer> userIds = new ArrayList<>();
        String query = "SELECT user_id FROM Users WHERE role != 'admin'";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                userIds.add(rs.getInt("user_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userIds;
    }

    // ✅ Hàm MỚI: Lấy tất cả thông báo
    public List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM Notifications";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Notification notification = new Notification(
                        rs.getInt("notification_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("message"),
                        rs.getTimestamp("sent_at"),
                        rs.getBoolean("is_read")
                );
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    // ✅ Hàm Xóa
    public boolean deleteNotification(int notificationId) {
        String sql = "DELETE FROM Notifications WHERE notification_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markNotificationAsRead(int notificationId) {
        String sql = "UPDATE Notifications SET is_read = 1 WHERE notification_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
