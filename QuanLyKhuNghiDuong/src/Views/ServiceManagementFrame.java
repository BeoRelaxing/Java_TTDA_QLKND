package Views;

import Controllers.ServiceController;
import Models.Service;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ServiceManagementFrame extends JFrame {

   private JTable table;
    private DefaultTableModel tableModel;
    private ServiceController controller = new ServiceController();

    public ServiceManagementFrame() {
        setTitle("Quản lý Dịch Vụ");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(new String[]{"ID", "Resort ID", "Tên dịch vụ", "Mô tả", "Giá"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Thêm dịch vụ");
        JButton btnEdit = new JButton("Sửa dịch vụ");
        JButton btnDelete = new JButton("Xóa dịch vụ");
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.SOUTH);

        loadServices();

        // Sự kiện
        btnAdd.addActionListener(e -> addService());
        btnEdit.addActionListener(e -> editService());
        btnDelete.addActionListener(e -> deleteService());

        setVisible(true);
    }

    private void loadServices() {
        List<Service> services = controller.getAllServices();
        tableModel.setRowCount(0);
        for (Service s : services) {
            tableModel.addRow(new Object[]{
                    s.getServiceId(), s.getResortId(), s.getName(),
                    s.getDescription(), s.getPrice()
            });
        }
    }

    private void addService() {
        Service service = showServiceForm(null);
        if (service != null) {
            controller.addService(service);
            loadServices();
            JOptionPane.showMessageDialog(this, "Thêm dịch vụ thành công!");
        }
    }

    private void editService() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Chọn dịch vụ cần sửa");
            return;
        }
        int serviceId = (int) tableModel.getValueAt(selectedRow, 0);
        Service service = new Service(
                serviceId,
                (int) tableModel.getValueAt(selectedRow, 1),
                (String) tableModel.getValueAt(selectedRow, 2),
                (String) tableModel.getValueAt(selectedRow, 3),
                (double) tableModel.getValueAt(selectedRow, 4)
        );

        Service updatedService = showServiceForm(service);
        if (updatedService != null) {
            controller.updateService(updatedService);
            loadServices();
            JOptionPane.showMessageDialog(this, "Cập nhật dịch vụ thành công!");
        }
    }

    private void deleteService() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Chọn dịch vụ cần xóa");
            return;
        }
        int serviceId = (int) tableModel.getValueAt(selectedRow, 0);
        controller.deleteService(serviceId);
        loadServices();
        JOptionPane.showMessageDialog(this, "Xóa dịch vụ thành công!");
    }

    private Service showServiceForm(Service service) {
        JTextField tfResortId = new JTextField();
        JTextField tfName = new JTextField();
        JTextArea taDescription = new JTextArea(3, 20);
        JTextField tfPrice = new JTextField();

        if (service != null) {
            tfResortId.setText(String.valueOf(service.getResortId()));
            tfName.setText(service.getName());
            taDescription.setText(service.getDescription());
            tfPrice.setText(String.valueOf(service.getPrice()));
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Resort ID:"));
        panel.add(tfResortId);
        panel.add(new JLabel("Tên dịch vụ:"));
        panel.add(tfName);
        panel.add(new JLabel("Mô tả:"));
        panel.add(new JScrollPane(taDescription));
        panel.add(new JLabel("Giá:"));
        panel.add(tfPrice);

        int result = JOptionPane.showConfirmDialog(this, panel, service == null ? "Thêm dịch vụ" : "Sửa dịch vụ",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int resortId = Integer.parseInt(tfResortId.getText());
                String name = tfName.getText();
                String description = taDescription.getText();
                double price = Double.parseDouble(tfPrice.getText());

                if (service == null) {
                    return new Service(0, resortId, name, description, price);
                } else {
                    service.setResortId(resortId);
                    service.setName(name);
                    service.setDescription(description);
                    service.setPrice(price);
                    return service;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!");
            }
        }
        return null;
    }
}
