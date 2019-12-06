package com.example.professortcc.controle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.professortcc.R;
import com.example.professortcc.modelo.Curso;
import com.example.professortcc.modelo.Mensagem;
import com.example.professortcc.modelo.Professor;
import com.example.professortcc.modelo.Turma;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ProfActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private EditText textMensagem;
    private List<Curso> cursos;
    private List<Turma> turmas;
    private Spinner spnCursos;
    private TextView aliaslinkBaixarGrade;
    private Spinner spnTurmas;
    private String nomeProf;
    private CheckBox checkMudancaDeHorario;
    private boolean mudancaHorario;
    private String txtTurmaAno, txtTurmaSemestre;
    private Button enviarMensagem;
    private String idMsg;
    private String nomeCurso;
    private final static String TAG  = "Firelog";


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore.getInstance().collection("professores")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals(FirebaseAuth.getInstance().getUid())) {


                                    String id = document.getString("id");
                                    String nome = document.getString("nomeProfessor");


                                    Professor u = new Professor();


                                    u.setId(id);
                                    u.setNomeProfessor(nome);

                                    nomeProf = nome;


                                }
                            }


                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prof_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Professor");


        spnCursos = findViewById(R.id.spinnerCurso);
        checkMudancaDeHorario = findViewById(R.id.editCheckAlertHorario);
        enviarMensagem = findViewById(R.id.buttonEnviarMensagem);
        textMensagem = findViewById(R.id.editMensagem);
        spnTurmas = findViewById(R.id.spinnerTurma);
        aliaslinkBaixarGrade = findViewById(R.id.linkBaixarGrade);


        spnCursos.setOnItemSelectedListener(this);


        cursos = new ArrayList<>();
        turmas = new ArrayList<>();


        carregarSpinnerCurso();
        enviarMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String uid = UUID.randomUUID().toString();

                idMsg = uid;

                sendMassage();

//                Curso curso = (Curso) spnCursos.getSelectedItem();
//                nomeCurso = curso.getCurso();

            }

        });

    aliaslinkBaixarGrade.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {




       }

    });

    }


    private  void carregarSpinnerTurma(){
        Curso curso = (Curso) spnCursos.getSelectedItem();

        FirebaseFirestore.getInstance().collection("cursos").document(curso.getId()).collection("turmas")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    turmas.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {



                        String anoTurma = document.getString("ano");
                        String semestreTurma = document.getString("semestre");


                        Turma u = new Turma();
                        u.setId(document.getId());
                        u.setAno(anoTurma);
                        u.setSemestre(semestreTurma);
                        turmas.add(u);


                    }

                    final ArrayAdapter<Turma> adaptador = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, turmas);
                    adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnTurmas.setAdapter(adaptador);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void sendMassage() {
        if (spnTurmas.getSelectedItem() != null) {


            if (checkMudancaDeHorario.isChecked()) {
                mudancaHorario = true;
            } else {
                mudancaHorario = false;
            }


            final Curso curso = (Curso) spnCursos.getSelectedItem();
            nomeCurso = curso.getCurso();

            Turma turma = (Turma) spnTurmas.getSelectedItem();

            txtTurmaAno = turma.getAno();
            txtTurmaSemestre = turma.getSemestre();

            String textMsg = textMensagem.getText().toString();

            textMensagem.setText(null);

            String idProfessor = FirebaseAuth.getInstance().getCurrentUser().getUid();

            SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm");

            SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy");

            Date data = new Date();

            final String dataFormatada = formataData.format(data);
            final String hora_atual = dateFormat_hora.format(data);

            final long timeStamp = System.currentTimeMillis();







            Mensagem mensagem = new Mensagem(idMsg, idProfessor, textMsg, "Prof." + nomeProf, txtTurmaAno, txtTurmaSemestre,
                    dataFormatada, timeStamp, false, mudancaHorario, hora_atual);

            if (!mensagem.getMensagem().isEmpty()) {
                FirebaseFirestore.getInstance().collection("cursos").document(curso.getId()).collection("turmas").document(turma.getId())
                        .collection("mensagens").document(idMsg).set(mensagem);

                FirebaseFirestore.getInstance().collection("professores").document(idProfessor).collection("mensagens")
                        .document(idMsg).set(mensagem);

                Toast.makeText(getApplicationContext(), "mensagem enviada com sucesso", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "mensagem vazia", Toast.LENGTH_SHORT).show();
            }
        }


    }

//    public void buscarNomeProfessor() {
//
//        FirebaseFirestore.getInstance().collection("professores")
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//
//                        Professor p = new Professor();
//                        p.setId(document.getString("id"));
//                        p.setNomeProfessor(document.getString("nomeProfessor"));
//                        nomeProf=p.getNomeProfessor();
//                       }
//
//
//                } else {
//                    Log.w(TAG, "Error getting documents.", task.getException());
//                }
//            }
//        });
//
//    }


    private void carregarSpinnerCurso() {
        FirebaseFirestore.getInstance().collection("cursos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            cursos.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String nomeCurso = document.getString("curso");


                                Curso u = new Curso();
                                u.setId(document.getId());
                                u.setCurso(nomeCurso);

                                cursos.add(u);
                                Log.d(TAG, nomeCurso);

                            }

                            final ArrayAdapter<Curso> adaptador = new ArrayAdapter <>(getBaseContext(), android.R.layout.simple_spinner_item, cursos);
                            adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnCursos.setAdapter(adaptador);
                            adaptador.notifyDataSetChanged();

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.msgProf:

                Intent intent = new Intent(ProfActivity.this, ListarMsgActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        carregarSpinnerTurma();
    }



    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
