package fatec.sp.gov.br.imc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editName, editAge, editWeight, editHeight;
    private TextView textResult;
    private Button btnSave;
    private DBHelper dbHelper;

    private String lastComment;
    private float lastBmi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);

        editName = findViewById(R.id.editName);
        editAge = findViewById(R.id.editAge);
        editWeight = findViewById(R.id.editWeight);
        editHeight = findViewById(R.id.editHeight);
        textResult = findViewById(R.id.textResult);
        
        Button btnCalculate = findViewById(R.id.btnCalculate);
        btnSave = findViewById(R.id.btnSave);
        Button btnHistory = findViewById(R.id.btnHistory);

        btnCalculate.setOnClickListener(v -> calculateIMC());
        btnSave.setOnClickListener(v -> saveIMC());
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    private void calculateIMC() {
        String name = editName.getText().toString().trim();
        String ageStr = editAge.getText().toString().trim();
        String weightStr = editWeight.getText().toString().trim();
        String heightStr = editHeight.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos para calcular", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float weight = Float.parseFloat(weightStr);
            float height = Float.parseFloat(heightStr);

            if (height <= 0) {
                Toast.makeText(this, "Altura inválida", Toast.LENGTH_SHORT).show();
                return;
            }

            lastBmi = weight / (height * height);
            lastComment = getBMIComment(lastBmi);

            String result = String.format(Locale.getDefault(),
                    "Olá %s!\nIMC: %.2f\nClassificação: %s",
                    name, lastBmi, lastComment);
            
            textResult.setText(result);
            btnSave.setEnabled(true);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores numéricos inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveIMC() {
        String name = editName.getText().toString().trim();
        int age = Integer.parseInt(editAge.getText().toString().trim());
        float weight = Float.parseFloat(editWeight.getText().toString().trim());
        float height = Float.parseFloat(editHeight.getText().toString().trim());

        long id = dbHelper.saveResult(name, age, weight, height, lastBmi, lastComment);

        if (id != -1) {
            Toast.makeText(this, "Dados salvos no histórico!", Toast.LENGTH_SHORT).show();
            btnSave.setEnabled(false); // Desabilita após salvar
        } else {
            Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show();
        }
    }

    private String getBMIComment(float bmi) {
        if (bmi < 18.5) return "Abaixo do peso";
        if (bmi < 25) return "Peso normal";
        if (bmi < 30) return "Sobrepeso";
        if (bmi < 35) return "Obesidade Grau I";
        if (bmi < 40) return "Obesidade Grau II";
        return "Obesidade Grau III (mórbida)";
    }
}