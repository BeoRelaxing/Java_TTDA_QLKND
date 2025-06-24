package Views;

import Controllers.ResortController;
import Models.Resort;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ResortManagementFrame extends JFrame {
    private ResortController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    public ResortManagementFrame() {
        controller = new ResortController();
        setTitle("Quản lý Khu nghỉ dưỡng");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initTopPanel();
        initTable();
        initButtons();

        loadResorts();

        setVisible(true);
    }

    private void initTopPanel() {
        JPanel panel = new JPanel();

        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Tìm kiếm");

        btnSearch.addActionListener(e -> handleSearch());

        panel.add(new JLabel("Từ khóa:"));
        panel.add(txtSearch);
        panel.add(btnSearch);

        add(panel, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] columns = {"ID", "Tên", "Vị trí", "Loại hình", "Giá", "Tiện nghi", "Ngày tạo"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initButtons() {
        JPanel panel = new JPanel();

        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm mới");

        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnRefresh);

        add(panel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> handleAdd());
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());
        btnRefresh.addActionListener(e -> loadResorts());
    }

    private void loadResorts() {
        tableModel.setRowCount(0);
        List<Resort> resorts = controller.getAllResorts();

        for (Resort r : resorts) {
            tableModel.addRow(new Object[]{
                    r.getResortId(),
                    r.getName(),
                    r.getLocation(),
                    r.getType(),
                    r.getPriceRange(),
                    r.getAmenities(),
                    r.getCreatedAt()
            });
        }
    }

    private void handleAdd() {
        JTextField tfName = new JTextField();
        JTextField tfLocation = new JTextField();
        JTextField tfType = new JTextField();
        JTextField tfDescription = new JTextField();
        JTextField tfPrice = new JTextField();
        JTextField tfAmenities = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Tên:")); panel.add(tfName);
        panel.add(new JLabel("Vị trí:")); panel.add(tfLocation);
        panel.add(new JLabel("Loại hình:")); panel.add(tfType);
        panel.add(new JLabel("Mô tả:")); panel.add(tfDescription);
        panel.add(new JLabel("Khoảng giá:")); panel.add(tfPrice);
        panel.add(new JLabel("Tiện nghi:")); panel.add(tfAmenities);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm Khu nghỉ dưỡng", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Resort resort = new Resort();
            resort.setName(tfName.getText());
            resort.setLocation(tfLocation.getText());
            resort.setType(tfType.getText());
            resort.setDescription(tfDescription.getText());
            resort.setPriceRange(tfPrice.getText());
            resort.setAmenities(tfAmenities.getText());

            controller.addResort(resort);
            loadResorts();
        }
    }

    private void handleEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int resortId = (int) tableModel.getValueAt(selectedRow, 0);

            Resort existing = controller.getResortById(resortId);

            JTextField tfName = new JTextField(existing.getName());
            JTextField tfLocation = new JTextField(existing.getLocation());
            JTextField tfType = new JTextField(existing.getType());
            JTextField tfDescription = new JTextField(existing.getDescription());
            JTextField tfPrice = new JTextField(existing.getPriceRange());
            JTextField tfAmenities = new JTextField(existing.getAmenities());

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Tên:")); panel.add(tfName);
            panel.add(new JLabel("Vị trí:")); panel.add(tfLocation);
            panel.add(new JLabel("Loại hình:")); panel.add(tfType);
            panel.add(new JLabel("Mô tả:")); panel.add(tfDescription);
            panel.add(new JLabel("Khoảng giá:")); panel.add(tfPrice);
            panel.add(new JLabel("Tiện nghi:")); panel.add(tfAmenities);

            int result = JOptionPane.showConfirmDialog(this, panel, "Sửa Khu nghỉ dưỡng", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                existing.setName(tfName.getText());
                existing.setLocation(tfLocation.getText());
                existing.setType(tfType.getText());
                existing.setDescription(tfDescription.getText());
                existing.setPriceRange(tfPrice.getText());
                existing.setAmenities(tfAmenities.getText());

                controller.updateResort(existing);
                loadResorts();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng để sửa.");
        }
    }

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int resortId = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                controller.deleteResort(resortId);
                loadResorts();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng để xóa.");
        }
    }

    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadResorts();
            return;
        }

        tableModel.setRowCount(0);
        List<Resort> resorts = controller.searchResorts(keyword);

        for (Resort r : resorts) {
            tableModel.addRow(new Object[]{
                    r.getResortId(),
                    r.getName(),
                    r.getLocation(),
                    r.getType(),
                    r.getPriceRange(),
                    r.getAmenities(),
                    r.getCreatedAt()
            });
        }
    }
}
