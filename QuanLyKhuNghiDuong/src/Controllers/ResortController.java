package Controllers;

import DAO.ResortDAO;
import Models.Resort;

import java.util.List;

public class ResortController {
    private ResortDAO dao;

    public ResortController() {
        dao = new ResortDAO();
    }

    public List<Resort> getAllResorts() {
        return dao.getAllResorts();
    }

    public List<Resort> searchResorts(String keyword) {
        return dao.searchResorts(keyword);
    }

    public Resort getResortById(int id) {
        return dao.getResortById(id);
    }

    public void addResort(Resort resort) {
        dao.addResort(resort);
    }

    public void updateResort(Resort resort) {
        dao.updateResort(resort);
    }

    public void deleteResort(int id) {
        dao.deleteResort(id);
    }
    
    public int countResorts() {
    return dao.countResorts();
    }
    // custommer 
    // 1. search
    public List<Resort> searchResortsWithoutIdAndCreatedAt(String keyword) {
        return dao.searchResortsWithoutIdAndCreatedAt(keyword);
    }
    
}
