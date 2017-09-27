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

import static org.assertj.core.api.Assertions.assertThat;

public class MySQLTest {

    private static final Logger logger = LoggerFactory.getLogger(MySQLTest.class);


    private static final MySQLContainer mysql = (MySQLContainer) new MySQLContainer().withLogConsumer(new Slf4jLogConsumer(logger));

    static {
        mysql.start();
    }

    @Test
    public void testSimple() throws SQLException {
        try {
            ResultSet resultSet = performQuery(mysql, "SELECT 1");
            int resultSetInt = resultSet.getInt(1);
            assertThat(resultSetInt).isEqualTo(1).as("A basic SELECT query succeeds");
        } finally {
            mysql.stop();
        }
    }

    private ResultSet performQuery(MySQLContainer containerRule, String sql) throws SQLException {
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