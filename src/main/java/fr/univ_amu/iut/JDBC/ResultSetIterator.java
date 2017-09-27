package fr.univ_amu.iut.JDBC;

import fr.univ_amu.iut.JDBC.RowMappers.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class ResultSetIterator<T> implements Iterator<T> {

    private ResultSet rs;
    private PreparedStatement ps;
    private Connection connection;
    private RowMapper<T> rowMapper;
    private String sql;

    public ResultSetIterator(Connection connection, String sql, RowMapper<T> rowMapper) {
        assert connection != null;
        assert sql != null;
        this.connection = connection;
        this.sql = sql;
        this.rowMapper = rowMapper;
    }

    public void init() {
        try {
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

        } catch (SQLException e) {
            close();
            throw new DataAccessException(e);
        }
    }

    @Override
    public boolean hasNext() {
        if (ps == null) {
            init();
        }
        try {
            boolean hasMore = rs.next();
            if (!hasMore) {
                close();
            }
            return hasMore;
        } catch (SQLException e) {
            close();
            throw new DataAccessException(e);
        }

    }

    private void close() {
        try {
            rs.close();
            try {
                ps.close();
            } catch (SQLException e) {
                //nothing we can do here
            }
        } catch (SQLException e) {
            //nothing we can do here
        }
    }

    @Override
    public T next() {
        try {
            return rowMapper.mapRow(rs, 1);
        } catch (SQLException e) {
            close();
            throw new DataAccessException(e);
        }
    }
}