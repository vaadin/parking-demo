package com.vaadin.demo.parking.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.vaadin.demo.parking.model.ClassificationGroup;
import com.vaadin.demo.parking.model.Species;

public class ClassificatiodDataReader {

    private static HashMap<String, Species> idToSpecies = new HashMap<String, Species>();

    private static ClassificationGroup currentLahko;
    private static ClassificationGroup currentHeimo;
    private static ClassificationGroup birds;
    private static Writer writer;
    private static Writer writerFi;
    private static Writer writerSe;
    private static boolean writeTranslations;

    public static void main(String[] args) throws ParserConfigurationException,
            SAXException, IOException {

        writeTranslations = true;
        readSpecies();

    }

    /**
     * Reads lintulajit.xml and creates NamesBundle resource files.
     * 
     * @param args
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static ClassificationGroup readSpecies()
            throws ParserConfigurationException, SAXException, IOException,
            UnsupportedEncodingException, FileNotFoundException {
        if (ClassificationGroup.AVES != null) {
            return ClassificationGroup.AVES;
        }
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = f.newDocumentBuilder();

        Document doc = builder.parse(ClassificatiodDataReader.class
                .getResourceAsStream("/lintulajit.xml"), "SF");

        NodeList childNodes = doc.getDocumentElement().getChildNodes();

        openTranslationFiles();

        birds = new ClassificationGroup("Aves");
        int length = childNodes.getLength();
        for (int i = 0; i < length; i++) {
            Node el = childNodes.item(i);
            String nodeName = el.getNodeName();
            if ("div".equals(nodeName)) {
                parseLahko(el);
            } else if ("b".equals(nodeName)) {
                parseHeimo(el);
            } else if ("pre".endsWith(nodeName)) {
                parseLajit(el);
            }
        }

        closeTranslationFiles();
        ClassificationGroup.AVES = birds;
        return birds;
    }

    private static void closeTranslationFiles() throws IOException {
        if (writeTranslations) {

            writer.close();
            writerFi.close();
            writerSe.close();
        }
    }

    private static void openTranslationFiles() throws IOException {
        if (writeTranslations) {

            writer = new OutputStreamWriter(new FileOutputStream(
                    "NamesBundle.properties"), "8859_1");
            writerFi = new OutputStreamWriter(new FileOutputStream(
                    "NamesBundle_fi.properties"), "8859_1");
            writerSe = new OutputStreamWriter(new FileOutputStream(
                    "NamesBundle_sv.properties"), "8859_1");

            addApplicationTranslations();

        }
    }

    private static void addApplicationTranslations() throws IOException {
        addTranslation("Aves", "Birds", "Linnut", "Fåglar");
        addTranslation("Observations", "Observations", "Havainnot",
                "Observations");
        addTranslation("Map", "Map", "Kartta", "Karta");
        addTranslation("Settings", "Settings", "Asetukset", "Settings");
        addTranslation("New Observation", "New Observation", "Uusi havainto",
                "Ny observation");
        addTranslation("Cancel", "Cancel", "Kumoa", "Avbryt");
        addTranslation("Save", "Save", "Tallenna", "Save");
        addTranslation("observationtime", "Time", "Aika", "Tid");
        addTranslation("species", "species", "laji", "species");
        addTranslation("location", "location", "paikka", "location");
        addTranslation("count", "count", "määrä", "count");
        addTranslation("observer", "observer", "havaitsija", "observer");
        addTranslation("time", "time", "aika", "time");
        addTranslation("name", "name", "nimi", "nam");
        addTranslation("Choose from map", "From map...", "Kartalta...",
                "Från karta...");
        addTranslation("Observation location", "Observation location",
                "Havaintopaikka", "Observatin plats");
        addTranslation("Observation details", "Observation details",
                "Havaintotiedot", "Observation detaljer");

    }

    private static void addTranslation(String latinName, String en, String fi,
            String sv) throws IOException {
        addTr(writer, latinName, en);
        addTr(writerFi, latinName, fi);
        addTr(writerSe, latinName, sv);
    }

    private static void parseLajit(Node el) throws IOException {
        String textContent = el.getTextContent();
        String[] rows = textContent.split("\n");
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i];
            String code = row.substring(0, 9).trim();
            String latinName = row.substring(12, 43).replaceAll(" [A-Z] ", "")
                    .trim();
            String fi = row.substring(43, 66).trim();
            String se = row.substring(66, 90).trim();
            String en = row.substring(90).trim();
            addTr(writer, latinName, en);
            addTr(writerFi, latinName, fi);
            addTr(writerSe, latinName, se);
            Species species = new Species();
            species.setName(latinName);
            species.setParent(currentHeimo);
            currentHeimo.getChildren().add(species);
            idToSpecies.put(code, species);
        }

    }

    private static void addTr(Writer writerFi2, String latinName, String fi)
            throws IOException {
        if (writeTranslations) {
            writerFi2.write(latinName.replaceAll(" ", "\\\\ "));
            writerFi2.write(" = ");
            writerFi2.write(fi);
            writerFi2.write("\n");
        }
    }

    private static void parseHeimo(Node el) throws IOException {
        String textContent = el.getTextContent();

        String[] split = textContent.substring(6).split("-");
        String latinName = split[0].trim();
        String finnishName = split[1].trim();
        String englishName = split[3].trim();
        String svenska = split[2].trim();
        addTr(writer, latinName, englishName);
        addTr(writerFi, latinName, finnishName);
        addTr(writerSe, latinName, svenska);
        ClassificationGroup heimo = new ClassificationGroup(latinName);
        currentLahko.getChildren().add(heimo);
        heimo.setParent(currentLahko);
        currentHeimo = heimo;
    }

    private static void parseLahko(Node el) throws IOException {
        String textContent = el.getTextContent();
        String latinName = textContent.substring(6, textContent.indexOf("-"))
                .trim();
        String[] split = textContent.split("-");
        String finnishName = split[1].trim();
        String englishName = split[3].trim();
        String svenska = split[2].trim();
        addTr(writer, latinName, englishName);
        addTr(writerFi, latinName, finnishName);
        addTr(writerSe, latinName, svenska);
        currentLahko = new ClassificationGroup(latinName);
        birds.getChildren().add(currentLahko);
        currentLahko.setParent(birds);
    }

    public static Species getSpeciesById(String id) {
        return idToSpecies.get(id);
    }

}
