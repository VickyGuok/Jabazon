
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class checkVaccinationStatus extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RelativeLayout relativeLayout;
    EditText batchNo, vaccine, status, healthcare,remarks;
    UserVaccineAppointment vaxAppt;
    Button buttonOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_vaccination_status);

        relativeLayout = findViewById(R.id.layout_vaccinationStatus);
        batchNo = findViewById(R.id.vaccination_batchNo);
        vaccine = findViewById(R.id.vaccination_vaccine);
        status = findViewById(R.id.vaccination_Status);
        healthcare = findViewById(R.id.vaccination_healthcare);
        buttonOK = findViewById(R.id.button_ok);
        remarks = findViewById(R.id.vaccination_Remarks);

        getCurrentUserAppointment();

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(checkVaccinationStatus.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getCurrentUserAppointment() {
        if (vaxAppt == null) {
            db.collection("Vaccination Appointment").whereEqualTo("userID", LoginActivity.User_ID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            vaxAppt = document.toObject(UserVaccineAppointment.class);
                            String batchNumFb = document.getString("batchNo").toString();
                            batchNo.setText(batchNumFb);
                            String vaccineFb = document.getString("selectedVaccine").toString();
                            vaccine.setText(vaccineFb);
                            String healthcareFb = document.getString("selectedHealthcare").toString();
                            healthcare.setText(healthcareFb);
                            String statusFb = document.getString("status").toString();
                            status.setText(statusFb);
                            String remarksFb = document.getString("remarks").toString();
                            remarks.setText(remarksFb);
                        }
                    }
                }
            });
        } else {
            AlertDialog.Builder ADBuilder1 = new AlertDialog.Builder(checkVaccinationStatus.this);
            ADBuilder1.setMessage("No Appointment made");
            ADBuilder1.setCancelable(true);
            ADBuilder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                    startActivity(intent);
                }
            });
            AlertDialog alert11 = ADBuilder1.create();
            alert11.setTitle("Appointment status");
            alert11.show();
        }
    }
}
