package Views;

import Controllers.BookingController;
import Models.Booking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BookingFormFrame extends JFrame {

    private JTextField txtUserId;
    private JTextField txtRoomId;
    private JTextField txtCheckInDate;
    private JTextField txtCheckOutDate;
    private JTextField txtTotalPrice;
    private JButton btnConfirm;
    private BookingController controller;

    public BookingFormFrame(int roomId) {
        controller = new BookingController();
        setTitle("Đặt phòng - Room ID: " + roomId);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Layout cho giao diện
        setLayout(new GridLayout(6, 2, 10, 10));

        // Labels và các trường nhập liệu
        add(new JLabel("User ID:"));
        txtUserId = new JTextField();
        add(txtUserId);

        add(new JLabel("Room ID:"));
        txtRoomId = new JTextField(String.valueOf(roomId));
        txtRoomId.setEditable(false);
        add(txtRoomId);

        add(new JLabel("Check-In Date (yyyy-MM-dd):"));
        txtCheckInDate = new JTextField();
        add(txtCheckInDate);

        add(new JLabel("Check-Out Date (yyyy-MM-dd):"));
        txtCheckOutDate = new JTextField();
        add(txtCheckOutDate);

        add(new JLabel("Total Price:"));
        txtTotalPrice = new JTextField();
        add(txtTotalPrice);

        // Nút xác nhận
        btnConfirm = new JButton("Xác nhận đặt phòng");
        add(new JLabel());  // Empty label to fill the grid
        add(btnConfirm);

        // Sự kiện cho nút xác nhận
        btnConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmBooking(roomId);
            }
        });

        setVisible(true);
    }

    // Phương thức xử lý xác nhận đặt phòng
    private void confirmBooking(int roomId) {
        try {
            // Lấy thông tin từ các trường nhập liệu
            int userId = Integer.parseInt(txtUserId.getText());
            String checkInDate = txtCheckInDate.getText();
            String checkOutDate = txtCheckOutDate.getText();
            double totalPrice = Double.parseDouble(txtTotalPrice.getText());

            // Kiểm tra định dạng ngày tháng
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date checkIn = sdf.parse(checkInDate);
            Date checkOut = sdf.parse(checkOutDate);
            if (checkIn.after(checkOut)) {
                JOptionPane.showMessageDialog(this, "Ngày check-out phải sau ngày check-in!");
                return;
            }

            // Tạo đối tượng Booking và gọi phương thức đặt phòng
            String status = "Pending"; // Trạng thái mặc định khi đặt phòng là "Pending"
            Date createdAt = new Date(); // Ngày tạo đặt phòng là ngày hiện tại
            // Tạo đối tượng Booking và gọi phương thức đặt phòng
            int bookingId = 0; // Hoặc bạn có thể để giá trị mặc định như 0 nếu bookingId là tự động tăng
            Booking booking = new Booking(bookingId, userId, roomId, checkIn, checkOut, totalPrice, status, createdAt);

            boolean success = controller.createBooking(booking); // Chuyển thành phương thức gọi trong controller

            if (success) {
                JOptionPane.showMessageDialog(this, "Đặt phòng thành công!");
                dispose();  // Đóng cửa sổ sau khi đặt phòng
            } else {
                JOptionPane.showMessageDialog(this, "Đặt phòng thất bại! Vui lòng thử lại.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + ex.getMessage());
        }
    }
}
