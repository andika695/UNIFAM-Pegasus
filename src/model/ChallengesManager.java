package model;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Challenges;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ChallengesManager {
    private static final String FILE_NAME = "challenges.xml";
    private static ChallengesManager instance;
    private ObservableList<Challenges> challengeList = FXCollections.observableArrayList();

    private ChallengesManager() {
        loadFromXML();
    }

    public static ChallengesManager getInstance() {
        if (instance == null) instance = new ChallengesManager();
        return instance;
    }

    public ObservableList<Challenges> getChallengeList() {
        return challengeList;
    }

    public void loadFromXML() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        XStream xstream = new XStream(new DomDriver());
        xstream.allowTypes(new Class[]{Challenges.class});
        xstream.alias("challenge", Challenges.class);

        try {
            ArrayList<Challenges> loaded = (ArrayList<Challenges>) xstream.fromXML(file);
            challengeList.setAll(loaded);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveToXML() {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("challenge", Challenges.class);
        String xml = xstream.toXML(new ArrayList<>(challengeList));

        try (FileOutputStream fos = new FileOutputStream(FILE_NAME)) {
            fos.write(xml.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
