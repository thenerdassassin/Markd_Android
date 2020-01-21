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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.customer_subactivities.ProfileEditActivity;
import com.schmidthappens.markd.utilities.StringUtilities;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class CreateProfileRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CreateProfileRecyclrAd";

    private static final String NOT_SPECIFIED = "Prefer Not To Say";
    private static final String NOT_SPECIFIED_VALUE = "Single";
    private static final String[] namePrefixArray = {
            "Mr.",
            "Mrs.",
            "Ms.",
            "Dr.",
            "Rev."
    };
    private static final String[] maritalStatusArray = {
            "Single",
            "Married",
            NOT_SPECIFIED
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
    private TextInputEditText confirmPasswordTextField;

    public CreateProfileRecyclerViewAdapter(final ProfileEditActivity context) {
        Log.d(TAG, "Creating Account");
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
                return new CreateProfileRecyclerViewAdapter.EditTextViewHolder(view);
            case R.layout.dropdown_menu:
                return new CreateProfileRecyclerViewAdapter.ExposedDropdownMenuViewHolder(view);
            case R.layout.view_holder_button:
                return new CreateProfileRecyclerViewAdapter.ButtonViewHolder(view);
            default:
                throw new IllegalArgumentException(String.format("No view type for %d", viewType));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (position) {
            case 0:
                String currentEmail = "";
                if(context.email != null) {
                    currentEmail = context.email;
                }
                ((CreateProfileRecyclerViewAdapter.EditTextViewHolder) viewHolder).bindData(
                        position, "Email", currentEmail);
                break;
            case 1:
                ((CreateProfileRecyclerViewAdapter.EditTextViewHolder) viewHolder).bindData(
                        position, "Password", null);
                break;
            case 2:
                ((CreateProfileRecyclerViewAdapter.EditTextViewHolder) viewHolder).bindData(
                        position, "Confirm Password", null);
                break;
            case 3:
                ((CreateProfileRecyclerViewAdapter.ExposedDropdownMenuViewHolder) viewHolder).bindData(
                        position, "Title", context.namePrefix, namePrefixArray);
                break;
            case 4:
                String firstName = "";
                if(context.firstName != null) {
                    firstName = context.firstName;
                }
                ((CreateProfileRecyclerViewAdapter.EditTextViewHolder) viewHolder).bindData(
                        position, "First Name", firstName);
                break;
            case 5:
                String lastName = "";
                if(context.lastName != null) {
                    lastName = context.lastName;
                }
                ((CreateProfileRecyclerViewAdapter.EditTextViewHolder) viewHolder).bindData(
                        position, "Last Name", lastName);
                break;
            case 6:
                if(context.userType.equalsIgnoreCase("Home Owner")
                        || context.userType.equalsIgnoreCase("customer")) {
                    ((CreateProfileRecyclerViewAdapter.ExposedDropdownMenuViewHolder) viewHolder).bindData(
                            position, "Marital Status", context.maritalStatus, maritalStatusArray);
                } else {
                    ((CreateProfileRecyclerViewAdapter.ExposedDropdownMenuViewHolder) viewHolder).bindData(
                            position, "Contractor Type", context.contractorType, contractorTypeArray);
                }
                break;
            case 7:
                ((CreateProfileRecyclerViewAdapter.ButtonViewHolder) viewHolder).bindData("Next");
                break;
            default:
                throw new IllegalArgumentException(String.format("No view holder for %d", position));
        }
    }

    @Override
    public int getItemCount() {
        return 8;
    }
    @Override
    public int getItemViewType(int position) {
        //Name Prefix, and either MaritalStatus or ContractorType
        if (position == 3 || position == 6) {
            return R.layout.dropdown_menu;
        } else if (position == 7) {
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
            case 2:
                break;
            case 3:
                context.namePrefix = newValue;
                break;
            case 4:
                context.firstName = newValue;
                break;
            case 5:
                context.lastName = newValue;
                break;
            case 6:
                if(isCustomerAccount()) {
                    if (NOT_SPECIFIED.equalsIgnoreCase(newValue)) {
                        newValue = NOT_SPECIFIED_VALUE;
                    }
                    context.maritalStatus = newValue;
                } else {
                    context.contractorType = newValue;
                }
                break;
            default:
                Log.e(TAG, String.format("No field for fieldNumber %d", fieldNumber));
        }
    }

    private boolean isCustomerAccount() {
        return context.userType.equalsIgnoreCase("Home Owner")
                || context.userType.equalsIgnoreCase("customer");
    }

    private void createUser() {
        if(isValidUser()) {
            final String password = passwordTextField.getText().toString();
            final String confirmPassword = confirmPasswordTextField.getText().toString();
            if (password.isEmpty()) {
                passwordTextField.setError("Must enter current password.");
            } else if (confirmPassword.isEmpty()) {
                confirmPasswordTextField.setError("Must confirm password.");
            } else if (!password.equals(confirmPassword)) {
                confirmPasswordTextField.setError("Passwords do not match.");
            } else {
                context.createUser(password);
            }
        }
    }

    private boolean isValidUser() {
        if(isCustomerAccount()) {
            if(StringUtilities.isNullOrEmpty(context.maritalStatus)) {
                context.maritalStatus = NOT_SPECIFIED_VALUE;
            }
            return isValidEmail(context.email)
                    && isValidName(context.namePrefix, context.firstName, context.lastName);
        } else {
            return isValidEmail(context.email)
                    && isValidName(context.namePrefix, context.firstName, context.lastName)
                    && isValidContractorType(context.contractorType);
        }
    }

    private boolean isValidEmail(final String email) {
        if(StringUtilities.isNullOrEmpty(email)) {
            Toast.makeText(context, "Invalid Email Address", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            int atPosition = email.indexOf("@");
            int dotPosition = email.lastIndexOf(".");
            if (atPosition < 2 || dotPosition < 3 || dotPosition - atPosition < 2) {
                Toast.makeText(context, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
    private boolean isValidName(final String title, final String firstName, final String lastName) {
        if(StringUtilities.isNullOrEmpty(title)) {
            Toast.makeText(context, "Must enter title.", Toast.LENGTH_SHORT).show();
            return false;
        } else if(StringUtilities.isNullOrEmpty(firstName)) {
            Toast.makeText(context, "Must enter first name.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (StringUtilities.isNullOrEmpty(lastName)) {
            Toast.makeText(context, "Must enter last name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean isValidContractorType(final String contractorType) {
        if (StringUtilities.isNullOrEmpty(contractorType)) {
            Toast.makeText(context, "Must specify Contractor Type.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
                if (position == 1) {
                    passwordTextField = textView;
                } else if (position == 2) {
                    confirmPasswordTextField = textView;
                }
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
                    createUser();
                }
            });
        }
    }
}
