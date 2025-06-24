package TestDAO;

import DAO.ResortDAO;
import Models.Resort;
import org.junit.*;
import utils.DBConnection;

import java.sql.*;
import java.util.List;

import static org.junit.Assert.*;

public class ResortDAOTest {

    private static ResortDAO resortDAO;
    private static Connection conn;
    private static int insertedResortId;

    @BeforeClass
    public static void setup() throws SQLException {
        resortDAO = new ResortDAO();
        conn = DBConnection.getConnection();

        // Chèn resort test
        String sql = "INSERT INTO Resorts (name, location, type, description, price_range, amenities, created_at) "
                + "OUTPUT INSERTED.resort_id "
                + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "Test Resort");
            stmt.setString(2, "Test Location");
            stmt.setString(3, "Test Type");
            stmt.setString(4, "Test Description");
            stmt.setString(5, "$100-$200");
            stmt.setString(6, "Pool, Gym");

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                insertedResortId = rs.getInt(1);
            }
        }
    }

    @AfterClass
    public static void cleanup() throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Resorts WHERE resort_id = ?")) {
            stmt.setInt(1, insertedResortId);
            stmt.executeUpdate();
        }
    }

    @Test
    public void testGetAllResorts() {
        List<Resort> list = resortDAO.getAllResorts();
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    @Test
    public void testGetResortById() {
        Resort resort = resortDAO.getResortById(insertedResortId);
        assertNotNull(resort);
        assertNotNull(resort);
        assertTrue(resort.getName().equals("Test Resort") || resort.getName().equals("Updated Resort"));

    }

    @Test
    public void testSearchResorts() {
        List<Resort> results = resortDAO.searchResorts("Test");
        assertNotNull(results);
        assertTrue(results.stream().anyMatch(r -> r.getResortId() == insertedResortId));
    }

    @Test
    public void testUpdateResort() {
        Resort resort = resortDAO.getResortById(insertedResortId);
        resort.setName("Updated Resort");
        resortDAO.updateResort(resort);

        Resort updated = resortDAO.getResortById(insertedResortId);
        assertEquals("Updated Resort", updated.getName());
    }

    @Test
    public void testCountResorts() {
        int count = resortDAO.countResorts();
        assertTrue(count > 0);
    }

    @Test
    public void testSearchResortsWithoutIdAndCreatedAt() {
        List<Resort> list = resortDAO.searchResortsWithoutIdAndCreatedAt("Test");
        assertNotNull(list);
        assertTrue(list.stream().anyMatch(r -> "Updated Resort".equals(r.getName()) || "Test Resort".equals(r.getName())));
    }

    @Test
    public void testAddAndDeleteResort() {
        Resort newResort = new Resort();
        newResort.setName("Delete Resort");
        newResort.setLocation("Test City");
        newResort.setType("Luxury");
        newResort.setDescription("Should be deleted");
        newResort.setPriceRange("$300-$400");
        newResort.setAmenities("Spa, WiFi");

        resortDAO.addResort(newResort);

        // Xác nhận tồn tại resort mới
        List<Resort> found = resortDAO.searchResorts("Delete Resort");
        assertTrue(found.size() > 0);

        // Xóa resort mới
        int idToDelete = found.get(0).getResortId();
        resortDAO.deleteResort(idToDelete);

        // Kiểm tra resort đã bị xóa
        Resort deleted = resortDAO.getResortById(idToDelete);
        assertNull(deleted);
    }
}
