package TestDAO;

import DAO.RoomDAO;
import Models.Room;
import utils.DBConnection;

import org.junit.*;
import java.sql.*;
import java.util.*;

import static org.junit.Assert.*;

public class RoomDAOTest {

    private static RoomDAO roomDAO;
    private static int testRoomId;
    private static int testResortId = 1; // Ensure resort with ID 1 exists in DB

    @BeforeClass
    public static void setup() throws SQLException {
        roomDAO = new RoomDAO();
        insertTestRoom();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        deleteTestRoom();
    }

    private static void insertTestRoom() throws SQLException {
        String sql = "INSERT INTO Rooms (resort_id, room_number, room_type, price_per_night, status, capacity) "
                + "OUTPUT INSERTED.room_id VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, testResortId);
            ps.setString(2, "101A");
            ps.setString(3, "Deluxe");
            ps.setDouble(4, 150.0);
            ps.setString(5, "Available");
            ps.setInt(6, 2);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                testRoomId = rs.getInt(1);
            }
        }
    }

    private static void deleteTestRoom() throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM Rooms WHERE room_id = ?")) {
            ps.setInt(1, testRoomId);
            ps.executeUpdate();
        }
    }

    @Test
    public void testGetAllRooms() {
        List<Room> rooms = roomDAO.getAllRooms();
        assertNotNull(rooms);
        assertTrue(rooms.size() > 0);
    }

    @Test
    public void testGetRoomById() {
        Room room = roomDAO.getRoomById(testRoomId);
        assertNotNull(room);
        assertEquals("101A", room.getRoomNumber());
    }

    @Test
    public void testUpdateRoom() {
        Room room = roomDAO.getRoomById(testRoomId);
        assertNotNull(room);
        room.setRoomType("Suite");
        room.setCapacity(4);
        roomDAO.updateRoom(room);

        Room updated = roomDAO.getRoomById(testRoomId);
        assertEquals("Suite", updated.getRoomType());
        assertEquals(4, updated.getCapacity());
    }

    @Test
    public void testGetRoomCount() {
        int count = roomDAO.getRoomCount();
        assertTrue(count > 0);
    }

    @Test
    public void testUpdateRoomStatus() {
        boolean success = roomDAO.updateRoomStatus(testRoomId, "Booked");
        assertTrue(success);

        Room room = roomDAO.getRoomById(testRoomId);
        assertEquals("Booked", room.getStatus());

        // revert
        roomDAO.updateRoomStatus(testRoomId, "Available");
    }

    @Test
    public void testSearchRooms() {
        List<Room> rooms = roomDAO.searchRooms(testResortId, "Deluxe", 200.0, "Available");
        assertNotNull(rooms);
        assertTrue(rooms.size() >= 0);
    }

    @Test
    public void testGetRoomStatusHistory() {
        List<String[]> history = roomDAO.getRoomStatusHistory(testRoomId);
        assertNotNull(history);
    }

    @Test
    public void testIsRoomAvailable() {
        Calendar cal = Calendar.getInstance();
        java.util.Date today = new java.util.Date();
        cal.setTime(today);
        cal.add(Calendar.DATE, 1);
        java.util.Date tomorrow = cal.getTime();

        // Chuyển sang java.sql.Date trước khi truyền vào hàm
        java.sql.Date sqlToday = new java.sql.Date(today.getTime());
        java.sql.Date sqlTomorrow = new java.sql.Date(tomorrow.getTime());
        
        boolean available = roomDAO.isRoomAvailable(testRoomId, sqlToday, sqlTomorrow);
        assertTrue(available); // assuming test room is not booked
    }

    @Test
    public void testGetRoomsByResortName() {
        List<Room> rooms = roomDAO.getRoomsByResortName("Test Resort");
        assertNotNull(rooms);
    }

    @Test
    public void testAddRoomAndDelete() throws SQLException {
        Room newRoom = new Room();
        newRoom.setResortId(testResortId);
        newRoom.setRoomNumber("999X");
        newRoom.setRoomType("Test");
        newRoom.setPricePerNight(999);
        newRoom.setStatus("Available");
        newRoom.setCapacity(1);

        roomDAO.addRoom(newRoom);

        List<Room> all = roomDAO.getAllRooms();
        Room last = all.get(all.size() - 1);
        assertEquals("999X", last.getRoomNumber());

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM Rooms WHERE room_number = ?")) {
            ps.setString(1, "999X");
            ps.executeUpdate();
        }
    }
}
