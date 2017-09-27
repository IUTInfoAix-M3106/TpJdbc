package fr.univ_amu.iut.JDBC;

import java.sql.SQLException;

public class DataAccessException extends RuntimeException {

    public int errorCode;
    public String state;

    public DataAccessException() {
    }

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
        getCodeAndState(cause);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }

    public DataAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    private void getCodeAndState(Throwable cause) {
        if (cause instanceof SQLException) {
            errorCode = ((SQLException) cause).getErrorCode();
            state = ((SQLException) cause).getSQLState();
        } else {
            errorCode = -1;
            state = cause.getMessage();
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return getClass().getName() + '[' + errorCode + ',' + state + "]: " + getLocalizedMessage();
    }
}