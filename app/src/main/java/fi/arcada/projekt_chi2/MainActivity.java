package fi.arcada.projekt_chi2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView firstStatement;
    TextView statement;
    TextView secondStatement;
    TextView firstGroup;
    TextView secondGroup;
    TextView result;
    TextView resultText;
    TextView percent1;
    TextView percent2;
    SharedPreferences sharedPref;
    SharedPreferences.Editor prefEditor;
    EditText inputText;
    // Deklarera 4 Button-objekt
    Button btn1, btn2, btn3, btn4;
    // Deklarera 4 heltalsvariabler för knapparnas värden
    int val1, val2, val3, val4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kopplar en varibel med id
        firstStatement = findViewById(R.id.textViewRow1);
        secondStatement = findViewById(R.id.textViewRow2);
        firstGroup = findViewById(R.id.textViewCol1);
        secondGroup = findViewById(R.id.textViewCol2);
        statement = findViewById(R.id.statement);
        percent1 = findViewById(R.id.percent1);
        percent2 = findViewById(R.id.percent2);
        result = findViewById(R.id.result);
        resultText = findViewById(R.id.resultText);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = sharedPref.edit();

        // Koppla samman Button-objekten med knapparna i layouten
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);

        // Output av statements och grupper
        firstStatement.setText(String.format("%s", sharedPref.getString("firstStatement", "Äger en PS5")));
        secondStatement.setText(String.format("%s", sharedPref.getString("secondStatement", "Äger inte en PS5")));
        firstGroup.setText(String.format("%s", sharedPref.getString("firstGroup", "Barn")));
        secondGroup.setText(String.format("%s", sharedPref.getString("secondGroup", "Vuxen")));
        statement.setText(String.format("%s", sharedPref.getString("firstStatement", "Äger en PS5")));
    }

    /**
     *  Klickhanterare för knapparna
     */
    public void buttonClick(View view) {

        // Skapa ett Button-objekt genom att type-casta (byta datatyp)
        // på det View-objekt som kommer med knapptrycket
        Button btn = (Button) view;

        // Kontrollera vilken knapp som klickats, öka värde på rätt vaiabel
        if (view.getId() == R.id.button1) val1++;
        if (view.getId() == R.id.button2) val2++;
        if (view.getId() == R.id.button3) val3++;
        if (view.getId() == R.id.button4) val4++;

        // Slutligen, kör metoden som ska räkna ut allt!
        calculate();
    }

    /**
     * Metod som uppdaterar layouten och räknar ut själva analysen.
     */
    public void calculate() {

        // Uppdatera knapparna med de nuvarande värdena
        btn1.setText(String.valueOf(val1));
        btn2.setText(String.valueOf(val2));
        btn3.setText(String.valueOf(val3));
        btn4.setText(String.valueOf(val4));

        // Mata in värdena i Chi-2-uträkningen och ta emot resultatet
        // i en Double-variabel
        double chi2 = Significance.chiSquared(val1, val2, val3, val4);

        // Mata in chi2-resultatet i getP() och ta emot p-värdet
        double pValue = Significance.getP(chi2);

        /**
         *  - Visa chi2 och pValue åt användaren på ett bra och tydligt sätt!
         *
         *  - Visa procentuella andelen jakande svar inom de olika grupperna.
         *    T.ex. (val1 / (val1+val3) * 100) och (val2 / (val2+val4) * 100
         *
         *  - Analysera signifikansen genom att jämföra p-värdet
         *    med signifikansnivån, visa reultatet åt användaren
         *
         */

        double sum1 = val1 + val3;
        double sum2 = val2 + val4;
        double percentOne = val1/sum1 * 100;
        double percentTwo = val2/sum2 * 100;
        percent1.setText(String.format(Locale.ENGLISH, "%.1f %%", percentOne));
        percent2.setText(String.format(Locale.ENGLISH,"%.1f %%", percentTwo));

        if (pValue < Double.parseDouble(sharedPref.getString("signifikans", "0.05"))) {
            double resPer1 = 100 - (pValue * 100);
            resultText.setText(String.format(Locale.ENGLISH,"Resultatet är med %.2f %% sannolikhet inte oberoende och kan bettraktas som signifikant, eftersom P-värdet är mindre än signifikansnivån.",
                        resPer1
                    ));
        } else if (pValue > Double.parseDouble(sharedPref.getString("signifikans", "0.05"))) {
            double resPer2 = 100 - (pValue * 100);
            resultText.setText(String.format(Locale.ENGLISH,"Resultatet är med %.2f %% sannolikhet inte oberoende och kan bettraktas som inte signifikant, eftersom P-värdet är större än signifikansnivån.",
                    resPer2
            ));
        }
        result.setText(String.format(Locale.ENGLISH,"Antal värden: %.0f\n\nChi-2 resultat: %.2f\n\nSignifikansnivå: %s\n\nP-värdet: %.2f",
                        sum1 + sum2,
                        chi2,
                        sharedPref.getString("signifikans", "0.05"),
                        pValue
                )
        );

    }
    // Settings knapp
    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    // Reset knapp
    public void reset(View view) {
        val1 = 0;
        val2 = 0;
        val3 = 0;
        val4 = 0;
        btn1.setText(String.valueOf(val1));
        btn2.setText(String.valueOf(val2));
        btn3.setText(String.valueOf(val3));
        btn4.setText(String.valueOf(val4));
        percent1.setText("0");
        percent2.setText("0");
        result.setText("...");
        resultText.setText("...");
    }
}