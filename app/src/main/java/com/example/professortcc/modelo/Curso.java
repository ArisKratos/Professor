package com.example.professortcc.modelo;

public class Curso {
    public Curso() {

    }

    private String id;
    private String curso;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    @Override
    public String toString() {

        return curso;
    }
}