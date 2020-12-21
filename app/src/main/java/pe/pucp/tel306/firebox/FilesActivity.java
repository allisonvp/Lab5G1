package pe.pucp.tel306.firebox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import pe.pucp.tel306.firebox.Clases.Files;
import pe.pucp.tel306.firebox.Clases.PrivateFiles;
import pe.pucp.tel306.firebox.Fragmentos.ListaArchivosPub;

public class FilesActivity extends AppCompatActivity {
    // Make sure to use the FloatingActionButton
    // for all the FABs
    FloatingActionButton mAddSto, mAddFile;

    // These are taken to make visible and invisible along
    // with FABs
    TextView addFileActionText;
    // to check whether sub FAB buttons are visible or not.
    Boolean isAllFabsVisible;
    private static final int FILE_UPLOAD = 1;
    private static final int FILE_PERMISSION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        setTitle("Profile");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            TextView textViewBienvenido = findViewById(R.id.textViewBienvenido);
            textViewBienvenido.setText("Bienvenido " + displayName);
        }
        openfilesFragment();

        setFloatingButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.files_menu, menu);
        return true;
    }

    public void openfilesFragment() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(currentUser.getUid()).collection("files")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Files> lista = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Files file = document.toObject(Files.class);
                                lista.add(file);
                            }
                            FragmentManager fm = getSupportFragmentManager();
                            Fragment fragment = fm.findFragmentById(R.id.id_fragment_files);
                            if (fragment != null) {
                                fm.beginTransaction()
                                        .remove(fragment)
                                        .commit();
                            }
                            ListaArchivosPub listFragment = ListaArchivosPub.newInstance(lista, FilesActivity.this);
                            getSupportFragmentManager().beginTransaction()
                                    .add(R.id.id_fragment_files, listFragment)
                                    .commit();

                        } else {
                            Log.d("debugeo", "Error getting documents: ", task.getException());
                        }
                    }
                });

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

        // Also register the action name text, of all the FABs.
        addFileActionText = findViewById(R.id.add_file_action_text);

        // Now set all the FABs and all the action name
        // texts as GONE
        mAddFile.setVisibility(View.GONE);
        addFileActionText.setVisibility(View.GONE);


        isAllFabsVisible = false;

        mAddSto.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {
                            mAddFile.show();
                            addFileActionText.setVisibility(View.VISIBLE);

                            isAllFabsVisible = true;
                        } else {

                            mAddFile.hide();
                            addFileActionText.setVisibility(View.GONE);

                            isAllFabsVisible = false;
                        }
                    }
                });

        mAddFile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        selectFile();

                    }
                });


    }

    UploadTask task = null;
    ProgressBar progressBar = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == FILE_UPLOAD) {
                subirArchivo(data.getData());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == FILE_PERMISSION) {
                selectFile();
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public long getFileSize(Uri uri) {
        String fileSize = null;
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {

                // get file size
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (!cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getString(sizeIndex);
                }
            }
        } finally {
            cursor.close();
        }
        return Long.parseLong(fileSize);
    }

    public void subirArchivo(Uri uri) {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
            String uid = currentUser.getUid();
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
            task = storageReference.child(uid + "/" + getFileName(uri))
                    .putFile(uri);

            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(FilesActivity.this, "Archivo subido exitosamente", Toast.LENGTH_SHORT).show();
                    Log.d("debugeo", "subida exitosa");
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    DocumentReference docRef = db.collection("users").document(currentUser.getUid()).collection("files").document(getFileName(uri));
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    docRef.set(new Files(getFileName(uri), getFileSize(uri), currentUser.getUid() + "/" + getFileName(uri),dtf.format(now)));
                    openfilesFragment();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
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

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        progressBar.setProgress(progreso, true);
                    } else {
                        progressBar.setProgress(progreso);
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (task != null) {
            progressBar.setVisibility(View.GONE);
            task.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (task != null && task.isInProgress()) {
            progressBar.setVisibility(View.GONE);
            task.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (task != null && task.isPaused()) {
            progressBar.setVisibility(View.VISIBLE);
            task.resume();
        }
    }

    public void selectFile() {
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("*/*").addCategory(Intent.CATEGORY_OPENABLE);
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            Log.d("debugeo", "ABIERTO");
            startActivityForResult(Intent.createChooser(intent, "Seleccionar archivo"), FILE_UPLOAD);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, FILE_PERMISSION);
        }
    }
}