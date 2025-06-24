/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;
import DAO.RoomDAO;
import Models.Room;
import java.util.List;
/**
 *
 * @author Beo
 */
public class RoomController {
     private RoomDAO dao = new RoomDAO();

    public List<Room> getAllRooms() {
        return dao.getAllRooms();
    }

    public void addRoom(Room room) {
        dao.addRoom(room);
    }

    public void updateRoom(Room room) {
        dao.updateRoom(room);
    }

    public List<String[]> getRoomStatusHistory(int roomId) {
        return dao.getRoomStatusHistory(roomId);
    }
    // đếm 
    public int getRoomCount() {
    return dao.getRoomCount();
    }
    // hien thi theo id khu nghi duong 
    public List<Room> getRoomsByResortName(String resortName) {
    RoomDAO roomDAO = new RoomDAO();
    return roomDAO.getRoomsByResortName(resortName);
}
    
    public boolean updateRoomStatus(int roomId, String status) {
        return dao.updateRoomStatus(roomId, status);
    }
      
    public Room getRoomById(int roomId) {
        return dao.getRoomById(roomId);
    }
}


