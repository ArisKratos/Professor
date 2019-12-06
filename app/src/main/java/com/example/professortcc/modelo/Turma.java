package com.example.professortcc.modelo;

public class Turma {


    private String id;
    private String ano;
    private String semestre;
    private String urlGrade;
    private String curso;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }
    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getUrlGrade() {
        return urlGrade;
    }

    public void setUrlGrade(String urlGrade) {
        this.urlGrade = urlGrade;
    }


    @Override
    public String toString() {
        return   ano + "/" + semestre;
    }
}

