package model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UserData {
  protected String nama, username, email, password, role, usia, tanggallahir;
  private ArrayList<UserData> family;

  public UserData(String nama, String username, String email, String password, String role, String usia,
      String tanggallahir) {
    this.nama = nama;
    this.username = username;
    this.email = email;
    this.password = password;
    this.role = role;
    this.usia = usia;
    this.tanggallahir = tanggallahir;

  }

  public String gettanggallahir() {
    return tanggallahir;
  }

  public void settanggallahir(String tanggallahir) {
    this.tanggallahir = tanggallahir;
  }

  public String getNama() {
    return nama;
  }

  public void setNama(String nama) {
    this.nama = nama;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsia() {
    return usia;
  }

  public void setUsia(String usia) {
    this.usia = usia;
  }

  public ArrayList<UserData> getFamily() {
    if (family == null)
      family = new ArrayList<>();
    return family;
  }

  public void setFamily(ArrayList<UserData> family) {
    this.family = family;
  }

}
