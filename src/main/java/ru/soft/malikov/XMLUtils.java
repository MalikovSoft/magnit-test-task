package ru.soft.malikov;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.List;
import java.util.Objects;

/**
 * Класс для работы с XML
 * @author Alexandr Malikov
 */
public class XMLUtils {

    private String outputPath = "." + File.separator;

    public XMLUtils() {
    }

    /**
     *
     * @param outputPath Путь для выгружаемых файлов
     */
    public XMLUtils(String outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * Создание XML из списка
     * @param integerList Список целочисленных значений
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    public void createXMLFromList(List<Integer> integerList) throws FileNotFoundException, XMLStreamException {
        XMLOutputFactory factory = XMLOutputFactory.newFactory();
        XMLStreamWriter writer = factory.createXMLStreamWriter(new FileOutputStream(outputPath + "1.xml"));
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeCharacters("\n\t");
        writer.writeStartElement("entries");
        writer.writeCharacters("\n");
        for (int i : integerList) {
            writer.writeCharacters("\t\t");
            writer.writeStartElement("entry");
            writer.writeCharacters("\n\t\t\t");
            writer.writeStartElement("field");
            writer.writeCharacters(Integer.toString(i));
            writer.writeEndElement();
            writer.writeCharacters("\n\t\t");
            writer.writeEndElement();
            writer.writeCharacters("\n");
        }
        writer.writeCharacters("\t");
        writer.writeEndElement();
        writer.writeCharacters("\n");
        writer.writeEndDocument();
        writer.close();
    }

    /**
     * Конвертация файла, используя правила по умолчанию
     * @param fileNameToConvert Имя файла для конвертации
     * @throws TransformerException
     * @throws IOException
     */
    public void convertXMLWithXSLT(String fileNameToConvert) throws TransformerException, IOException {
        convertXML(new File(outputPath + fileNameToConvert), getFileFromResources("transformation/rules.xsl"));
    }

    /**
     * Конвертация файла по указанным правилам
     * @param fileNameToConvert Имя файла для конвертации
     * @param rulesFilename Имя файла, который содержит правила в формате XSLT
     * @throws TransformerException
     */
    public void convertXMLWithXSLT(String fileNameToConvert, String rulesFilename) throws TransformerException {
        convertXML(new File(outputPath + fileNameToConvert), new File(outputPath + rulesFilename));
    }

    /**
     * Возвращает выходную директорию
     * @return Выходная директория
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * Устанавливает выходную директорию
     * @param outputPath Выходная директория
     */
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    private void convertXML(File fileToConvert, File rules) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xsltFile = new StreamSource(rules);
        Source inputXML = new StreamSource(fileToConvert);
        Transformer transformer = factory.newTransformer(xsltFile);
        transformer.transform(inputXML, new StreamResult(new File(outputPath + "2.xml")));
    }

    private File getFileFromResources(String fileName) throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resourceStream = classLoader.getResourceAsStream(fileName);

        //URL resource = classLoader.getResource(fileName);
        if (resourceStream == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            File result = File.createTempFile("stream", "tmp");
            result.deleteOnExit();
            FileOutputStream outputStream = new FileOutputStream(result);
            copyStream(resourceStream, outputStream);
            outputStream.close();
            return result;
        }

    }

    private void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XMLUtils)) return false;
        XMLUtils xmlUtils = (XMLUtils) o;
        return getOutputPath().equals(xmlUtils.getOutputPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOutputPath());
    }

    @Override
    public String toString() {
        return "XMLUtils{" +
                "outputPath='" + outputPath + '\'' +
                '}';
    }
}
