package Models;

import java.util.Date;

public class Notification {
    private int notificationId;
    private int userId;
    private String title;
    private String message;
    private Date sentAt;
    private boolean isRead;

    // Constructor đầy đủ (xài trong getAllNotifications)
    public Notification(int notificationId, int userId, String title, String message, Date sentAt, boolean isRead) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.sentAt = sentAt;
        this.isRead = isRead;
    }

    // Constructor ngắn (xài khi gửi)
    public Notification(int userId, String title, String message, Date sentAt) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.sentAt = sentAt;
        this.isRead = false;
    }

    public Notification(int userId, String title, String message, Date sentAt, boolean isRead) {
    this.userId = userId;
    this.title = title;
    this.message = message;
    this.sentAt = sentAt;
    this.isRead = isRead;
}
    // Getter
    public int getNotificationId() {
        return notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public boolean isRead() {
        return isRead;
    }

    // Setter (chỉ cần userId khi gửi)
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
