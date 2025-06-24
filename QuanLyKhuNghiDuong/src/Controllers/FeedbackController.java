package Controllers;

import DAO.FeedbackDAO;
import Models.Feedback;
import java.util.List;

public class FeedbackController {
    private FeedbackDAO feedbackDAO;

    public FeedbackController() {
        this.feedbackDAO = new FeedbackDAO();
    }

    public boolean addFeedback(Feedback feedback) {
        return feedbackDAO.insertFeedback(feedback);
    }
    
    public List<Feedback> getAllFeedbacks() {
        return feedbackDAO.getAllFeedbacks();
    }
    
     public boolean deleteFeedback(int userId, String content) {
        return feedbackDAO.deleteFeedback(userId, content);
    }
}
    