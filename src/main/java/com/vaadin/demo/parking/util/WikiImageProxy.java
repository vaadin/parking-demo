package com.vaadin.demo.parking.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.UI;

public class WikiImageProxy extends HttpServlet {

    private static WikiImageProxy instance = new WikiImageProxy();

    private final Map<String, File> imageCache = new HashMap<String, File>();

    private final Set<File> pendingFiles = Collections
            .synchronizedSet(new HashSet<File>());

    private File getFileForName(String latinName) {
        File file = imageCache.get(latinName);
        if (file != null) {
            return file;
        }
        return retrieveFromWikipedia(latinName);
    }

    private File retrieveFromWikipedia(final String latinName) {
        String tmp = latinName.toLowerCase().replace(" ", "_");
        final String articlename = tmp.substring(0, 1).toUpperCase()
                + tmp.substring(1);

        final File tempFile;
        try {
            tempFile = File.createTempFile("TEMP_IMAGE_" + articlename, ".jpg");
            tempFile.deleteOnExit();
            pendingFiles.add(tempFile);
            imageCache.put(latinName, tempFile);

            new Thread() {
                @Override
                public void run() {

                    Document doc = getDocument("http://en.wikipedia.org/wiki/"
                            + articlename);

                    try {
                        Elements elementsByTagName = doc.getElementsByTag("a");
                        int length = elementsByTagName.size();
                        for (int i = 0; i < length; i++) {
                            Element item = elementsByTagName.get(i);
                            String href = item.attr("href");
                            if (href.contains("File:") && href.contains(".jpg")) {
                                doc = getDocument("http://en.wikipedia.org"
                                        + href);
                                Elements elementsByTagName2 = doc
                                        .getElementsByTag("img");
                                for (int j = 0; j < elementsByTagName2.size(); j++) {
                                    Element img = elementsByTagName2.get(j);
                                    Element div = img.parent().parent();
                                    String imgsrc = img.attr("src");
                                    if (div.attr("id").equals("file")
                                            && imgsrc.endsWith(".jpg")) {
                                        if (!imgsrc.startsWith("http")) {
                                            imgsrc = "http:" + imgsrc;
                                        }
                                        URL imageUrl = new URL(imgsrc);

                                        InputStream openStream = imageUrl
                                                .openStream();
                                        FileOutputStream out = new FileOutputStream(
                                                tempFile);

                                        int b;
                                        while ((b = openStream.read()) != -1) {
                                            out.write(b);
                                        }
                                        out.close();
                                        openStream.close();
                                        pendingFiles.remove(tempFile);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    } catch (Exception e) {
                        Logger.getAnonymousLogger().log(Level.INFO,
                                "Error in image lookup", e);
                    }

                    Logger.getAnonymousLogger().info(
                            "No image found for " + latinName);
                }
            }.start();
            return tempFile;
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return null;
    }

    public static Document getDocument(String strurl) {
        try {
            URL url = new URL(strurl);
            return Jsoup.parse(url, 5000);
        } catch (Exception e) {
            return null;
        }

    }

    public static Resource getImage(UI app, String name) {
        try {
            if (instance.getFileForName(name) != null) {
                return new ExternalResource("IMG/" + name.replace(" ", "_")
                        + ".jpg");
            }
        } catch (Exception e) {
        }
        return new ThemeResource("nobirdimagefound.png");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String requestPathInfo = req.getPathInfo();
        String fileName = requestPathInfo.substring(requestPathInfo
                .lastIndexOf("/") + 1);
        String name = fileName.substring(0, fileName.indexOf(".")).replace("_",
                " ");

        File fileForName = getFileForName(name);
        InputStream stream = null;
        String mimetype = "image/png";
        if (pendingFiles.contains(fileForName)) {
            for (int i = 0; i < 10; i++) {
                if (!pendingFiles.contains(fileForName)) {
                    break;
                }
                if (i == 9) {
                    // Assume file was not found.
                    imageCache.remove(name);
                    stream = getClass().getResourceAsStream(
                            "nobirdimagefound.png");
                }
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        try {
            stream = new FileInputStream(fileForName);
            mimetype = "image/jpeg";
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        res.setContentType(mimetype);
        // TODO cache headers
        OutputStream output = res.getOutputStream();
        IOUtils.copy(stream, output);

    }

}
