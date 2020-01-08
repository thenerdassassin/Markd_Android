package com.schmidthappens.markd.AdapterClasses;

import android.app.DatePickerDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.customer_subactivities.ApplianceEditActivity;
import com.schmidthappens.markd.utilities.StringUtilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditApplianceRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "EditApplRecyclerAdapter";

    private ApplianceEditActivity context;

    public EditApplianceRecyclerViewAdapter(final ApplianceEditActivity context) {
        this.context = context;
    }

    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case R.layout.view_holder_edit_text:
                return new EditTextViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(viewType, parent, false));
            case 2:
                return new DateTextViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.view_holder_edit_text, parent, false));
            case R.layout.view_holder_lifespan:
               return new LifespanViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(viewType, parent, false));
            default:
                throw new IllegalArgumentException(String.format("No view type for %d", viewType));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (position) {
            case 0:
                ((EditTextViewHolder) viewHolder).bindData(position, "Manufacturer", context.manufacturer);
                break;
            case 1:
                ((EditTextViewHolder) viewHolder).bindData(position, "Model", context.model);
                break;
            case 2:
                ((DateTextViewHolder) viewHolder).bindData(position, context.installDate);
                break;
            case 3:
                ((LifespanViewHolder) viewHolder).bindData(position, context.lifespan, context.units);
                break;
            default:
                throw new IllegalArgumentException(String.format("No view holder for %d", position));
        }
    }
    @Override
    public int getItemCount() {
        return 4;
    }
    @Override
    public int getItemViewType(int position) {
        if(position <= 1) {
            return R.layout.view_holder_edit_text;
        } else if(position == 2) {
            return 2;
        } else if(position == 3) {
            return R.layout.view_holder_lifespan;
        }
        throw new IllegalArgumentException(String.format("No view holder for %d", position));
    }

    private void updateField(int fieldNumber, String newValue) {
        switch(fieldNumber) {
            case 0:
                context.manufacturer = newValue;
                break;
            case 1:
                context.model = newValue;
                break;
            case 2:
                context.installDate = newValue;
                break;
            case 3:
                try {
                    context.lifespan = Integer.parseInt(newValue.split("\\s+")[0]);
                    context.units = newValue.split("\\s+")[1];
                } catch (final Exception e) {
                    Log.e(TAG, e.toString());
                }
            default:
                Log.e(TAG, String.format("No field for fieldNumber %d", fieldNumber));
        }
    }

    class EditTextViewHolder extends RecyclerView.ViewHolder {
        private TextInputLayout inputLayout;
        private TextInputEditText textView;
        private int fieldNumber;

        EditTextViewHolder(final View v) {
            super(v);
            inputLayout = v.findViewById(R.id.input_layout);
            textView = v.findViewById(R.id.edit_text);
            textView.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
            textView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        }

        void bindData(final int position, final String hint, final String currentText) {
            fieldNumber = position;
            inputLayout.setHint(hint);
            textView.setText(currentText);

            textView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {
                    updateField(fieldNumber, s.toString());
                }
            });
        }
    }

    class LifespanViewHolder extends RecyclerView.ViewHolder {
        private TextInputEditText lifespanTextView;
        private AutoCompleteTextView unitTextView;
        private int fieldNumber;
        private String timeUnitsSelected;

        LifespanViewHolder(final View v) {
            super(v);
            lifespanTextView = v.findViewById(R.id.edit_text);
            unitTextView = v.findViewById(R.id.units_dropdown);
            unitTextView.setInputType(InputType.TYPE_NULL);
            unitTextView.setAdapter(adapter);
            unitTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "Item Clicked: " + adapter.getItem(position));
                    final String lifespan = lifespanTextView.getText().toString();
                    final String timeUnits = adapter.getItem(position);
                    if(validateValue(lifespan, timeUnits)) {
                        timeUnitsSelected = timeUnits;
                        updateField(fieldNumber, lifespan + " " + timeUnits);
                        clearError();
                    } else {
                        setError(timeUnits);
                    }
                }
            });
            unitTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View focusedView, boolean hasFocus) {
                    if(hasFocus) {
                        context.hideKeyboard(focusedView);
                    }
                }
            });
        }

        void bindData(final int position, final int lifespan, final String timeUnits) {
            fieldNumber = position;
            lifespanTextView.setText(String.valueOf(lifespan));
            if(StringUtilities.isNullOrEmpty(timeUnits)) {
                unitTextView.setText("years", false);
                timeUnitsSelected = "years";
            } else {
                unitTextView.setText(timeUnits, false);
                timeUnitsSelected = timeUnits;
            }

            lifespanTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {
                    final String lifespan = lifespanTextView.getText().toString();
                    final String timeUnits = timeUnitsSelected;
                    if(validateValue(lifespan, timeUnits)) {
                        updateField(fieldNumber, lifespan + " " + timeUnits);
                        clearError();
                    } else {
                        setError(timeUnits);
                    }
                }
            });
        }

        private String[] timeUnits = new String[] {
                "days", "months", "years"
        };

        private final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context, R.layout.dropdown_menu_popup_item, timeUnits
        );

        private boolean validateValue(final String lifespan, final String units) {
            try {
                int numberToValidate = Integer.parseInt(lifespan);
                if (timeUnits[0].equals(units)) {
                    return numberToValidate > 0 && numberToValidate < 366;
                } else if (timeUnits[1].equals(units)) {
                    return numberToValidate > 0 && numberToValidate < 13;
                } else if (timeUnits[2].equals(units)) {
                    return numberToValidate > 0 && numberToValidate < 51;
                }

                return false;
            } catch (final NumberFormatException e) {
                return false;
            }
        }

        private void setError(final String units) {
            String errorMessage = "Invalid Value";
            if (timeUnits[0].equals(units)) {
                errorMessage = "Must be between 1 and 365.";
            } else if (timeUnits[1].equals(units)) {
                errorMessage = "Must be between 1 and 12.";
            } else if (timeUnits[2].equals(units)) {
                errorMessage = "Must be between 1 and 50.";
            }
            lifespanTextView.setError(errorMessage);
        }

        private void clearError() {
            lifespanTextView.setError(null);
        }
    }
    class DateTextViewHolder extends RecyclerView.ViewHolder {
        private TextInputLayout inputLayout;
        private TextInputEditText textView;
        private int fieldNumber;
        private final Calendar myCalendar = Calendar.getInstance();
        private DatePickerDialog.OnDateSetListener date;

        DateTextViewHolder(final View v) {
            super(v);
            inputLayout = v.findViewById(R.id.input_layout);
            textView = v.findViewById(R.id.edit_text);
            textView.setClickable(true);
            textView.setLongClickable(false);
            textView.setFocusable(false);
            date = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel();
                }

            };
        }

        void bindData(final int position, final String currentText) {
            fieldNumber = position;
            inputLayout.setHint("Install Date");
            textView.setText(currentText);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DatePickerDialog datePickerDialog = new DatePickerDialog(context, date,
                            myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                    datePickerDialog.show();
                }
            });
        }

        private void updateLabel() {
            final SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yy", Locale.US);
            final String updatedDate = sdf.format(myCalendar.getTime());
            textView.setText(updatedDate);
            updateField(fieldNumber, updatedDate);
        }
    }
}
