package com.example.professortcc.controle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.professortcc.R;
import com.example.professortcc.modelo.Mensagem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListarMsgActivity extends AppCompatActivity {

    private ListView aliasListMsg;
    private List<Mensagem> mensagens;
    private String id;

    private AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_msg);

        getSupportActionBar().setTitle("Mensagens enviadas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        aliasListMsg = findViewById(R.id.editListMsg);
        mensagens = new ArrayList<>();


        id = FirebaseAuth.getInstance().getCurrentUser().getUid();


        carregarMsg();



       aliasListMsg.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Alerta!");
                builder.setMessage("Deseja mesmo excluir essa mensangem?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        String idProf = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        FirebaseFirestore.getInstance().collection("professores").document(idProf).collection("mensagens")
                    .document(mensagens.get(position).getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ListarMsgActivity.this, "Mensagem excluida com sucesso!", Toast.LENGTH_LONG).show();
                                carregarMsg();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ListarMsgActivity.this, "Erro ao deletar mensagem", Toast.LENGTH_SHORT).show();
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
                return true;
            }
        });
    }

    public void carregarMsg(){


        FirebaseFirestore.getInstance().collection("professores").document(id).collection("mensagens").orderBy("timeMassage", Query.Direction.DESCENDING).limit(20)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mensagens.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String remetente = document.getString("remetenteMsg");
                                String data = document.getString("dataMensagem");
                                String id = document.getString("id");
                                String idRemetente = document.getString("idRemetente");
                                String mensagem = document.getString("mensagem");
                                Boolean mudanca = document.getBoolean("mudancaHorario");
                                Boolean paraTodos = document.getBoolean("paraTodos");
                                String semestre = document.getString("semestreMensagem");
                                long time = document.getLong("timeMassage");
                                String turmaAno = document.getString("turmaAnoMensagem");
                                String hora = document.getString("hora_atual");
                                String curso = document.getString("cursoMsg");

                                Mensagem u = new Mensagem(id, idRemetente, mensagem, remetente, turmaAno, semestre, data, time, paraTodos, mudanca,hora, curso);
                                mensagens.add(u);

                            }
                            ArrayAdapter<Mensagem> adaptador = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, mensagens);
                            aliasListMsg.setAdapter(adaptador);
                            adaptador.notifyDataSetChanged();



                            if(mensagens.isEmpty()){

                                Toast.makeText(ListarMsgActivity.this, "não há mensagens nesta turma", Toast.LENGTH_SHORT).show();
                            }

                        } else {

                        }
                    }
                });
    }
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                //startActivity(new Intent(ManterAdmin.this, Admin.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                Intent intent = new Intent(ListarMsgActivity.this, ProfActivity.class);
                startActivity(intent);
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }

}
