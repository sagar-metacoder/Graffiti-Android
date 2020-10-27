package com.app.graffiti.utils;


import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * {@link Validations} : <p>
 * <p>
 * </p>
 *
 * @author Jeel Vankhede
 * @version 1.0.0
 * @since 4/1/18
 */

public class Validations {
    public static final String TAG = Validations.class.getSimpleName();

    public static boolean isEmpty(@NonNull View view, final CharSequence errorText) {
        if (view instanceof TextInputLayout) {
            final TextInputLayout parentInputLayout = (TextInputLayout) view;
            for (int index = 0; index < parentInputLayout.getChildCount(); index++) {
                if (parentInputLayout.getChildAt(index) instanceof FrameLayout) {
                    FrameLayout childFrameLayout = (FrameLayout) parentInputLayout.getChildAt(index);
                    for (int childIndex = 0; childIndex < childFrameLayout.getChildCount(); childIndex++) {
                        if (childFrameLayout.getChildAt(childIndex) instanceof EditText) {
                            EditText editText = (EditText) childFrameLayout.getChildAt(childIndex);
                            if (TextUtils.isEmpty(editText.getText().toString())) {
                                parentInputLayout.setError(errorText);
                                editText.requestFocus();
                                editText.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                        if (editable.length() == 0) {
                                            parentInputLayout.setError(errorText);
                                        } else {
                                            parentInputLayout.setError(null);
                                        }
                                    }
                                });
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                }
            }
        } else if (view instanceof EditText) {
            final EditText editText = (EditText) view;
            if (TextUtils.isEmpty(editText.getText().toString())) {
                editText.setError(errorText);
                editText.requestFocus();
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() == 0) {
                            editText.setError(errorText);
                        } else {
                            editText.setError(null);
                        }
                    }
                });
                return true;
            } else {
                return false;
            }
        } else {
            Log.wtf(TAG, " isEmpty : view is not wrapper of EditText or TextInputLayout Type ");
            return true;
        }
        return true;
    }

    public static boolean isValidEmail(@NonNull View view, final CharSequence errorText) {
        if (view instanceof TextInputLayout) {
            final TextInputLayout parentInputLayout = (TextInputLayout) view;
            for (int index = 0; index < parentInputLayout.getChildCount(); index++) {
                if (parentInputLayout.getChildAt(index) instanceof FrameLayout) {
                    FrameLayout childFrameLayout = (FrameLayout) parentInputLayout.getChildAt(index);
                    for (int childIndex = 0; childIndex < childFrameLayout.getChildCount(); childIndex++) {
                        if (childFrameLayout.getChildAt(childIndex) instanceof EditText) {
                            EditText editText = (EditText) childFrameLayout.getChildAt(childIndex);
                            if (!Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches()) {
                                parentInputLayout.setError(errorText);
                                editText.requestFocus();
                                editText.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                        if (editable.length() == 0) {
                                            parentInputLayout.setError(errorText);
                                        } else {
                                            parentInputLayout.setError(null);
                                        }
                                    }
                                });
                                return false;
                            } else {
                                return true;
                            }
                        }
                    }
                }
            }
        } else if (view instanceof EditText) {
            final EditText editText = (EditText) view;
            if (!Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches()) {
                editText.setError(errorText);
                editText.requestFocus();
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() == 0) {
                            editText.setError(errorText);
                        } else {
                            editText.setError(null);
                        }
                    }
                });
                return false;
            } else {
                return true;
            }
        } else {
            Log.wtf(TAG, " isValidEmail : view is not wrapper of EditText or TextInputLayout Type ");
            return false;
        }
        return false;
    }

    public static boolean isValidPhone(@NonNull View view, final CharSequence errorText, int length) {
        final int numberLength;
        if (length == -1) {
            numberLength = 20;
        } else {
            numberLength = length;
        }
        if (view instanceof TextInputLayout) {
            final TextInputLayout parentInputLayout = (TextInputLayout) view;
            for (int index = 0; index < parentInputLayout.getChildCount(); index++) {
                if (parentInputLayout.getChildAt(index) instanceof FrameLayout) {
                    FrameLayout childFrameLayout = (FrameLayout) parentInputLayout.getChildAt(index);
                    for (int childIndex = 0; childIndex < childFrameLayout.getChildCount(); childIndex++) {
                        if (childFrameLayout.getChildAt(childIndex) instanceof EditText) {
                            final EditText editText = (EditText) childFrameLayout.getChildAt(childIndex);
                            if (numberLength != editText.getText().length()
                                    && !Patterns.PHONE.matcher(editText.getText().toString()).matches()) {
                                parentInputLayout.setError(errorText);
                                editText.requestFocus();
                                editText.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                        if (editable.length() == 0) {
                                            parentInputLayout.setError(errorText);
                                        } else {
                                            if (numberLength != editable.toString().length()
                                                    && !Patterns.PHONE.matcher(editable.toString()).matches()) {
                                                parentInputLayout.setError(errorText);
                                            } else {
                                                parentInputLayout.setError(null);
                                            }
                                        }
                                    }
                                });
                                return false;
                            } else {
                                return true;
                            }
                        }
                    }
                }
            }
        } else if (view instanceof EditText) {
            final EditText editText = (EditText) view;
            if (numberLength != editText.getText().length()
                    && !Patterns.PHONE.matcher(editText.getText().toString()).matches()) {
                editText.setError(errorText);
                editText.requestFocus();
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() == 0) {
                            editText.setError(errorText);
                        } else {
                            if (numberLength != editable.toString().length()
                                    && !Patterns.PHONE.matcher(editable.toString()).matches()) {
                                editText.setError(errorText);
                            } else {
                                editText.setError(null);
                            }
                        }
                    }
                });
                return false;
            } else {
                return true;
            }
        } else {
            Log.wtf(TAG, " isValidPhone : view is not wrapper of EditText or TextInputLayout Type ");
            return false;
        }
        return false;
    }

    public static boolean isValidLength(@NonNull View view, final CharSequence errorText, int length) {
        final int numberLength;
        if (length == -1) {
            numberLength = 20;
        } else {
            numberLength = length;
        }
        if (view instanceof TextInputLayout) {
            final TextInputLayout parentInputLayout = (TextInputLayout) view;
            for (int index = 0; index < parentInputLayout.getChildCount(); index++) {
                if (parentInputLayout.getChildAt(index) instanceof FrameLayout) {
                    FrameLayout childFrameLayout = (FrameLayout) parentInputLayout.getChildAt(index);
                    for (int childIndex = 0; childIndex < childFrameLayout.getChildCount(); childIndex++) {
                        if (childFrameLayout.getChildAt(childIndex) instanceof EditText) {
                            final EditText editText = (EditText) childFrameLayout.getChildAt(childIndex);
                            if (numberLength != editText.getText().length()) {
                                parentInputLayout.setError(errorText);
                                editText.requestFocus();
                                editText.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                        if (editable.length() == 0) {
                                            parentInputLayout.setError(errorText);
                                        } else {
                                            if (numberLength != editable.toString().length()) {
                                                parentInputLayout.setError(errorText);
                                            } else {
                                                parentInputLayout.setError(null);
                                            }
                                        }
                                    }
                                });
                                return false;
                            } else {
                                return true;
                            }
                        }
                    }
                }
            }
        } else if (view instanceof EditText) {
            final EditText editText = (EditText) view;
            if (numberLength != editText.getText().length()) {
                editText.setError(errorText);
                editText.requestFocus();
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() == 0) {
                            editText.setError(errorText);
                        } else {
                            if (numberLength != editable.toString().length()) {
                                editText.setError(errorText);
                            } else {
                                editText.setError(null);
                            }
                        }
                    }
                });
                return false;
            } else {
                return true;
            }
        } else {
            Log.wtf(TAG, " isValidLength : view is not wrapper of EditText or TextInputLayout Type ");
            return false;
        }
        return false;
    }

    public static boolean isValidLimitLow(@NonNull View view, final CharSequence errorText, @NonNull final int limit) {
        if (view instanceof TextInputLayout) {
            final TextInputLayout parentInputLayout = (TextInputLayout) view;
            for (int index = 0; index < parentInputLayout.getChildCount(); index++) {
                if (parentInputLayout.getChildAt(index) instanceof FrameLayout) {
                    FrameLayout childFrameLayout = (FrameLayout) parentInputLayout.getChildAt(index);
                    for (int childIndex = 0; childIndex < childFrameLayout.getChildCount(); childIndex++) {
                        if (childFrameLayout.getChildAt(childIndex) instanceof EditText) {
                            final EditText editText = (EditText) childFrameLayout.getChildAt(childIndex);
                            int inputNumber = 0;
                            try {
                                inputNumber = Integer.parseInt(editText.getText().toString().trim());
                            } catch (NumberFormatException e) {
                                Log.wtf(TAG, " isValidLimitLow :(Handle editText to inputType:\"number\" to avoid this silly exception): NumberFormatException ", e);
                                inputNumber = limit - 1;
                                return false;
                            } finally {
                                if (inputNumber < limit) {
                                    parentInputLayout.setError(errorText);
                                    editText.requestFocus();
                                    editText.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {
                                            try {
                                                int number = Integer.parseInt(editable.toString().trim());
                                                if (number < limit) {
                                                    parentInputLayout.setError(errorText);
                                                } else {
                                                    parentInputLayout.setError(null);
                                                }
                                            } catch (NumberFormatException e) {
                                                Log.wtf(TAG, " afterTextChanged : (Handle editText to inputType:\"number\" to avoid this silly exception): NumberFormatException ", e);
                                            }
                                        }
                                    });
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        } else if (view instanceof EditText) {
            final EditText editText = (EditText) view;
            int inputNumber = 0;
            try {
                inputNumber = Integer.parseInt(editText.getText().toString().trim());
            } catch (NumberFormatException e) {
                Log.wtf(TAG, " isValidLimitLow :(Handle editText to inputType:\"number\" to avoid this silly exception): NumberFormatException ", e);
                inputNumber = limit - 1;
                return false;
            } finally {
                if (inputNumber < limit) {
                    editText.setError(errorText);
                    editText.requestFocus();
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            try {
                                int number = Integer.parseInt(editable.toString().trim());
                                if (number < limit) {
                                    editText.setError(errorText);
                                } else {
                                    editText.setError(null);
                                }
                            } catch (NumberFormatException e) {
                                Log.wtf(TAG, " afterTextChanged : (Handle editText to inputType:\"number\" to avoid this silly exception): NumberFormatException ", e);
                            }
                        }
                    });
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            Log.wtf(TAG, " isValidLimitLow : view is not wrapper of EditText or TextInputLayout Type ");
            return false;
        }
        return false;
    }
}
