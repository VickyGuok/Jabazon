
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class SignUpAdmin extends AppCompatActivity {

    EditText edittextUsername;
    EditText edittextPassword;
    EditText edittextAddHealthcareCentreName;
    EditText edittextAddHealthcareCentreAddress;
    EditText edittextAdminEmail;
    EditText edittextAdminStaffID;
    TextView textViewHealthcareCentre;
    Pattern PASSWORD_PATTERN = Pattern.compile("[a-zA-Z0-9\\!\\@\\#\\$\\*]{3,24}");
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[com]+";
    String usernamePattern = "[a-zA-z0-9._-]+@[jabazonAdmin]+\\.+[com]+";
    Spinner healthCareCentreSpinner;
    boolean isUsernameValid,isPasswordValid,isAdminEmailValid,isStaffIDValid,isHealthCareSpinnerValid;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fbfs = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_admin);

        edittextUsername = findViewById(R.id.edit_text_Ausername);
        edittextPassword = findViewById(R.id.edit_text_Apassword);
        edittextAddHealthcareCentreName = findViewById(R.id.edit_text_AddHealthcareCentreName);
        edittextAddHealthcareCentreAddress = findViewById(R.id.edit_text_AddHealthcareCentreAddress);
        edittextAdminEmail = findViewById(R.id.edit_text_Aemail);
        edittextAdminStaffID = findViewById(R.id.edit_text_AstaffID);
        textViewHealthcareCentre = findViewById(R.id.text_view_select_healthcare_centre);
        healthCareCentreSpinner = findViewById(R.id.spinner_healthcareCentres);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.healthcareCentres, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        healthCareCentreSpinner.setAdapter(adapter);
    }
    private void checkDataValidation(){
        //to check if username is valid
        if(edittextUsername.getText().toString().isEmpty()){
            edittextUsername.setError("Please enter this field");
            isUsernameValid = false;
        }else if(!(edittextUsername.getText().toString()).matches(usernamePattern)){
            edittextUsername.setError("Please include @jabazonAdmin.com");
            isUsernameValid = false;
        }else{
            isUsernameValid = true;
        }
        //to check if email is valid
        if(edittextAdminEmail.getText().toString().isEmpty()){
            edittextAdminEmail.setError("Please enter this field");
            isAdminEmailValid = false;
        }else if(!(edittextAdminEmail.getText().toString()).matches(emailPattern)){
            edittextAdminEmail.setError("Invalid Email Address");
            isAdminEmailValid = false;
        }
        else{
            isAdminEmailValid = true;
        }
        //to check if password is valid
        if(edittextPassword.getText().toString().isEmpty()){
            edittextPassword.setError("Please enter this field");
            isPasswordValid = false;
        }else if(!PASSWORD_PATTERN.matcher(edittextPassword.getText().toString()).matches()) {
            edittextPassword.setError("Please include numeric digits and symbols like !@#$* in your password.");
            isPasswordValid = false;
        }else{
            isPasswordValid = true;
        }
        //to check if staff id is empty
        if(edittextAdminStaffID.getText().toString().isEmpty()){
            edittextAdminStaffID.setError("Please enter this field");
            isStaffIDValid = false;
        }
        else{
            isStaffIDValid = true;
        }
        if(healthCareCentreSpinner.getSelectedItem().equals("Select One")){
            textViewHealthcareCentre.setError("Please select one healthcare centres");
            isHealthCareSpinnerValid = false;
        }else if (healthCareCentreSpinner.getSelectedItem().equals("Add new healthcare centre")){
            edittextAddHealthcareCentreName.setText("");
            edittextAddHealthcareCentreName.requestFocus();
            edittextAddHealthcareCentreAddress.setText("");
            edittextAddHealthcareCentreAddress.requestFocus();
            isHealthCareSpinnerValid = false;
        }
        else{
            isHealthCareSpinnerValid = true;
        }

        if(isUsernameValid&&isPasswordValid&&isAdminEmailValid && isStaffIDValid&&isHealthCareSpinnerValid){
            AlertDialog.Builder ADBuilder2 = new AlertDialog.Builder(this);
            ADBuilder2.setTitle("Account created successfully");
            ADBuilder2.setMessage("Account has been created! Please proceed to login");
            ADBuilder2.setCancelable(false);
            ADBuilder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    dialogInterface.cancel();
                }
            });
            AlertDialog alert22 = ADBuilder2.create();
            alert22.show();
        }
    }

    public void signUpButtonAdminOnClick(View view){
        hideKeyboard(SignUpAdmin.this, view);
        checkDataValidation();
            mAuth.createUserWithEmailAndPassword(edittextUsername.getText().toString(), edittextPassword.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                DocumentReference df = fbfs.collection("Users").document(user.getUid());
                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("Username ", edittextUsername.getText().toString());
                                userInfo.put("Password", edittextPassword.getText().toString());
                                userInfo.put("Healthcare centre name", healthCareCentreSpinner.getSelectedItem().toString());
                                userInfo.put("Staff ID", edittextAdminStaffID.getText().toString());
                                userInfo.put("Email", edittextAdminEmail.getText().toString());
                                //to specify user is admin
                                userInfo.put("isAdmin", "1");
                                df.set(userInfo);
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpAdmin.this, "Failed to create an account", Toast.LENGTH_SHORT).show();
                }
            });
    }
    private void hideKeyboard(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void ShowHiddenPasswordOnClick(View view) {
        if(view.getId()==R.id.show_password_button){
            if(edittextPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                edittextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                edittextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }

    }
}
