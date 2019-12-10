package com.example.professortcc.controle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.professortcc.R;
import com.example.professortcc.Services.MySingleton;
import com.example.professortcc.modelo.Aluno;
import com.example.professortcc.modelo.Curso;
import com.example.professortcc.modelo.Mensagem;
import com.example.professortcc.modelo.Professor;
import com.example.professortcc.modelo.Turma;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProfActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText textMensagem;
    private List<Curso> cursos;
    private List<Turma> turmas;
    private List<Aluno> alunos;
    private Spinner spnCursos;
    private TextView aliaslinkBaixarGrade;
    private Spinner spnTurmas;
    private String nomeProf;
    private CheckBox checkMudancaDeHorario;
    private boolean mudancaHorario;
    private String txtTurmaAno, txtTurmaSemestre;
    private Button enviarMensagem;
    private String idMsg;
    private String aliasurlGrade;
    private String nomeCurso;
    private AlertDialog alerta;
    private final static String TAG  = "Firelog";

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAo7mweGA:APA91bGDHvSeiMjst5UU0sEhkyQ0Kga7_Nykjj9GnqA0stYRDEitbhuseng2ZDrBIEQjHYmwB6CMb_TLuD7ePP0vocyJB1iyDtplqC-vjqA434gFkhrzC3BqKP987w6TPFEJjdZRuPfs";
    final private String contentType = "application/json";
    final String TAG2 = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC, TOKEN;



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
        alunos = new ArrayList<>();


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


                final Curso curso = (Curso) spnCursos.getSelectedItem();
                final Turma turma = (Turma) spnTurmas.getSelectedItem();


                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Alerta!");
                builder.setMessage("Deseja mesmo baixar a grade de "+turma.getAno()+"/"+ turma.getSemestre()+"-"+ curso.getCurso()+"?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        FirebaseFirestore.getInstance().collection("cursos").document(curso.getId())
                                .collection("turmas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.getId().equals(turma.getId())) {

                                            String id = document.getString("id");
                                            String urlGrade = document.getString("urlGrade");

                                            Turma u = new Turma();

                                            u.setId(id);
                                            u.setUrlGrade(urlGrade);

                                            aliasurlGrade = urlGrade;

                                        }
                                    }
                                    if(aliasurlGrade.isEmpty()){

                                        Toast.makeText(ProfActivity.this, "Essa turma não possui grade de horários", Toast.LENGTH_LONG).show();

                                    }
                                    else{
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(aliasurlGrade));
                                        startActivity(browserIntent);
                                    }

                                } else {

                                }
                            }
                        });

                    }
                });

                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(getApplicationContext(), "Ação cancelada", Toast.LENGTH_SHORT).show();
                    }
                });
                alerta = builder.create();
                alerta.show();

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
                    dataFormatada, timeStamp, false, mudancaHorario, hora_atual, nomeCurso);

            if (!mensagem.getMensagem().isEmpty()) {
                FirebaseFirestore.getInstance().collection("cursos").document(curso.getId()).collection("turmas").document(turma.getId())
                        .collection("mensagens").document(idMsg).set(mensagem);

                FirebaseFirestore.getInstance().collection("professores").document(idProfessor).collection("mensagens")
                        .document(idMsg).set(mensagem);

                FirebaseFirestore.getInstance().collection("mensagens").document(mensagem.getId()).set(mensagem);

                Toast.makeText(getApplicationContext(), "mensagem enviada com sucesso", Toast.LENGTH_SHORT).show();

                prepararNotificacao(mensagem);
            }
            else{
                Toast.makeText(getApplicationContext(), "mensagem vazia", Toast.LENGTH_SHORT).show();
            }
        }

    }

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


private void prepararNotificacao(Mensagem mensagem){
    TOPIC = "/topics/userABC"; //topic has to match what the receiver subscribed to
    NOTIFICATION_TITLE = mensagem.getTurmaAnoMensagem() +"/"+ mensagem.getSemestreMensagem()
            + "-" +  mensagem.getCursoMsg();
    NOTIFICATION_MESSAGE = mensagem.getMensagem();

    listadealunos();



}

    private void listadealunos() {

        final Curso curso = (Curso) spnCursos.getSelectedItem();
        final Turma turma = (Turma) spnTurmas.getSelectedItem();
        FirebaseFirestore.getInstance().collection("cursos").document(curso.getId())
                .collection("turmas").document(turma.getId())
                .collection("alunos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            alunos.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                    String id = document.getId();

                                    String token = document.getString("token");

//                                String nomeCurso = document.getString("curso");
//
//
//                                Curso u = new Curso();
//                                u.setId(document.getId());
//                                u.setCurso(nomeCurso);
                                Aluno a = new Aluno();
                                a.setId(id);
                                a.setToken(token);
                                alunos.add(a);

                            }

                        }
                    }
                });

        for (int i=0;i<alunos.size(); i++) {


            JSONObject notification = new JSONObject();
            JSONObject notifcationBody = new JSONObject();
            try {
                notifcationBody.put("title", NOTIFICATION_TITLE);
                notifcationBody.put("message", NOTIFICATION_MESSAGE);

                notification.put("to", alunos.get(i).getToken());
                //notification.put("to", TOPIC);
                notification.put("data", notifcationBody);
                Log.i("notifica", alunos.get(i).toString());
            } catch (JSONException e) {
                Log.e(TAG, "onCreate: " + e.getMessage());
            }
            sendNotification(notification);
        }

    }


    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
//                        edtTitle.setText("");
//                        edtMessage.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfActivity.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

}
