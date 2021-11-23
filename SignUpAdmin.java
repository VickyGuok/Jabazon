
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class SignUpAdmin extends AppCompatActivity {

    EditText edittextUsername;
    EditText edittextPassword;
    EditText edittextHealthcareAddress;
    EditText edittextAddHealthcareCentreName;
    EditText edittextAddHealthcareCentreAddress;
    EditText edittextAdminEmail;
    EditText edittextAdminStaffID;
    TextView textViewHealthcareCentre;
    Pattern PASSWORD_PATTERN = Pattern.compile("[a-zA-Z0-9\\!\\@\\#\\$\\*]{3,24}");
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[com]+";
    String usernamePattern = "[a-zA-z0-9._-]+@[jabazonAdmin]+\\.+[com]+";
    boolean isUsernameValid,isPasswordValid,isAdminEmailValid,isStaffIDValid,isHealthCareSpinnerValid,isAdditionalHCValid;
    FirebaseFirestore fbfs = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String HCName;
    HealthcareCentres healthcareCentres;
    String item;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    String healthcareAddress;



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

        CollectionReference cfHealthcare = fbfs.collection("Healthcare");
        Spinner HealthcareChosen = findViewById(R.id.spinner_healthcareCentres);
        final ArrayList<String> healthcare = new ArrayList<>();
        ArrayList<String> healthcareListName = new ArrayList<String>();
        ArrayAdapter<String>healthcareArrayAdapter = new ArrayAdapter<String>(SignUpAdmin.this, android.R.layout.simple_spinner_item,healthcareListName);
        healthcareArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        HealthcareChosen.setAdapter(healthcareArrayAdapter);
        cfHealthcare.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        HCName = document.getString("centreName");
                        healthcareListName.add(HCName);
                        healthcare.add(HCName);
                    }
                    healthcareArrayAdapter.notifyDataSetChanged();
                }
            }
        });
        HealthcareChosen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                item = String.valueOf(adapterView.getItemAtPosition(i));
                Log.d("TAG", ""+item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(SignUpAdmin.this, "Please select ",Toast.LENGTH_LONG).show();
            }
        });

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
        if(item.equals("Add Healthcare")){
            Toast.makeText(SignUpAdmin.this,"Please select one healthcare centre",Toast.LENGTH_SHORT).show();
            isHealthCareSpinnerValid = false;
        }else if (item.equals("Add Healthcare") && edittextAddHealthcareCentreAddress.getText().toString().isEmpty() &&
                edittextAddHealthcareCentreName.getText().toString().isEmpty()){
            edittextAddHealthcareCentreName.setText("");
            edittextAddHealthcareCentreName.requestFocus();
            edittextAddHealthcareCentreAddress.setText("");
            edittextAddHealthcareCentreAddress.requestFocus();
            isHealthCareSpinnerValid = false;
        }
        else{
            isHealthCareSpinnerValid = true;
        }

        if((!edittextAddHealthcareCentreName.getText().toString().isEmpty()) && (!edittextAddHealthcareCentreAddress.getText().toString().isEmpty())){
            if(healthcareCentres == null){
                DocumentReference newHCRef = fbfs.collection("Healthcare").document();
                healthcareCentres = new HealthcareCentres();
                healthcareCentres.setCentreName(edittextAddHealthcareCentreName.getText().toString());
                healthcareCentres.setCentreAddress(edittextAddHealthcareCentreAddress.getText().toString());

                fbfs.collection("Healthcare")
                        .document(healthcareCentres.getCentreName())
                        .set(healthcareCentres)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) { finish(); }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpAdmin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else{
                DocumentReference HCRef = fbfs.collection("Healthcare")
                        .document(healthcareCentres.getCentreName());
                HCRef.update("centreName", edittextAddHealthcareCentreName.getText().toString(),
                        "centreAddress", edittextAddHealthcareCentreAddress.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) { finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpAdmin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            isAdditionalHCValid = true;
        }else{
            isAdditionalHCValid = false;
        }

        if(isUsernameValid && isPasswordValid&&isAdminEmailValid && isStaffIDValid&&isHealthCareSpinnerValid && isAdditionalHCValid){
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
                                userInfo.put("Healthcare centre name", item);
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
