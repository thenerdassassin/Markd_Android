package com.schmidthappens.markd.AdapterClasses;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.customer_subactivities.ProfileEditActivity;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class EditProfileRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "EditProfRecyclerAdapter";

    private static final String[] namePrefixArray = {
            "Mr.",
            "Mrs.",
            "Ms.",
            "Dr.",
            "Rev."
    };
    private static final String[] maritalStatusArray = {
            "Single",
            "Married"
    };
    private static final String[] contractorTypeArray = {
            "Plumber",
            "Hvac",
            "Electrician",
            "Painter"
    };

    private final InputMethodManager IMM;
    private ProfileEditActivity context;
    private TextInputEditText passwordTextField;

    public EditProfileRecyclerViewAdapter(final ProfileEditActivity context) {
        this.context = context;
        IMM = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(viewType, parent, false);
        switch (viewType) {
            case R.layout.view_holder_edit_text:
                return new EditTextViewHolder(view);
            case R.layout.dropdown_menu:
                return new ExposedDropdownMenuViewHolder(view);
            case R.layout.view_holder_button:
                return new ButtonViewHolder(view);
            default:
                throw new IllegalArgumentException(String.format("No view type for %d", viewType));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (position) {
            case 0:
                ((EditTextViewHolder) viewHolder).bindData(
                        position, "Email", context.email);
                break;
            case 1:
                ((EditTextViewHolder) viewHolder).bindData(
                        position, "Password", null);
                break;
            case 2:
                ((ExposedDropdownMenuViewHolder) viewHolder).bindData(
                        position, "Title", context.namePrefix, namePrefixArray);
                break;
            case 3:
                ((EditTextViewHolder) viewHolder).bindData(
                        position, "First Name", context.firstName);
                break;
            case 4:
                ((EditTextViewHolder) viewHolder).bindData(
                        position, "Last Name", context.lastName);
                break;
            case 5:
                if(context.userType.equalsIgnoreCase("Home Owner")
                        || context.userType.equalsIgnoreCase("customer")) {
                    ((ExposedDropdownMenuViewHolder) viewHolder).bindData(
                            position, "Marital Status", context.maritalStatus, maritalStatusArray);
                } else {
                    ((ExposedDropdownMenuViewHolder) viewHolder).bindData(
                            position, "Contractor Type", context.contractorType, contractorTypeArray);
                }
                break;
            case 6:
                ((ButtonViewHolder) viewHolder).bindData("Save");
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
        //Name Prefix, and either MaritalStatus or ContractorType
        if (position == 2 || position == 5) {
            return R.layout.dropdown_menu;
        } else if (position == 6) {
            return R.layout.view_holder_button;
        } else {
            return R.layout.view_holder_edit_text;
        }
    }

    private void updateField(int fieldNumber, String newValue) {
        switch (fieldNumber) {
            case 0:
                context.email = newValue;
                break;
            case 1:
                break;
            case 2:
                context.namePrefix = newValue;
                break;
            case 3:
                context.firstName = newValue;
                break;
            case 4:
                context.lastName = newValue;
                break;
            case 5:
                if(context.userType.equalsIgnoreCase("Home Owner")
                        || context.userType.equalsIgnoreCase("customer")) {
                    context.maritalStatus = newValue;
                } else {
                    context.contractorType = newValue;
                }
                break;
            default:
                Log.e(TAG, String.format("No field for fieldNumber %d", fieldNumber));
        }
    }

    private void updateUser() {
        final String password = passwordTextField.getText().toString();
        if (password.isEmpty()) {
            passwordTextField.setError("Must enter current password.");
        } else {
            context.updateUser(password);
        }
    }

    private void hideKeyboard(View view) {
        IMM.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    class EditTextViewHolder extends RecyclerView.ViewHolder {
        private TextInputLayout inputLayout;
        private TextInputEditText textView;
        private int fieldNumber;

        EditTextViewHolder(final View v) {
            super(v);
            inputLayout = v.findViewById(R.id.input_layout);
            textView = v.findViewById(R.id.edit_text);
            textView.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        /**
         *
         * @param position
         * @param hint
         * @param currentText - Set to null if you want password type
         */
        void bindData(
                final int position,
                final String hint,
                final String currentText) {
            if(currentText == null) {
                textView.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                textView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordTextField = textView;
            }
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
    class ExposedDropdownMenuViewHolder extends RecyclerView.ViewHolder {
        private TextInputLayout inputLayout;
        private AutoCompleteTextView dropdown;
        private ArrayAdapter<String> adapter;
        private int fieldNumber;

        ExposedDropdownMenuViewHolder(final View v) {
            super(v);
            inputLayout = v.findViewById(R.id.dropdown_menu);
            dropdown = v.findViewById(R.id.dropdown);
            dropdown.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View focusedView, boolean hasFocus) {
                    if(hasFocus) {
                        hideKeyboard(focusedView);
                        inputLayout.performClick();
                    }
                }
            });
        }

        void bindData(
                final int position,
                final String hint,
                final String currentText,
                final String[] adapterArray) {
            fieldNumber = position;
            inputLayout.setHint(hint);
            setUpAdapter(adapterArray);

            dropdown.setText(currentText, false);
            dropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "Item Clicked: " + adapter.getItem(position));
                    updateField(fieldNumber, adapter.getItem(position));
                }
            });
        }

        private void setUpAdapter(final String[] adapterArray) {
            adapter = new ArrayAdapter<>(
                    context,
                    R.layout.dropdown_menu_popup_item,
                    adapterArray);
            dropdown.setAdapter(adapter);
        }
    }
    class ButtonViewHolder extends RecyclerView.ViewHolder {
        private Button button;

        ButtonViewHolder(final View v) {
            super(v);
            button = v.findViewById(R.id.button);
        }

        void bindData(final String buttonText) {
            button.setText(buttonText);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateUser();
                }
            });
        }
    }
}
