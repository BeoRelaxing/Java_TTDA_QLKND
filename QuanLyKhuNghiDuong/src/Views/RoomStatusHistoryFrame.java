/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Views;
import Controllers.RoomController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
/**
 *
 * @author Beo
 */
public class RoomStatusHistoryFrame extends JFrame{
     private JTable table;
    private DefaultTableModel tableModel;

    public RoomStatusHistoryFrame(int roomId) {
        setTitle("Lịch sử trạng thái phòng");
        setSize(500, 300);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"Trạng thái", "Ngày thay đổi"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadHistory(roomId);

        setVisible(true);
    }

    private void loadHistory(int roomId) {
        RoomController controller = new RoomController();
        List<String[]> history = controller.getRoomStatusHistory(roomId);

        tableModel.setRowCount(0);
        for (String[] row : history) {
            tableModel.addRow(row);
        }
    }
}
