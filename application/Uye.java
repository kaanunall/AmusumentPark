package application;

public class Uye {
    private String tc;
    private String ad;
    private String soyad;
    private String email;
    private String telefon;

    public Uye(String tc, String ad, String soyad, String email, String telefon) {
        this.tc = tc;
        this.ad = ad;
        this.soyad = soyad;
        this.email = email;
        this.telefon = telefon;
    }

    public String getTc() { return tc; }
    public String getAd() { return ad; }
    public String getSoyad() { return soyad; }
    public String getEmail() { return email; }
    public String getTelefon() { return telefon; }
}