package Views;
import Models.ServiceBooking;
import Controllers.ServiceBookingController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 *
 * @author Beo
 */
public class ServiceBookingManagementFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private ServiceBookingController controller;

    public ServiceBookingManagementFrame() {
        controller = new ServiceBookingController();

        setTitle("Quản lý Đặt Dịch Vụ");
        setSize(800, 400);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{
                "ID", "Booking ID", "Service ID", "Số lượng", "Tổng tiền"
        }, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnUpdate = new JButton("Cập nhật số lượng / tình trạng");
        add(btnUpdate, BorderLayout.SOUTH);

        loadServiceBookings();

        btnUpdate.addActionListener(e -> updateServiceBooking());

        setVisible(true);
    }

    private void loadServiceBookings() {
        List<ServiceBooking> list = controller.getAllServiceBookings();
        tableModel.setRowCount(0);
        for (ServiceBooking sb : list) {
            tableModel.addRow(new Object[]{
                    sb.getServiceBookingId(),
                    sb.getBookingId(),
                    sb.getServiceId(),
                    sb.getQuantity(),
                    sb.getTotalPrice()
            });
        }
    }

    private void updateServiceBooking() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Chọn dịch vụ cần cập nhật!");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int bookingId = (int) tableModel.getValueAt(selectedRow, 1);
        int serviceId = (int) tableModel.getValueAt(selectedRow, 2);

        // Nhập số lượng mới
        String quantityInput = JOptionPane.showInputDialog(this, "Nhập số lượng mới:");
        if (quantityInput == null) { // Người dùng nhấn Cancel
            return;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityInput.trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Nhập tổng tiền mới
        String totalPriceInput = JOptionPane.showInputDialog(this, "Nhập tổng tiền mới:");
        if (totalPriceInput == null) { // Người dùng nhấn Cancel
            return;
        }
        double totalPrice;
        try {
            totalPrice = Double.parseDouble(totalPriceInput.trim());
            if (totalPrice < 0) {
                JOptionPane.showMessageDialog(this, "Tổng tiền không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Tổng tiền không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ServiceBooking sb = new ServiceBooking(id, bookingId, serviceId, quantity, totalPrice);
        if (controller.updateServiceBooking(sb)) {
            loadServiceBookings();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }
}