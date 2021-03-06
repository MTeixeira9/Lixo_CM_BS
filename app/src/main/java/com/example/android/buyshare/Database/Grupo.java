package com.example.android.buyshare.Database;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Grupo {

    private String nome;
    private List<String> membrosGrupo;
    private String admin;
    private String idG;

    public Grupo(){
    }

    public Grupo(String nome, String admin, String idG){
        this.nome = nome;
        this.membrosGrupo = new ArrayList<>();
        this.admin = admin;
        membrosGrupo.add(admin);
        this.idG = idG;
    }

    public String getNome() {
        return nome;
    }

    public List<String> getMembrosGrupo() {
        return membrosGrupo;
    }

    public String getAdmin() {
        return admin;
    }

    public String getIdG() {
        return idG;
    }
}
