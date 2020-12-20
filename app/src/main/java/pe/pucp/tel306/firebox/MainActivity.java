package pe.pucp.tel306.firebox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

import pe.pucp.tel306.firebox.Clases.Users;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("FireBox");

        validacionUsuario();
    }

    public void login(View view) {
        List<AuthUI.IdpConfig> proveedores = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        AuthUI instance = AuthUI.getInstance();
        Intent intent = instance
                .createSignInIntentBuilder()
                .setAvailableProviders(proveedores)
                .build();
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//
        if (requestCode == 1 && resultCode == RESULT_OK) {
            validacionUsuario();
        }
    }

    public void validacionUsuario() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                openFilesAct();
            } else {
                currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (currentUser.isEmailVerified()) {
                            openFilesAct();
                        } else {
                            Toast.makeText(MainActivity.this, "Se le envió un correo para verificar su cuenta", Toast.LENGTH_SHORT).show();
                            currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("debugeo", "Correo Enviado");
                                }
                            });
                        }
                    }
                });
            }
        }


    }

    public void openFilesAct() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection("users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        Users user = new Users(currentUser.getDisplayName(),"Free",26214400,0); //26214400 - 25MB
                        docRef.set(user);
                    }
                } else {
                    Log.d("debugeo", "get failed with ", task.getException());
                }
            }
        });
        startActivity(new Intent(MainActivity.this, FilesActivity.class));
        finish();

    }
}