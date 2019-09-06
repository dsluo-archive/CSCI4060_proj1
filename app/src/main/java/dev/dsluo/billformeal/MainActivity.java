package dev.dsluo.billformeal;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * A basic tip calculator.
 *
 * @author David Luo
 */

public class MainActivity extends AppCompatActivity {

    private TextView priceTotalResult;
    private TextView perPersonResult;
    private EditText priceFoodField;
    private EditText partySizeField;
    private Button[] tipButtons;

    private final int UNSELECTED = Color.LTGRAY;
    private final int SELECTED = Color.GRAY;

    // from https://stackoverflow.com/a/5233488
    private TextWatcher moneyFormat = new TextWatcher() {
        private String current = "";

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        /**
         * Ensures that the text entered as "Price of food" is shown as money and only accepts money as input.
         */
        @Override
        public void onTextChanged(CharSequence seq, int start, int end, int count) {
            if (!seq.toString().equals(current)) {
                priceFoodField.removeTextChangedListener(this);

                String cleanString = seq.toString().replaceAll("[$,.]", "");
                double parsed = Double.parseDouble(cleanString);
                String formatted = NumberFormat.getCurrencyInstance().format(parsed / 100.0);
                current = formatted;

                priceFoodField.setText(formatted);
                priceFoodField.setSelection(formatted.length());
                priceFoodField.addTextChangedListener(this);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    /**
     * onCreate for the main activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        priceTotalResult = findViewById(R.id.priceTotal);
        perPersonResult = findViewById(R.id.perPerson);
        priceFoodField = findViewById(R.id.priceFood);
        partySizeField = findViewById(R.id.partySize);
        tipButtons = new Button[] {
                findViewById(R.id.tip10),
                findViewById(R.id.tip15),
                findViewById(R.id.tip18),
        };

        priceFoodField.addTextChangedListener(moneyFormat);

        for (Button button : tipButtons) {
            button.setOnClickListener(this::onClick);
            button.setBackgroundColor(UNSELECTED);
        }
    }

    /**
     * OnClickListener for the tip buttons. Does all the calculations.
     *
     * @param view The button.
     */
    public void onClick(View view) {
        double priceFood;
        int partySize;
        double tipPercent;

        try {
            String cleanedPriceFood = priceFoodField.getText().toString().replaceAll("[$,.]", "");
            priceFood = Double.parseDouble(cleanedPriceFood) / 100.0;
        } catch (NumberFormatException ex) {
            Toast.makeText(
                    getApplicationContext(),
                    "Enter the price of the food to calculate.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        try {
            partySize = Integer.parseInt(partySizeField.getText().toString());
        } catch (NumberFormatException ex) {
            partySize = 1;
            partySizeField.setText("1");
        }

        switch (view.getId()) {
            case R.id.tip10:
                tipPercent = 0.1;
                break;
            case R.id.tip15:
                tipPercent = 0.15;
                break;
            case R.id.tip18:
                tipPercent = 0.18;
                break;
            default:
                return;
        }

        for (Button button : tipButtons) {
            if (button.getId() == view.getId())
                button.setBackgroundColor(SELECTED);
            else
                button.setBackgroundColor(UNSELECTED);
        }


        double tip = tipPercent * priceFood;
        double priceTotal = priceFood + tip;
        double perPerson = priceTotal / partySize;

        priceTotalResult.setText(String.format(
                Locale.getDefault(),
                "$%.2f",
                Math.round(priceTotal * 100.0) / 100.0
        ));
        perPersonResult.setText(String.format(
                Locale.getDefault(),
                "$%.2f",
                Math.round(perPerson * 100.0) / 100.0
        ));

    }
}
