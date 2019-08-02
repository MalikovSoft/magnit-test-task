package ru.soft.malikov;

import java.util.List;

/**
 *
 * Основной класс приложения (содержит main-метод)
 * @author Alexandr Malikov
 */
public class MagnitApp {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/magnit?serverTimezone=UTC";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = ",fhhfrelf";


    public static void main(String[] args) throws Exception {
        List<Integer> integerList;
        XMLUtils xmlUtils = new XMLUtils();
        try (DbUtils dbUtils = DbUtils.getInstance(JDBC_DRIVER, JDBC_URL, JDBC_USER, JDBC_PASSWORD, 100)) {
            integerList = dbUtils.getFields();
            xmlUtils.createXMLFromList(integerList);
            xmlUtils.convertXMLWithXSLT("1.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
