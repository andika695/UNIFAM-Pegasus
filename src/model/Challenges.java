package model;

public class Challenges {
    private String date;
    private String challenge;
    private String pesan;
    private String nama;
    private String status;

    public Challenges() {}

    public Challenges(String date,String nama,String challenge, String pesan,String status){
        this.nama = nama;
        this.challenge = challenge;
        this.pesan = pesan;
        this.date = date;
        this.status = status;
    }
    
    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }


    public String getName(){
        return nama;
    }
    
    public void setName(String nama){
        this.nama = nama;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}