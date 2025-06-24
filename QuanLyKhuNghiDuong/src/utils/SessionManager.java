package utils;

import Models.NguoiDung;

public class SessionManager {
    private static SessionManager instance;
    private NguoiDung currentUser;

    private SessionManager() {
        
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(NguoiDung user) {
        this.currentUser = user;
    }

    public NguoiDung getCurrentUser() {
        return this.currentUser;
    }

    public boolean isLoggedIn() {
        return this.currentUser != null;
    }

    public void logout() {
        this.currentUser = null;
    }
}