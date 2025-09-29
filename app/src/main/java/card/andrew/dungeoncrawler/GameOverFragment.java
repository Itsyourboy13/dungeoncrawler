package card.andrew.dungeoncrawler;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class GameOverFragment extends DialogFragment {

    public interface GameOverListener {
        void onLeaderboardEntryEntered(String name);
    }

    private GameOverListener listener;
    private EditText editText;
    private AlertDialog dialog; // Store dialog to access button later

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false); // Prevent dismissal by back button or touch outside (for the fragment itself)
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        editText = new EditText(requireActivity()); // Assign to field
        editText.setHint(R.string.enter_name);
        editText.setSingleLine();
        editText.setSelectAllOnFocus(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.game_over);
        builder.setView(editText);
        builder.setPositiveButton(R.string.submit_score, (dialogInterface, which) -> {
            // The button click will only happen if it's enabled
            String name = editText.getText().toString();
            if (listener != null) {
                listener.onLeaderboardEntryEntered(name);
            }
        });

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); // Specifically prevent dialog dismissal on touch outside
        // Note: setCancelable(false) on the DialogFragment (in onCreate) also handles this, 
        // but being explicit on the dialog itself is also fine.
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positiveButton != null) {
            positiveButton.setEnabled(false); // Disable initially

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Not needed
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Not needed
                }

                @Override
                public void afterTextChanged(Editable s) {
                    positiveButton.setEnabled(s.toString().trim().length() > 0);
                }
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GameOverListener) {
            listener = (GameOverListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GameOverListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        dialog = null; 
        editText = null;
    }
}
