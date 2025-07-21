package controller;

import java.io.FileInputStream;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

import model.UserData;

public class UserList {

  private ArrayList<UserData> userList = new ArrayList<>();

  private static UserData loginAccount;

  public UserList() {
    loadUser();
  }

  public ArrayList<UserData> getUserList() {
    return userList;
  }

  public ArrayList<UserData> loadUser() {
    XStream xStream = new XStream(new StaxDriver());
    xStream.addPermission(AnyTypePermission.ANY);
    xStream.alias("user", UserData.class);

    try (FileInputStream fileInput = new FileInputStream("users.xml")) {
        StringBuilder s = new StringBuilder();
        int isi;
        while ((isi = fileInput.read()) != -1) {
            s.append((char) isi);
        }

        this.userList = (ArrayList<UserData>) xStream.fromXML(s.toString());
    } catch (Exception e) {
        System.out.println("loadUser error: " + e.getMessage());
        this.userList = new ArrayList<>(); 
    }

    return this.userList;
}

  public static void setLoginAccount(UserData loginAccount) {
    UserList.loginAccount = loginAccount;
  }

  public static UserData getLoginAccount() {
    return loginAccount;
  }

}
