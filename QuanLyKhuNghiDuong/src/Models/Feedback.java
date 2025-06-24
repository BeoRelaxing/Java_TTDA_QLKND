package Models;

import java.util.Date;

public class Feedback {
    private int id;         
    private int userId;
    private String content;
    private Date createdAt;

    public Feedback() {
    }

    public Feedback(int userId, String content) {
        this.userId = userId;
        this.content = content;
        this.createdAt = new Date();
    }

    public Feedback(int id, int userId, String content, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getter và Setter
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
