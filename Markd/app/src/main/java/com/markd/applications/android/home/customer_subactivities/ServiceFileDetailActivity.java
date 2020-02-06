package com.markd.applications.android.home.customer_subactivities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.markd.applications.android.home.R;
import com.markd.applications.android.home.account_authentication.FirebaseAuthentication;
import com.markd.applications.android.home.account_authentication.LoginActivity;
import com.markd.applications.android.home.data_objects.ContractorService;
import com.markd.applications.android.home.data_objects.TempCustomerData;
import com.markd.applications.android.home.file_storage.FirebaseFile;
import com.markd.applications.android.home.utilities.StringUtilities;
import com.markd.applications.android.home.view_initializers.ReplaceableImageHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh Schmidt on 1/27/18.
 */

public class ServiceFileDetailActivity extends AppCompatActivity {
    private final static String TAG = "ServiceFileDetailActvty";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;

    //XML Objects
    private TextInputEditText editFileName;
    private AlertDialog alertDialog;

    private String serviceType;
    private int serviceId;
    private int fileId; // newFile == -1

    private FirebaseFile file;

    private static final int IMAGE_REQUEST_CODE = 934;
    private static final int CAMERA_PERMISSION_CODE = 997;
    private ReplaceableImageHandler imageHandler;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.edit_view_service_image);
        authentication = new FirebaseAuthentication(this);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initializeXMLObjects();
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            sendErrorMessage("Not logged in");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        processIntent(getIntent());
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
        if(customerData != null) {
            customerData.removeListeners();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imageHandler.grantCameraPermission();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == IMAGE_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                file = getFile();
                String oldFileName = file.getFilePath(customerData.getUid());
                Log.d(TAG, "oldFileName:" + oldFileName);
                file.setGuid(null);
                String newFileName = file.getFilePath(customerData.getUid());
                Log.d(TAG, "newFileName:" + newFileName);
                imageHandler.updateImage(newFileName, data);
                saveFile();
            } else {
                Log.d(TAG, "Result not okay");
            }
        } else {
            Log.e(TAG, "Unknown Request");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initializeXMLObjects() {
        editFileName = findViewById(R.id.file_name);
        setEnterButtonToKeyboardDismissal(editFileName);

        imageHandler = new ReplaceableImageHandler();
        ((FrameLayout)findViewById(R.id.service_file_replaceable_image)).addView(
                imageHandler.initialize(
                        this,
                        false,
                        checkForCameraPermission(),
                        IMAGE_REQUEST_CODE,
                        findViewById(R.id.service_file_progress),
                        findViewById(R.id.service_file_edit_form)
        ));
        findViewById(R.id.service_file_delete_button)
                .setOnClickListener(v -> {
                    hideKeyboard(ServiceFileDetailActivity.this.getCurrentFocus());
                    if(fileId < 0) {
                        goBackToActivity();
                    } else {
                        showRemoveFileWarning();
                    }
                });
    }
    private void processIntent(Intent intent) {
        if(intent != null) {
            if(intent.hasExtra("customerId")) {
                customerData = new TempCustomerData(intent.getStringExtra("customerId"), null);
            } else {
                sendErrorMessage("No customerId in intent");
            }
            serviceType = intent.getStringExtra("serviceType");
            serviceId = intent.getIntExtra("serviceId", -1);
            Log.d(TAG, "Setting file id");
            fileId = intent.getIntExtra("fileId", -1);
            if(fileId == -1) {
                setTitle("Add File");
            } else {
                setTitle("Edit File");
                if(intent.hasExtra("fileName")) {
                    final String originalFileName = intent.getStringExtra("fileName");
                    editFileName.setText(originalFileName);
                    editFileName.setSelection(originalFileName.length());
                }
            }

            imageHandler.loadImage(getFile().getFilePath(customerData.getUid()));
        }
    }
    private FirebaseFile getFile() {
        if(file != null) {
            return file;
        } else {
            if(fileId < 0) {
                Log.d(TAG, "Creating new file");
                return new FirebaseFile(getFileName());
            } else {
                ContractorService service = customerData.getServices(serviceType).get(serviceId);
                List<FirebaseFile> files = service.getFiles();
                file = files.get(fileId);
                return file;
            }
        }
    }
    private void saveFile() {
        hideKeyboard(this.getCurrentFocus());

        FirebaseFile file = getFile();
        Log.d(TAG, "File is: " + file.toString());
        file.setFileName(getFileName());

        final ContractorService service = customerData.getServices(serviceType).get(serviceId);
        List<FirebaseFile> files = service.getFiles();
        Log.d(TAG, "Saving file " + fileId);
        if(fileId < 0) {
            if(files == null) {
                files = new ArrayList<>();
            }
            // Setting file id since the file is being saved and we want the saved
            // one updated if it is saved again and not creating duplicates
            getIntent().putExtra("fileId", files.size());
            files.add(file);
        } else {
            files.set(fileId, file);
        }

        Log.d(TAG, "Saving Files: " + files.toString());
        service.setFiles(files);
        customerData.updateService(
                serviceId,
                service.getContractor(),
                service.getDate(),
                service.getComments(),
                files,
                serviceType);
    }
    private String getFileName() {
        String fileName = editFileName.getText().toString();
        if(StringUtilities.isNullOrEmpty(fileName)) {
            fileName = "File";
        }
        return fileName;
    }
    private void showRemoveFileWarning() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete File")
                .setMessage("This action can not be reversed. Are you sure you want to delete this file?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Cancel button
                        Log.d(TAG, "Cancel the file deletion");
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Delete button
                        Log.d(TAG, "Confirm the file deletion");
                        deleteFile();
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }
    private void deleteFile() {
        ContractorService service = customerData.getServices(serviceType).get(serviceId);
        List<FirebaseFile> files = service.getFiles();
        files.remove(fileId);
        service.setFiles(files);
        customerData.updateService(
                serviceId,
                service.getContractor(),
                service.getDate(),
                service.getComments(),
                files,
                serviceType);
        goBackToActivity();
    }

    @Override
    public boolean onSupportNavigateUp(){
        Log.i(TAG, "Navigate Up");
        saveFile();
        goBackToActivity();
        return true;
    }
    private boolean checkForCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[] {
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE },
                    CAMERA_PERMISSION_CODE);
            return false;
        } else {
            return true;
        }
    }
    private void goBackToActivity() {
        hideKeyboard(this.getCurrentFocus());
        finish();
    }
    private void setEnterButtonToKeyboardDismissal(final EditText view) {
        view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard(v);
                    return true;
                }
                return false;
            }
        });
    }
    private void hideKeyboard(View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    private void sendErrorMessage(String message) {
        Log.e(TAG, message);
        Toast.makeText(getApplicationContext(), "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
        goBackToActivity();
    }
}