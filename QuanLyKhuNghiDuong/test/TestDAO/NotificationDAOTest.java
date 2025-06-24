package TestDAO;

import DAO.NotificationDAO;
import Models.Notification;
import org.junit.*;
import utils.DBConnection;

import java.sql.*;
import java.util.*;

import static org.junit.Assert.*;

public class NotificationDAOTest {
    private static NotificationDAO notificationDAO;
    private static int testUserId;
    private static Connection conn;

    @BeforeClass
    public static void setup() throws SQLException {
        conn = DBConnection.getConnection();
        notificationDAO = new NotificationDAO();

        // Tạo user test mới và lấy ra user_id
        String sql = "INSERT INTO Users (name, email, password_hash, phone, role, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "TestUser");
            stmt.setString(2, "test@example.com");
            stmt.setString(3, "123456");
            stmt.setString(4, "0000000000");
            stmt.setString(5, "customer");
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                testUserId = rs.getInt(1);
            } else {
                throw new SQLException("Không lấy được user_id sau khi tạo User test.");
            }
        }
    }

    @AfterClass
    public static void cleanup() throws SQLException {
        // Xóa các thông báo của user test
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Notifications WHERE user_id = ?")) {
            stmt.setInt(1, testUserId);
            stmt.executeUpdate();
        }

        // Xóa user test (đảm bảo không còn ràng buộc nào khác liên kết)
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Users WHERE user_id = ?")) {
            stmt.setInt(1, testUserId);
            stmt.executeUpdate();
        }
    }

    @Test
    public void testSendNotification() {
        Notification noti = new Notification(testUserId, "Test Title", "Test Message", new java.util.Date());
        boolean result = notificationDAO.sendNotification(noti);
        assertTrue(result);
    }

    @Test
    public void testGetAllNotifications() {
        Notification noti = new Notification(testUserId, "Another Title", "Another Message", new java.util.Date());
        notificationDAO.sendNotification(noti);

        List<Notification> list = notificationDAO.getAllNotifications();
        assertNotNull(list);
        assertTrue(list.stream()
            .anyMatch(n -> n.getUserId() == testUserId && n.getTitle().equals("Another Title")));
    }

    @Test
    public void testMarkNotificationAsRead() {
        Notification noti = new Notification(testUserId, "Mark Read", "Message", new java.util.Date());
        notificationDAO.sendNotification(noti);

        List<Notification> list = notificationDAO.getAllNotifications();
        Optional<Notification> optional = list.stream()
            .filter(n -> n.getUserId() == testUserId && n.getTitle().equals("Mark Read"))
            .findFirst();

        assertTrue(optional.isPresent());

        boolean result = notificationDAO.markNotificationAsRead(optional.get().getNotificationId());
        assertTrue(result);
    }

    @Test
    public void testDeleteNotification() {
        Notification noti = new Notification(testUserId, "To Delete", "Message", new java.util.Date());
        notificationDAO.sendNotification(noti);

        List<Notification> list = notificationDAO.getAllNotifications();
        Optional<Notification> optional = list.stream()
            .filter(n -> n.getUserId() == testUserId && n.getTitle().equals("To Delete"))
            .findFirst();

        assertTrue(optional.isPresent());

        boolean deleted = notificationDAO.deleteNotification(optional.get().getNotificationId());
        assertTrue(deleted);
    }
}
