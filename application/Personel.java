package application;

public class Personel {
    private String email;
    private String sifre;
    private String ad;
    private String soyad;
    private String tc;
    private String telefon;
    private String resimYolu;

    public Personel(String email, String sifre, String ad, String soyad, String tc, String telefon, String resimYolu) {
        this.email = email;
        this.sifre = sifre;
        this.ad = ad;
        this.soyad = soyad;
        this.tc = tc;
        this.telefon = telefon;
        this.resimYolu = resimYolu;
    }

    // Getters
    public String getEmail() { return email; }
    public String getSifre() { return sifre; }
    public String getAd() { return ad; }
    public String getSoyad() { return soyad; }
    public String getTc() { return tc; }
    public String getTelefon() { return telefon; }
    public String getResimYolu() { return resimYolu; }

    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setSifre(String sifre) { this.sifre = sifre; }
    public void setAd(String ad) { this.ad = ad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }
    public void setTc(String tc) { this.tc = tc; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public void setResimYolu(String resimYolu) { this.resimYolu = resimYolu; }
}