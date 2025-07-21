package model;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import model.ArrayList; 
import java.io.*;
import java.util.List;

public class RewardManager {
    private static final RewardManager instance = new RewardManager();
    
    private final ArrayList<Reward> rewardList;
    private String currentUserId;

    
    private RewardManager() {
        rewardList = new ArrayList<>();
    }

    public static RewardManager getInstance() {
        return instance;
    }

  
    public void setCurrentUser(String userId) {
        this.currentUserId = userId;
        rewardList.clear(); 
        loadFromXML();     
    }

    public ArrayList<Reward> getRewardList() {
        return rewardList;
    }

 
    private String getFileName() {
        if (currentUserId == null || currentUserId.trim().isEmpty()) {
            return "rewards_default.xml";
        }
        return "rewards_" + currentUserId + ".xml";
    }

    public void saveToXML() {
        try {
            XStream xstream = new XStream(new DomDriver());
            xstream.setMode(XStream.NO_REFERENCES);
            xstream.alias("reward", Reward.class);
            xstream.allowTypes(new Class[]{Reward.class, java.util.ArrayList.class});

            java.util.ArrayList<Reward> listToSave = new java.util.ArrayList<>();
            for(Reward reward : this.rewardList) {
                listToSave.add(reward);
            }

            String xml = xstream.toXML(listToSave);

            try (FileOutputStream out = new FileOutputStream(getFileName())) {
                out.write(xml.getBytes("UTF-8"));
            }
        } catch (IOException e) {
            System.err.println("Gagal menyimpan ke XML: " + e.getMessage());
            e.printStackTrace();
        }
    }

 
    @SuppressWarnings("unchecked")
    public void loadFromXML() {
        try {
            File file = new File(getFileName());
            if (!file.exists()) {
                rewardList.clear(); 
                return;
            }

            XStream xstream = new XStream(new DomDriver());
            xstream.setMode(XStream.NO_REFERENCES);
            xstream.alias("reward", Reward.class);
            xstream.allowTypes(new Class[]{Reward.class, java.util.ArrayList.class});

            List<Reward> loadedList = (List<Reward>) xstream.fromXML(file);
            
            rewardList.clear();
            if (loadedList != null) {
                for (Reward reward : loadedList) {
                    rewardList.add(reward);
                }
            }

        } catch (Exception e) {
            System.err.println("Gagal memuat dari XML: " + e.getMessage());
            e.printStackTrace();
            rewardList.clear(); 
        }
    }

    public void clearData() {
        rewardList.clear();
        currentUserId = null;
    }
}