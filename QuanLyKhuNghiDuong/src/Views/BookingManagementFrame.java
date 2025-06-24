/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Views;
import Controllers.BookingController;
import Models.Booking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
/**
 *
 * @author Beo
 */
public class BookingManagementFrame extends JFrame{
    private JTable table;
    private DefaultTableModel tableModel;
    private BookingController controller;

    public BookingManagementFrame() {
        controller = new BookingController();

        setTitle("Quản lý Đặt Phòng");
        setSize(800, 400);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"Booking ID", "User ID", "Room ID", "Check-In Date", "Check-Out Date", "Status", "Total Price", "Created At"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnConfirm = new JButton("Xác nhận");
        JButton btnCancel = new JButton("Hủy đặt phòng");
        btnPanel.add(btnConfirm);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        loadBookings();

        // Sự kiện
        btnConfirm.addActionListener(e -> confirmBooking());
        btnCancel.addActionListener(e -> cancelBooking());

        setVisible(true);
    }

    // Load danh sách đặt phòng
    private void loadBookings() {
        List<Booking> bookings = controller.getAllBookings();
        tableModel.setRowCount(0);
        for (Booking b : bookings) {
            tableModel.addRow(new Object[]{
                    b.getBookingId(), b.getUserId(), b.getRoomId(),
                    b.getCheckInDate(), b.getCheckOutDate(), b.getStatus(),
                    b.getTotalPrice(), b.getCreatedAt()
            });
        }
    }

    // Xác nhận đặt phòng
    private void confirmBooking() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Chọn đặt phòng cần xác nhận");
        return;
    }

    int bookingId = (int) tableModel.getValueAt(selectedRow, 0);

    if (controller.isBookingTimeConflict(bookingId)) {
        JOptionPane.showMessageDialog(this, "Không thể xác nhận: Phòng đã có người đặt trong khoảng thời gian này.");
        return;
    }

    if (controller.confirmBooking(bookingId)) {
        loadBookings();
        JOptionPane.showMessageDialog(this, "Xác nhận đặt phòng thành công!");
    } else {
        JOptionPane.showMessageDialog(this, "Xác nhận đặt phòng thất bại!");
    }
}


    // Hủy đặt phòng
    private void cancelBooking() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Chọn đặt phòng cần hủy");
            return;
        }
        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        if (controller.cancelBooking(bookingId)) {
            loadBookings();
            JOptionPane.showMessageDialog(this, "Hủy đặt phòng thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Hủy đặt phòng thất bại!");
        }
    }
}