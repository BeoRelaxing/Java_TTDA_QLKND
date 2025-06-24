package Controllers;

import DAO.NotificationDAO;
import Models.Notification;
import java.util.Date;
import java.util.List;

public class NotificationController {
    private NotificationDAO notificationDAO;

    public NotificationController() {
        notificationDAO = new NotificationDAO();
    }

    // Gửi thông báo khuyến mãi đến 1 người dùng
    public boolean sendPromoNotification(int userId, String title, String message) {
        // Lấy thời gian hiện tại làm thời gian gửi thông báo
        Date sendAt = new Date();
        
        // Tạo thông báo khuyến mãi
        Notification notification = new Notification(userId, title, message, sendAt);
        
        // Gửi thông báo và lưu vào cơ sở dữ liệu
        return notificationDAO.sendNotification(notification);
    }
    
    // ✅ Gửi thông báo khuyến mãi cho tất cả người dùng (trừ admin)
    public boolean sendPromoNotificationForAll(String title, String message) {
        // Gọi trực tiếp DAO để nó lo chuyện gửi cho tất cả user
        return notificationDAO.sendNotificationToAllUsersExceptAdmin(title, message);
    }
    
    // Xóa thông báo theo ID
    public boolean deleteNotification(int notificationId) {
        return notificationDAO.deleteNotification(notificationId);
    }

    // Lấy tất cả danh sách thông báo
    public List<Notification> getAllNotifications() {
        return notificationDAO.getAllNotifications();
    }
    
    public boolean markNotificationAsRead(int notificationId) {
    return notificationDAO.markNotificationAsRead(notificationId);
}

}
