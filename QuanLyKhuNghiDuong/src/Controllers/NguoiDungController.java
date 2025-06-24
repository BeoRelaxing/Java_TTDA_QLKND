package Controllers;

import DAO.NguoiDungDAO;
import Models.NguoiDung;
import java.util.List;

public class NguoiDungController {

    private NguoiDungDAO userDao;

    public NguoiDungController() {
        userDao = new NguoiDungDAO();
    }

    // Đăng nhập
    public NguoiDung login(String username, String passwordHash) {
        if (username == null || passwordHash == null || username.isEmpty() || passwordHash.isEmpty()) {
            return null;
        }
        return userDao.getUserByLogin(username, passwordHash);
    }

    // Đăng ký
    public boolean register(NguoiDung user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null 
            || user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
            return false;
        }
        
        // Kiểm tra nếu tên đăng nhập hoặc email đã tồn tại
        if (checkUserExists(user.getUsername(), user.getEmail())) {
            return false;  // Tên đăng nhập hoặc email đã tồn tại
        }

        return userDao.insertUser(user);
    }

    // Lấy danh sách tất cả user
    public List<NguoiDung> getAllUsers() {
        return userDao.getAllUsers();
    }

    // Cập nhật user
    public boolean updateUser(NguoiDung user) {
        if (user == null || user.getId() == 0) {
            return false;
        }
        return userDao.updateUser(user);
    }

    // Xóa user
    public boolean deleteUser(int userId) {
        if (userId <= 0) {
            return false;
        }
        return userDao.deleteUser(userId);
    }
    
    // Kiểm tra username hoặc email đã tồn tại chưa
    public boolean checkUserExists(String username, String email) {
        return userDao.checkUserExists(username, email);
    }
    
    // Hàm đếm số lượng user role = customer
    public int getCustomerCount() {
        return userDao.getCustomerCount();
    }
    
    public NguoiDung getUserById(int userId) {
    return userDao.getUserById(userId);  // Gọi xuống DAO
}
}
