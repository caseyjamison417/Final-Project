package com.techelevator.dao;

import com.techelevator.exception.DaoException;
import com.techelevator.model.Attraction;
import com.techelevator.model.Badge;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.List;

public class JdbcBadgeDao implements BadgeDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcBadgeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Badge getBadgeById(int id){
        Badge badge = null;
        String sql = "SELECT id, name, description" +
                " FROM badge WHERE id = ?";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
               badge = mapRowToBadge(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return badge;
    }

    public List<Badge> getBadge() {
        List<Badge> badge = new ArrayList<>();
        String sql = "SELECT id, name, description" +
                " FROM badge ORDER BY name ASC";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                Badge badges = mapRowToBadge(results);
                badge.add(badges);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return badge;
    }

    public Badge getBadgeByName(String name) {
        if (name == null) throw new IllegalArgumentException("name cannot be null");
        Badge badge = null;
        String sql = "SELECT id, name, description" +
                " FROM badge WHERE name = ?";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, name);
            if (rowSet.next()) {
                badge = mapRowToBadge(rowSet);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return badge;
    }

    public Badge createBadge(Badge badge) {
        Badge newBadge = null;
        String insertBadgeSql = "INSERT INTO badge ( " +
                " name, description) " +
                " VALUES ( ?, ?);" +
                " RETURNING id";

        try {
            int newAttractionId = jdbcTemplate.queryForObject(insertBadgeSql, int.class, badge.getName(), badge.getDescription());
            newBadge = getBadgeById(newAttractionId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return newBadge;
    }

    public int deleteBadgeById(int id){
        int numberOfRows = 0;
        String attractionDeleteSql = "DELETE FROM attraction WHERE id = ?";
        try {
            numberOfRows = jdbcTemplate.update(attractionDeleteSql, id);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return numberOfRows;

    }

    public Badge updateBadge(Badge badge) {
        Badge updatedBadge = null;
        String sql = "UPDATE badge SET name=?, description=? WHERE id=?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, badge.getName(), badge.getDescription(), badge.getId());

            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            } else {
                updatedBadge = getBadgeById(badge.getId());
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        return updatedBadge;
    }

    private Badge mapRowToBadge(SqlRowSet rs) {
        Badge badge = new Badge();
        badge.setId(rs.getInt("id"));
        badge.setName(rs.getString("name"));
        badge.setDescription(rs.getString("description"));
        return badge;
    }




}
