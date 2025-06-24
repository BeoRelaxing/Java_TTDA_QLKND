package TestDAO;

import DAO.NguoiDungDAO;
import Models.NguoiDung;

import org.junit.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import static org.junit.Assert.*;
import utils.DBConnection;


public class NguoiDungDAOTest {
    private static NguoiDungDAO dao;
    private String testEmail;
    private String testUsername;

    
    @BeforeClass
    public static void setUpClass() {
        dao = new NguoiDungDAO();
    }

  
     // Tạo email và tên người dùng test duy nhất theo timestamp để tránh trùng dữ liệu.
     
    @Before
    public void setUp() {
        long timestamp = System.currentTimeMillis();
        testEmail = "testuser_" + timestamp + "@example.com";
        testUsername = "testuser_" + timestamp;
    }

    
     // Xóa người dùng vừa được tạo theo email hoặc username để làm sạch dữ liệu sau test.
    @After
    public void tearDown() {
        deleteUserByEmailOrUsername(testEmail, testUsername);
    }

    
     //Xóa toàn bộ dữ liệu test còn sót lại theo mẫu email hoặc tên người dùng.
    @AfterClass
    public static void tearDownClass() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM Users WHERE email LIKE 'testuser_%@example.com' OR name LIKE 'testuser_%'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    
    //Kiểm thử đăng ký người dùng thành công với thông tin duy nhất.
     
    @Test
    public void testInsertUser_Success() {
        NguoiDung user = new NguoiDung(
            0,
            testUsername,
            testEmail,
            "hashedPassword",
            "0123456789",
            "customer",
            new Timestamp(System.currentTimeMillis())
        );

        boolean result = dao.insertUser(user);
        assertTrue("Insert user should succeed", result);
    }

    
    //Kiểm thử đăng ký với email bị trùng 
     
    @Test
    public void testInsertUser_DuplicateEmail() {
        // Tạo user đầu tiên
        NguoiDung user1 = new NguoiDung(
            0,
            testUsername,
            testEmail,
            "pass1",
            "0123456789",
            "customer",
            new Timestamp(System.currentTimeMillis())
        );
        dao.insertUser(user1);

        // Tạo user thứ hai với email trùng nhưng username khác
        NguoiDung user2 = new NguoiDung(
            0,
            testUsername + "_diff",
            testEmail,
            "pass2",
            "0987654321",
            "customer",
            new Timestamp(System.currentTimeMillis())
        );

        boolean result = dao.insertUser(user2);
        assertFalse("Insert should fail with duplicate email", result);
    }

   // Kiểm thử đăng ký với username bị trùng
    @Test
    public void testInsertUser_DuplicateUsername() {
        // Tạo user đầu tiên
        NguoiDung user1 = new NguoiDung(
            0,
            testUsername,
            testEmail,
            "pass1",
            "0123456789",
            "customer",
            new Timestamp(System.currentTimeMillis())
        );
        dao.insertUser(user1);

        // Tạo user thứ hai với username trùng nhưng email khác
        NguoiDung user2 = new NguoiDung(
            0,
            testUsername,
            testEmail + "_diff",
            "pass2",
            "0987654321",
            "customer",
            new Timestamp(System.currentTimeMillis())
        );

        boolean result = dao.insertUser(user2);
        assertFalse("Insert should fail with duplicate username", result);
    }

    
    //Kiểm thử đăng nhập thành công với thông tin đúng.
    @Test
    public void testGetUserByLogin_Success() {
        // Tạo user
        NguoiDung user = new NguoiDung(
            0,
            testUsername,
            testEmail,
            "testpass",
            "0123456789",
            "customer",
            new Timestamp(System.currentTimeMillis())
        );
        dao.insertUser(user);

        // Đăng nhập với thông tin vừa tạo
        NguoiDung result = dao.getUserByLogin(testUsername, "testpass");
        assertNotNull("User should be found", result);
        assertEquals(testUsername, result.getUsername());
        assertEquals(testEmail, result.getEmail());
    }

    
    //Kiểm thử đăng nhập thất bại khi cung cấp sai username hoặc password.
     
    @Test
    public void testGetUserByLogin_Failure() {
        // Thử đăng nhập với thông tin không tồn tại
        NguoiDung result = dao.getUserByLogin("nonexistent", "wrongpass");
        assertNull("User should not be found with wrong credentials", result);
    }

    
    //Phương thức hỗ trợ xóa người dùng theo email hoặc tên người dùng. 
    private void deleteUserByEmailOrUsername(String email, String username) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM Users WHERE email = ? OR name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
