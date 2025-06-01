package application;
public class Kampanya {
    private int kampanyaId;
    private String aciklama;

    public Kampanya(int kampanyaId, String aciklama) {
        this.kampanyaId = kampanyaId;
        this.aciklama = aciklama;
    }

    public int getKampanyaId() {
        return kampanyaId;
    }

    public void setKampanyaId(int kampanyaId) {
        this.kampanyaId = kampanyaId;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }
}
