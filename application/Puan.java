package application;

public class Puan {
    private int id;
    private String ad;
    private String soyad;
    private int puan;

    public Puan(int id, String ad, String soyad, int puan) {
        this.id = id;
        this.ad = ad;
        this.soyad = soyad;
        this.puan = puan;
    }

    // Getter ve Setter metodları
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public int getPuan() {
        return puan;
    }

    public void setPuan(int puan) {
        this.puan = puan;
    }
}
