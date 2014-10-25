package de.bigboot.qcthemer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
* Created by Marco Kirchner
*/
public class Clock {
    private String title = null;
    private String id = null;
    private String author = null;
    private String description = null;
    private List<String> files = new ArrayList<String>();
    private int activate = -1;

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getFiles() {
        return files;
    }

    public int getActivate() {
        return activate;
    }

    public static Clock fromXML (String xml) throws IOException {
        return fromXML(new StringReader(xml));
    }

    public static Clock fromXML (InputStream in) throws IOException {
        return fromXML(new InputStreamReader(in));
    }

    private static Clock fromXML (Reader reader) throws IOException {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(reader);
            int eventType = xpp.getEventType();
            Clock clock = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("clock"))
                        clock = new Clock();
                    else if(clock != null)
                        if(xpp.getName().equalsIgnoreCase("title"))
                            clock.title = xpp.nextText();
                        else if(xpp.getName().equalsIgnoreCase("id"))
                            clock.id = xpp.nextText();
                        else if(xpp.getName().equalsIgnoreCase("author"))
                            clock.author = xpp.nextText();
                        else if(xpp.getName().equalsIgnoreCase("description"))
                            clock.description = xpp.nextText();
                        else if(xpp.getName().equalsIgnoreCase("file"))
                            clock.files.add(xpp.nextText());
                        else if(xpp.getName().equalsIgnoreCase("activate"))
                            clock.activate = Integer.parseInt(xpp.nextText());
                }
                eventType = xpp.next();
            }
            return clock;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        throw new IOException("Invalid clock.xml");
    }

    public String toXML () {
        StringBuilder sb = new StringBuilder();
        sb.append(
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
        "<clock>\n" +
        "\t<title>" + title + "</title>\n" +
        "\t<id>" + id + "</id>\n" +
        "\t<author>" + author + "</author>\n" +
        "\t<description>" + description + "</description>\n" +
        "\t<activate>" + activate + "</activate>\n" +
        "\t<replaces>\n");
        for (String file : files)
            sb.append("\t\t<file>" + file + "</file>\n");
        sb.append(
        "\t</replaces>\n" +
        "</clock>");

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Clock clock = (Clock) o;

        if (!id.equals(clock.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
