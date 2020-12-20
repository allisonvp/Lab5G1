package pe.pucp.tel306.firebox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;

public class FilesActivity extends AppCompatActivity {
    // Make sure to use the FloatingActionButton
    // for all the FABs
    FloatingActionButton mAddSto, mAddFile, mAddPrivate;

    // These are taken to make visible and invisible along
    // with FABs
    TextView addFileActionText, addPrivateActionText;
    // to check whether sub FAB buttons are visible or not.
    Boolean isAllFabsVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        setTitle("Profile");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            TextView textViewBienvenido = findViewById(R.id.textViewBienvenido);
            textViewBienvenido.setText("Bienvenido " + displayName);
        }
        setFloatingButton();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.files_menu, menu);
        return true;
    }

    public void logout(MenuItem menuitem) {
        AuthUI instance = AuthUI.getInstance();
        instance.signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(FilesActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    public void setFloatingButton() {
        // Register all the FABs with their IDs
        // This FAB button is the Parent
        mAddSto = findViewById(R.id.add_storage);
        // FAB button
        mAddFile = findViewById(R.id.add_file);
        mAddPrivate = findViewById(R.id.add_private);

        // Also register the action name text, of all the FABs.
        addFileActionText = findViewById(R.id.add_private_action_text);
        addPrivateActionText = findViewById(R.id.add_file_action_text);

        // Now set all the FABs and all the action name
        // texts as GONE
        mAddFile.setVisibility(View.GONE);
        mAddPrivate.setVisibility(View.GONE);
        addFileActionText.setVisibility(View.GONE);
        addPrivateActionText.setVisibility(View.GONE);

        isAllFabsVisible = false;

        mAddSto.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {
                            mAddFile.show();
                            mAddPrivate.show();
                            addFileActionText.setVisibility(View.VISIBLE);
                            addPrivateActionText.setVisibility(View.VISIBLE);

                            isAllFabsVisible = true;
                        } else {

                            mAddFile.hide();
                            mAddPrivate.hide();
                            addFileActionText.setVisibility(View.GONE);
                            addPrivateActionText.setVisibility(View.GONE);

                            isAllFabsVisible = false;
                        }
                    }
                });

        mAddFile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(FilesActivity.this, "Archivo clickeado", Toast.LENGTH_SHORT).show();
                    }
                });


        mAddPrivate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(FilesActivity.this, "Archivo privado clickeado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    UploadTask task = null;
    ProgressBar progressBar = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            subirArchivo(data.getData());
        }

    }

    public void subirArchivo(Uri uri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        Log.d("debugeo", uri.getPath());

        task = storageReference.child("archivo.jpg")
                .putFile(uri);
        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("debugeo", "subida exitosa");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("debugeo", "error en la subida");
                e.printStackTrace();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                long bytesTransferred = snapshot.getBytesTransferred();
                long totalByteCount = snapshot.getTotalByteCount();

                int progreso = (int) Math.round((100.0 * bytesTransferred) / totalByteCount);

                Log.d("debugeo", "progreso: " + progreso + "%");

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (task != null && task.isInProgress()) {
            task.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (task != null && task.isPaused()) {
            task.resume();
        }
    }

    public void subirFile(View view) {
        Intent intent = new Intent(this, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowFiles(true)
                .enableImageCapture(true)
                .setMaxSelection(1)
                .setSkipZeroSizeFiles(true)
                .build());
        startActivityForResult(intent, 1);
    }
}