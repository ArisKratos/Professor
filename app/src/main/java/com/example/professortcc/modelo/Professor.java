package com.example.professortcc.modelo;

public class Professor {

    public  Professor(){

    }

    private String id;
    private String nomeProfessor;
    private  String emailProfessor;
    private String senhaProfessor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeProfessor() {
        return nomeProfessor;
    }

    public String getEmailProfessor() {
        return emailProfessor;
    }

    public void setEmailProfessor(String emailProfessor) {
        this.emailProfessor = emailProfessor;
    }

    public void setNomeProfessor(String nomeProfessor) {
        this.nomeProfessor = nomeProfessor;
    }

    public String getSenhaProfessor() {
        return senhaProfessor;
    }

    public void setSenhaProfessor(String senhaProfessor) {
        this.senhaProfessor = senhaProfessor;
    }

    @Override
    public String toString() {
        return nomeProfessor + '\n' + emailProfessor;
    }
}
