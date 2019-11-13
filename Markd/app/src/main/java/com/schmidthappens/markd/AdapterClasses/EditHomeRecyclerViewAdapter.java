package com.schmidthappens.markd.AdapterClasses;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.customer_subactivities.HomeEditActivityV2;

import java.util.Locale;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class EditHomeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "EditHomeRecyclerAdapter";

    private final InputMethodManager IMM;
    private HomeEditActivityV2 context;

    public EditHomeRecyclerViewAdapter(final HomeEditActivityV2 context) {
        this.context = context;
        IMM = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0) {
            return new IntegerStepperViewHolder(LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.view_holder_number_picker, parent, false));
        }

        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(viewType, parent, false);
        switch (viewType) {
            case R.layout.view_holder_edit_text:
                return new EditTextViewHolder(view);
            case R.layout.dropdown_menu_state:
                return new DetailDisclosureViewHolder(view);
            case R.layout.view_holder_number_picker:
                return new DoubleStepperViewHolder(view);
            default:
                throw new IllegalArgumentException(String.format("No view type for %d", viewType));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (position) {
            case 0:
                ((EditTextViewHolder)viewHolder).bindData(position, "Street Address", context.street);
                break;
            case 1:
                ((EditTextViewHolder)viewHolder).bindData(position, "City", context.city);
                break;
            case 2:
                ((DetailDisclosureViewHolder)viewHolder).bindData(position, context.state);
                break;
            case 3:
                ((EditTextViewHolder)viewHolder).bindData(position, "Zip Code", context.zipCode);
                break;
            case 4:
                ((DoubleStepperViewHolder)viewHolder)
                        .bindData(position, context.bedrooms, "Bedrooms");
                break;
            case 5:
                ((DoubleStepperViewHolder)viewHolder)
                        .bindData(position, context.bathrooms, "Bathrooms");
                break;
            case 6:
                ((IntegerStepperViewHolder)viewHolder)
                        .bindData(position, context.squareFootage, "Square Footage");
                break;
            default:
                throw new IllegalArgumentException(String.format("No view holder for %d", position));
        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }
    @Override
    public int getItemViewType(int position) {
        if (position == 0 ||
            position == 1 ||
            position == 3) {
            return R.layout.view_holder_edit_text;
        } else if (position == 2) {
            return R.layout.dropdown_menu_state;
        } else if (position == 6) {
            return 0; //Used to differentiate Integer Picker from Double
        }
        return R.layout.view_holder_number_picker;
    }

    private void updateField(int fieldNumber, String newValue) {
        switch (fieldNumber) {
            case 0:
                context.street = newValue;
                break;
            case 1:
                context.city = newValue;
                break;
            case 2:
                context.state = newValue;
                break;
            case 3:
                context.zipCode = newValue;
                break;
            case 4:
                context.bedrooms = Double.valueOf(newValue);
                break;
            case 5:
                context.bathrooms = Double.valueOf(newValue);
                break;
            case 6:
                context.squareFootage = Integer.valueOf(newValue);
                break;
            default:
                Log.e(TAG, String.format("No field for fieldNumber %d", fieldNumber));
        }
    }

    private void hideKeyboard(View view) {
        IMM.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    class EditTextViewHolder extends RecyclerView.ViewHolder {
        private static final int ZIP_CODE_POSITION = 3;
        private EditText textView;
        private String originalValue;
        private int fieldNumber;

        EditTextViewHolder(final View v) {
            super(v);
            textView = v.findViewById(R.id.edit_text_box);
        }

        void bindData(final int position, final String hint, final String currentText) {
            fieldNumber = position;
            textView.setHint(hint);
            originalValue = currentText;
            textView.setText(currentText);

            //ZipCode
            if (fieldNumber == ZIP_CODE_POSITION) {
                textView.setInputType(InputType.TYPE_CLASS_NUMBER);
            }

            textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if(fieldNumber == ZIP_CODE_POSITION
                                && textView.getText().toString().length() != 5) {
                            textView.setError("Invalid Zip Code");
                        } else {
                            updateField(
                                    fieldNumber,
                                    ((EditText)v).getText().toString());
                        }
                        hideKeyboard(v);
                    }
                }
            });
        }
    }

    class DetailDisclosureViewHolder extends RecyclerView.ViewHolder {
        private AutoCompleteTextView textView;
        private int fieldNumber;

        DetailDisclosureViewHolder(final View v) {
            super(v);
            textView = v.findViewById(R.id.state_dropdown);
            textView.setAdapter(adapter);
            textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "Item Clicked: " + adapter.getItem(position));
                    updateField(fieldNumber, adapter.getItem(position));
                }
            });
        }

        void bindData(final int position, final String currentText) {
            fieldNumber = position;
            textView.setText(currentText, false);
        }

        private final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                R.layout.dropdown_menu_popup_item,
                new String[] {
                        "AK", "AL", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
                        "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
                        "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
                        "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
                        "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
                });
    }

    class IntegerStepperViewHolder extends StepperViewHolder {
        private int fieldNumber;
        private Integer currentIntegerValue;

        IntegerStepperViewHolder(final View v) {
            super(v);
            stepValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Log.v(TAG, "IntegerStepperViewHolder beforeTextChanged");
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Log.v(TAG, "IntegerStepperViewHolder onTextChanged");

                    try {
                        final int integerValue = Integer.valueOf(charSequence.toString());
                        if(validate(integerValue)) {
                            currentIntegerValue = integerValue;
                            updateField(fieldNumber, charSequence.toString());
                        }
                    } catch (final NumberFormatException e) {
                        stepValue.setError(detailText.getText() + " must be a multiple of 50");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Log.v(TAG, "IntegerStepperViewHolder afterTextChanged");
                }
            });
        }

        void bindData(final int position, final Integer currentValue, final String detail) {
            if(currentValue == null) {
                currentIntegerValue = 1250;
            }

            fieldNumber = position;
            currentIntegerValue = currentValue;
            detailText.setText(detail);
            setValue(currentIntegerValue);
            minus.setOnClickListener(clickListener);
            plus.setOnClickListener(clickListener);
        }

        void incrementValue() {
            int newValue = currentIntegerValue + 50;
            if (validate(newValue)) {
                currentIntegerValue = newValue;
                setValue(currentIntegerValue);
            }
        }

        void decrementValue() {
            int newValue = currentIntegerValue - 50;
            if (validate(newValue)) {
                currentIntegerValue = newValue;
                setValue(currentIntegerValue);
            }
        }

        private boolean validate(final int value) {
            if (value % 50 != 0) {
                stepValue.setError(detailText.getText() + " must be a multiple of 50");
            } else if (value < 600) {
                stepValue.setError("600 is the minimum "+ detailText.getText());
            } else if (value > 40000) {
                stepValue.setError("40,000 is the maximum " + detailText.getText());
            } else {
                return true;
            }
            return false;
        }

        private void setValue(int value) {
            final String currentValue = String.format(Locale.ENGLISH, "%d", value);
            stepValue.setText(currentValue);
            updateField(fieldNumber, currentValue);
        }
    }

    class DoubleStepperViewHolder extends StepperViewHolder {
        private int fieldNumber;
        private Double currentDoubleValue;

        DoubleStepperViewHolder(final View v) {
            super(v);
            stepValue.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
            stepValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Log.v(TAG, "DoubleStepperViewHolder beforeTextChanged");
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Log.v(TAG, "DoubleStepperViewHolder onTextChanged");
                    try {
                        final double value = Double.valueOf(charSequence.toString());
                        if(validate(value)) {
                            currentDoubleValue = value;
                            updateField(fieldNumber, charSequence.toString());
                        }
                    } catch (final NumberFormatException e) {
                        stepValue.setError("Value must be a multiple of 0.5");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Log.v(TAG, "DoubleStepperViewHolder afterTextChanged");
                }
            });
        }

        void bindData(final int position, final Double currentValue, final String detail) {
            if(currentValue == null) {
                currentDoubleValue = 2.0;
            }
            fieldNumber = position;
            currentDoubleValue = currentValue;
            detailText.setText(detail);
            setValue(currentDoubleValue);
            stepValue.setText(String.format(Locale.ENGLISH, "%.1f", currentValue));
            minus.setOnClickListener(clickListener);
            plus.setOnClickListener(clickListener);
        }

        void incrementValue() {
            double newValue = currentDoubleValue + 0.5;
            if(validate(newValue)) {
                currentDoubleValue = newValue;
                setValue(currentDoubleValue);
            }
        }

        void decrementValue() {
            double newValue = currentDoubleValue - 0.5;
            if (validate(newValue)) {
                currentDoubleValue = newValue;
                setValue(currentDoubleValue);
            }
        }

        private boolean validate(double value) {
            final boolean isMultipleOfHalf = value * 2 % 1 == 0;
            if (!isMultipleOfHalf) {
                stepValue.setError(detailText.getText() + " must be a multiple of 0.5");
            } else if (value < 0.5) {
                stepValue.setError(detailText.getText() + " can't be zero.");
            } else if (value > 20.0) {
                stepValue.setError("20.0 is the maxium " + detailText.getText());
            } else {
                return true;
            }
            return false;
        }

        private void setValue(double value) {
            final String currentValue = String.format(Locale.ENGLISH, "%.1f", value);
            stepValue.setText(currentValue);
            updateField(fieldNumber, currentValue);
        }
    }

    abstract class StepperViewHolder extends RecyclerView.ViewHolder {
        TextView detailText;
        Button minus;
        EditText stepValue;
        Button plus;

        StepperViewHolder(final View v) {
            super(v);
            detailText = v.findViewById(R.id.text_view);
            minus = v.findViewById(R.id.btn_less);
            stepValue = v.findViewById(R.id.current_value);
            plus = v.findViewById(R.id.btn_more);
        }

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view instanceof Button) {
                    final Button button = (Button)view;
                    if (button.getText().equals("+")) {
                        incrementValue();
                    } else if (button.getText().equals("-")) {
                        decrementValue();
                    }
                }
            }
        };

        abstract void incrementValue();
        abstract void decrementValue();
    }
}
