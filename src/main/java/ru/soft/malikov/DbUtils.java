package ru.soft.malikov;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для работы с БД
 * @author Alexandr Malikov
 */
public class DbUtils implements AutoCloseable {

    private static final String SQL_SELECT_COUNT = "SELECT COUNT(*) FROM TEST;";
    private static final String SQL_SELECT_ALL = "SELECT * FROM TEST;";
    private static final String SQL_DELETE_ALL = "DELETE FROM TEST;";
    private static final String SQL_INSERT_FIELD = "INSERT TEST (FIELD) VALUES (?);";
    private static DbUtils instance;
    private String JDBCDriver;
    private String JDBCDbURL;
    private String JDBCUser;
    private String JDBCPass;
    private int n;

    private Connection connection = null;
    private List<Integer> fields = new ArrayList<>();


    private DbUtils(String JDBCDriver, String JDBCDbURL, String JDBCUser, String JDBCPass, int n) {
        setJDBCDriver(JDBCDriver);
        setJDBCDbURL(JDBCDbURL);
        setJDBCUser(JDBCUser);
        setJDBCPass(JDBCPass);
        setN(n);
        try {
            Class.forName(JDBCDriver);
            connection = DriverManager.getConnection(JDBCDbURL, JDBCUser, JDBCPass);
            connection.setAutoCommit(false);
            Statement stmt = connection.createStatement();
            PreparedStatement pstmt = connection.prepareStatement(SQL_INSERT_FIELD);
            ResultSet rs = stmt.executeQuery(SQL_SELECT_COUNT);
            connection.commit();
            int rowsCount = 0;
            while (rs.next()) rowsCount = rs.getInt("COUNT(*)");
            rs.close();
            if (rowsCount > 0) {
                stmt.executeUpdate(SQL_DELETE_ALL);
                connection.commit();
            }
            for (int i = 1; i <= n; i++) {
                pstmt.setInt(1, i);
                pstmt.executeUpdate();
                pstmt.clearParameters();
            }
            connection.commit();
            ResultSet allValuesResult = stmt.executeQuery(SQL_SELECT_ALL);
            connection.commit();
            while (allValuesResult.next()) {
                fields.add(allValuesResult.getInt("field"));
            }
            allValuesResult.close();
            stmt.close();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param JDBCDriver Класс драйвера JDBC
     * @param JDBCDbURL  URL БД
     * @param JDBCUser   Пользователь
     * @param JDBCPass   Пароль
     * @param n          Кол-во записей, которые необходимо создать
     */
    public static DbUtils getInstance(String JDBCDriver, String JDBCDbURL, String JDBCUser, String JDBCPass, int n) {
        if (instance == null) {
            instance = new DbUtils(JDBCDriver, JDBCDbURL, JDBCUser, JDBCPass, n);
        }
        return instance;
    }

    @Override
    public void close() throws Exception {
        if (this.connection != null) {
            this.connection.close();
        }
    }

    /**
     * @return Имя JDBC драйвера
     */
    public String getJDBCDriver() {
        return JDBCDriver;
    }

    /**
     * @param JDBCDriver Имя JDBC драйвера
     */
    public void setJDBCDriver(String JDBCDriver) {
        this.JDBCDriver = JDBCDriver;
    }

    /**
     * @return URL для подключения к базе
     */
    public String getJDBCDbURL() {
        return JDBCDbURL;
    }

    /**
     * @param JDBCDbURL URL для подключения к базе
     */
    public void setJDBCDbURL(String JDBCDbURL) {
        this.JDBCDbURL = JDBCDbURL;
    }

    /**
     * @return Имя пользователя БД
     */
    public String getJDBCUser() {
        return JDBCUser;
    }

    /**
     * @param JDBCUser Имя пользователя БД
     */
    public void setJDBCUser(String JDBCUser) {
        this.JDBCUser = JDBCUser;
    }

    /**
     * @return Пароль для подключения к БД
     */
    public String getJDBCPass() {
        return JDBCPass;
    }

    /**
     * @param JDBCPass Пароль для подключения к БД
     */
    public void setJDBCPass(String JDBCPass) {
        this.JDBCPass = JDBCPass;
    }

    /**
     * @return Кол-во созданных строк
     */
    public int getN() {
        return n;
    }

    /**
     * @param n Кол-во строк для создания в таблице "test" БД
     */
    public void setN(int n) {
        this.n = n;
    }

    /**
     * @return Все записи в таблице "test" по полю "field"
     */
    public List<Integer> getFields() {
        return fields;
    }

    /**
     * @param fields Все записи в таблице "test" по полю "field"
     */
    public void setFields(List<Integer> fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbUtils)) return false;

        DbUtils dbUtils = (DbUtils) o;

        if (getN() != dbUtils.getN()) return false;
        if (getJDBCDriver() != null ? !getJDBCDriver().equals(dbUtils.getJDBCDriver()) : dbUtils.getJDBCDriver() != null)
            return false;
        if (getJDBCDbURL() != null ? !getJDBCDbURL().equals(dbUtils.getJDBCDbURL()) : dbUtils.getJDBCDbURL() != null)
            return false;
        if (getJDBCUser() != null ? !getJDBCUser().equals(dbUtils.getJDBCUser()) : dbUtils.getJDBCUser() != null)
            return false;
        if (getJDBCPass() != null ? !getJDBCPass().equals(dbUtils.getJDBCPass()) : dbUtils.getJDBCPass() != null)
            return false;
        if (connection != null ? !connection.equals(dbUtils.connection) : dbUtils.connection != null) return false;
        return getFields() != null ? getFields().equals(dbUtils.getFields()) : dbUtils.getFields() == null;
    }

    @Override
    public int hashCode() {
        int result = getJDBCDriver() != null ? getJDBCDriver().hashCode() : 0;
        result = 31 * result + (getJDBCDbURL() != null ? getJDBCDbURL().hashCode() : 0);
        result = 31 * result + (getJDBCUser() != null ? getJDBCUser().hashCode() : 0);
        result = 31 * result + (getJDBCPass() != null ? getJDBCPass().hashCode() : 0);
        result = 31 * result + getN();
        result = 31 * result + (connection != null ? connection.hashCode() : 0);
        result = 31 * result + (getFields() != null ? getFields().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DbUtils{" +
                "JDBCDriver='" + JDBCDriver + '\'' +
                ", JDBCDbURL='" + JDBCDbURL + '\'' +
                ", JDBCUser='" + JDBCUser + '\'' +
                ", JDBCPass='" + JDBCPass + '\'' +
                ", n=" + n +
                ", connection=" + connection +
                ", fields=" + fields +
                '}';
    }
}
