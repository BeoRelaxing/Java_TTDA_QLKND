package Views;

import Controllers.RoomController;
import Models.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class RoomManagementFrame extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private RoomController controller = new RoomController();

    public RoomManagementFrame() {
        setTitle("Quản lý Phòng");
        setSize(800, 400);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"ID", "Resort ID", "Số phòng", "Loại", "Giá", "Trạng thái", "Sức chứa"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Thêm phòng");
        JButton btnEdit = new JButton("Sửa phòng");
        JButton btnHistory = new JButton("Lịch sử trạng thái");
        JButton btnReload = new JButton("Load");
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnHistory);
        btnPanel.add(btnReload);
        add(btnPanel, BorderLayout.SOUTH);

        loadRooms();

        // Sự kiện
        btnAdd.addActionListener(e -> addRoom());
        btnEdit.addActionListener(e -> editRoom());
        btnHistory.addActionListener(e -> viewHistory());
        btnReload.addActionListener(e -> loadRooms());

        setVisible(true);
    }

    private void loadRooms() {
        List<Room> rooms = controller.getAllRooms();
        tableModel.setRowCount(0);
        for (Room r : rooms) {
            tableModel.addRow(new Object[]{
                r.getRoomId(), r.getResortId(), r.getRoomNumber(),
                r.getRoomType(), r.getPricePerNight(), r.getStatus(), r.getCapacity()
            });
        }
    }

    private void addRoom() {
        Room room = showRoomForm(null);
        if (room != null) {
            controller.addRoom(room);
            loadRooms();
            JOptionPane.showMessageDialog(this, "Thêm phòng thành công!");
        }
    }

    private void editRoom() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Chọn phòng cần sửa");
            return;
        }
        int roomId = (int) tableModel.getValueAt(selectedRow, 0);

        // Lấy giá trị Resort ID và kiểm tra kiểu dữ liệu
        Object resortIdObj = tableModel.getValueAt(selectedRow, 1);
        int resortId = resortIdObj instanceof String ? Integer.parseInt((String) resortIdObj) : (int) resortIdObj;

        Room room = new Room();
        room.setRoomId(roomId);
        room.setResortId(resortId);  // Sử dụng resortId đã được xử lý đúng kiểu
        room.setRoomNumber((String) tableModel.getValueAt(selectedRow, 2));
        room.setRoomType((String) tableModel.getValueAt(selectedRow, 3));
        room.setPricePerNight((double) tableModel.getValueAt(selectedRow, 4));
        room.setStatus((String) tableModel.getValueAt(selectedRow, 5));
        room.setCapacity((int) tableModel.getValueAt(selectedRow, 6));

        Room updatedRoom = showRoomForm(room);
        if (updatedRoom != null) {
            controller.updateRoom(updatedRoom);
            loadRooms();
            JOptionPane.showMessageDialog(this, "Cập nhật phòng thành công!");
        }
    }

    private void viewHistory() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Chọn phòng để xem lịch sử");
            return;
        }
        int roomId = (int) tableModel.getValueAt(selectedRow, 0);
        new RoomStatusHistoryFrame(roomId);
    }

    private Room showRoomForm(Room room) {
        JTextField tfResortId = new JTextField();
        JTextField tfRoomNumber = new JTextField();
        JTextField tfRoomType = new JTextField();
        JTextField tfPrice = new JTextField();
        String[] statuses = {"Available", "Booked", "Maintenance"};
        JComboBox<String> cbStatus = new JComboBox<>(statuses);
        JTextField tfCapacity = new JTextField();

        if (room != null) {
            tfResortId.setText(String.valueOf(room.getResortId()));
            tfRoomNumber.setText(room.getRoomNumber());
            tfRoomType.setText(room.getRoomType());
            tfPrice.setText(String.valueOf(room.getPricePerNight()));
            cbStatus.setSelectedItem(room.getStatus());  // Không cần kiểm tra room != null nữa
            tfCapacity.setText(String.valueOf(room.getCapacity()));
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Resort ID:"));
        panel.add(tfResortId);
        panel.add(new JLabel("Số phòng:"));
        panel.add(tfRoomNumber);
        panel.add(new JLabel("Loại phòng:"));
        panel.add(tfRoomType);
        panel.add(new JLabel("Giá mỗi đêm:"));
        panel.add(tfPrice);
        panel.add(new JLabel("Trạng thái:"));
        panel.add(cbStatus);
        panel.add(new JLabel("Sức chứa:"));
        panel.add(tfCapacity);

        int result = JOptionPane.showConfirmDialog(this, panel, room == null ? "Thêm phòng" : "Sửa phòng",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Room r = room == null ? new Room() : room;

                // Kiểm tra và chuyển Resort ID từ String sang Integer
                try {
                    r.setResortId(Integer.parseInt(tfResortId.getText()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Resort ID phải là số!");
                    return null;
                }

                r.setRoomNumber(tfRoomNumber.getText());
                r.setRoomType(tfRoomType.getText());

                // Kiểm tra và chuyển giá mỗi đêm từ String sang Double
                try {
                    r.setPricePerNight(Double.parseDouble(tfPrice.getText()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Giá mỗi đêm phải là số!");
                    return null;
                }

                r.setStatus((String) cbStatus.getSelectedItem());

                // Kiểm tra và chuyển Sức chứa từ String sang Integer
                try {
                    r.setCapacity(Integer.parseInt(tfCapacity.getText()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Sức chứa phải là số!");
                    return null;
                }

                return r;  // Trả về đối tượng Room nếu mọi dữ liệu hợp lệ
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ! Vui lòng kiểm tra lại.");
            }
        }
        return null;
    }

}
