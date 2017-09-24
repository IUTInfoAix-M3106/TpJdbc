package fr.univ_amu.iut;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import static org.rnorth.visibleassertions.VisibleAssertions.assertEquals;

public class SimpleMySQLTest {

    private static final Logger logger = LoggerFactory.getLogger(SimpleMySQLTest.class);


    public static MySQLContainer mysql = (MySQLContainer) new MySQLContainer().withLogConsumer(new Slf4jLogConsumer(logger));
    static {
        mysql.start();
    }

    @Test
    public void testSimple() throws SQLException {
        try {
            ResultSet resultSet = performQuery(mysql, "SELECT 1");
            int resultSetInt = resultSet.getInt(1);

            assertEquals("A basic SELECT query succeeds", 1, resultSetInt);
        } finally {
            mysql.stop();
        }
    }

    protected ResultSet performQuery(MySQLContainer containerRule, String sql) throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(containerRule.getJdbcUrl());
        hikariConfig.setUsername(containerRule.getUsername());
        hikariConfig.setPassword(containerRule.getPassword());

        HikariDataSource ds = new HikariDataSource(hikariConfig);
        Statement statement = ds.getConnection().createStatement();
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();

        resultSet.next();
        return resultSet;
    }
}