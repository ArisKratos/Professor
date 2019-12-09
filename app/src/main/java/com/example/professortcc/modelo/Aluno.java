package com.example.professortcc.modelo;

public class Aluno {

    private String id;
    private String token;
    private  boolean logado;

    public Aluno() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public boolean isLogado() {
        return logado;
    }

    public void setLogado(boolean logado) {
        this.logado = logado;
    }

    @Override
    public String toString() {
        return "Aluno{" +
                "id='" + id + '\'' +
                ", token='" + token + '\'' +
                ", logado=" + logado +
                '}';
    }
}
