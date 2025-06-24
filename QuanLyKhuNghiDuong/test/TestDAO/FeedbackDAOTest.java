package TestDAO;

import DAO.FeedbackDAO;
import Models.Feedback;
import org.junit.*;

import java.util.List;

import static org.junit.Assert.*;

public class FeedbackDAOTest {

    private static FeedbackDAO feedbackDAO;
    private static final int TEST_USER_ID = 14;
    private static final String TEST_CONTENT = "Đánh giá test";

    @BeforeClass
    public static void beforeAll() {
        feedbackDAO = new FeedbackDAO();
    }

    @Before
    public void setUp() {
        
        List<Feedback> feedbacks = feedbackDAO.getAllFeedbacks();
        boolean exists = feedbacks.stream()
                .anyMatch(fb -> fb.getUserId() == TEST_USER_ID && TEST_CONTENT.equals(fb.getContent()));
        if (!exists) {
            Feedback fb = new Feedback();
            fb.setUserId(TEST_USER_ID);
            fb.setContent(TEST_CONTENT);
            feedbackDAO.insertFeedback(fb);
        }
    }

    @After
    public void tearDown() {
        feedbackDAO.deleteFeedback(TEST_USER_ID, TEST_CONTENT);
    }

    @Test
    public void testInsertFeedback() {
        Feedback fb = new Feedback();
        fb.setUserId(TEST_USER_ID);
        fb.setContent("Insert test content");

        boolean inserted = feedbackDAO.insertFeedback(fb);
        assertTrue("Feedback should be inserted", inserted);

        // Xóa luôn sau test
        feedbackDAO.deleteFeedback(fb.getUserId(), fb.getContent());
    }

    @Test
    public void testGetAllFeedbacks() {
        List<Feedback> list = feedbackDAO.getAllFeedbacks();
        assertNotNull("List should not be null", list);

        boolean found = list.stream().anyMatch(fb ->
                fb.getUserId() == TEST_USER_ID && TEST_CONTENT.equals(fb.getContent()));
        assertTrue("Test feedback should be present in DB", found);
    }

    @Test
    public void testDeleteFeedback() {
        // Đảm bảo feedback test tồn tại trước khi xóa
        Feedback fb = new Feedback();
        fb.setUserId(TEST_USER_ID);
        fb.setContent("To be deleted");

        feedbackDAO.insertFeedback(fb);

        // Thực hiện xóa
        boolean deleted = feedbackDAO.deleteFeedback(fb.getUserId(), fb.getContent());
        assertTrue("Feedback should be deleted", deleted);

        // Kiểm tra không còn tồn tại
        List<Feedback> list = feedbackDAO.getAllFeedbacks();
        boolean found = list.stream().anyMatch(f ->
                f.getUserId() == fb.getUserId() && "To be deleted".equals(f.getContent()));
        assertFalse("Feedback should no longer be in DB", found);
    }
}
