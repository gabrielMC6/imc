package fatec.sp.gov.br.imc;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListView listView = findViewById(R.id.listViewHistory);
        DBHelper dbHelper = new DBHelper(this);
        
        ArrayList<String> historyList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllResults();

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                float bmi = cursor.getFloat(cursor.getColumnIndexOrThrow("bmi"));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"));
                
                historyList.add(String.format(Locale.getDefault(), 
                    "Nome: %s\nIMC: %.2f - %s", name, bmi, comment));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_list_item_1, historyList);
        listView.setAdapter(adapter);
    }
}