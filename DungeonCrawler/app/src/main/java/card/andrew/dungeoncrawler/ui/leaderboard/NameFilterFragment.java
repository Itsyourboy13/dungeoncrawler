package card.andrew.dungeoncrawler.ui.leaderboard;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment; // Added for getParentFragment

import card.andrew.dungeoncrawler.R;

public class NameFilterFragment extends DialogFragment {
    public interface NameFilterListener {
        void onNameFilterSelected(String name);
    }
    private NameFilterListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        EditText editText = new EditText(requireActivity());
        editText.setHint(R.string.enter_name);
        editText.setSingleLine();
        editText.setSelectAllOnFocus(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.filter_by_name);
        builder.setView(editText);
        builder.setPositiveButton(R.string.apply_filter, (dialog, which) -> {
            String name = editText.getText().toString();
            if (listener != null) {
                listener.onNameFilterSelected(name);
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof NameFilterListener) {
            listener = (NameFilterListener) parentFragment;
        } else if (context instanceof NameFilterListener) {
            listener = (NameFilterListener) context;
        } else {
            throw new RuntimeException((parentFragment != null ? parentFragment.toString() : context.toString())
                    + " must implement NameFilterListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
