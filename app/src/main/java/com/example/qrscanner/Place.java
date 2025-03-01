package com.example.qrscanner;

import java.util.List;

public class Place {
    private String id;
    private String nazwa;
    private String opis;
    private String zdjecie;
    private List<String> zdjecia_inne;
    private String kategoria;
    private String wspolrzedne;

    public Place() {}

    public Place(String id, String nazwa, String opis, String zdjecie, List<String> zdjecia_inne, String kategoria, String wspolrzedne) {
        this.id = id;
        this.nazwa = nazwa;
        this.opis = opis;
        this.zdjecie = zdjecie;
        this.zdjecia_inne = zdjecia_inne;
        this.kategoria = kategoria;
        this.wspolrzedne = wspolrzedne;
    }

    public String getId() { return id; }
    public String getNazwa() { return nazwa; }
    public String getOpis() { return opis; }
    public String getZdjecie() { return zdjecie; }
    public List<String> getZdjeciaInne() { return zdjecia_inne; }
    public String getKategoria() { return kategoria; }
    public String getWspolrzedne() { return wspolrzedne; }
}
