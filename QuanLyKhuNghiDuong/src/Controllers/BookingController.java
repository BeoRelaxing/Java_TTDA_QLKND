/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import DAO.BookingDAO;
import DAO.NotificationDAO;
import Models.Booking;
import Models.Notification;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Beo
 */
public class BookingController {

    private BookingDAO dao = new BookingDAO();

    // Lấy tất cả các booking
    public List<Booking> getAllBookings() {
        return dao.getAllBookings();
    }

    // Xác nhận booking
    // Xác nhận booking
    public boolean confirmBooking(int bookingId) {
        boolean result = dao.updateBookingStatus(bookingId, "checkedin");
        if (result) {
            Booking booking = dao.getBookingById(bookingId);
            Notification noti = new Notification(
                    booking.getUserId(),
                    "Đặt phòng đã được xác nhận",
                    "Đặt phòng #" + bookingId + " đã được xác nhận. Vui lòng đến đúng giờ.",
                    new Date(),
                    false
            );
            NotificationDAO notificationDAO = new NotificationDAO();
            notificationDAO.sendNotification(noti);
        }
        return result;
    }

    // Hủy booking
    // Hủy booking
    public boolean cancelBooking(int bookingId) {
        boolean result = dao.cancelBooking(bookingId);
        if (result) {
            Booking booking = dao.getBookingById(bookingId);
            Notification noti = new Notification(
                    booking.getUserId(),
                    "Đặt phòng bị hủy",
                    "Đặt phòng #" + bookingId + " đã bị hủy bởi quản trị viên.",
                    new Date(),
                    false
            );
            NotificationDAO notificationDAO = new NotificationDAO();
            notificationDAO.sendNotification(noti);
        }
        return result;
    }

    // them 
    // Thêm đặt phòng vào database
    public boolean createBooking(Booking booking) {
        return dao.addBooking(booking); // Gọi phương thức thêm booking trong DAO
    }

    public boolean isRoomBooked(int roomId, java.util.Date checkInDate, java.util.Date checkOutDate) {
        // Chuyển đổi java.util.Date sang java.sql.Date
        java.sql.Date sqlCheckInDate = new java.sql.Date(checkInDate.getTime());
        java.sql.Date sqlCheckOutDate = new java.sql.Date(checkOutDate.getTime());
        return dao.isRoomBooked(roomId, sqlCheckInDate, sqlCheckOutDate);
    }

    public boolean isBookingTimeConflict(int bookingId) {
        Booking booking = dao.getBookingById(bookingId);
        if (booking == null) {
            return true; // Không tìm thấy => báo lỗi
        }
        java.sql.Date checkIn = new java.sql.Date(booking.getCheckInDate().getTime());
        java.sql.Date checkOut = new java.sql.Date(booking.getCheckOutDate().getTime());

        return dao.isRoomBooked(booking.getRoomId(), checkIn, checkOut);
    }

    public List<Booking> getBookingsByUser(int userId) {
        return dao.getBookingsByUserId(userId);
    }
    // Lấy các đặt phòng đang được checked-in của người dùng

    public List<Booking> getCheckedInBookings(int userId) {
        return dao.getCheckedInBookingsByUserId(userId);
    }

// Xóa đặt phòng
    public boolean deleteBooking(int bookingId) {
        return dao.deleteBooking(bookingId);
    }
    
    public String getBookingStatus(int bookingId) {
        return dao.getBookingStatus(bookingId);
    }

    public int getNumberOfNights(int bookingId) {
    return dao.getNumberOfNights(bookingId);
}

   public List<Booking> getBookingsByRoomId(int roomId) {
        return dao.getBookingsByRoomId(roomId);
    }
}
