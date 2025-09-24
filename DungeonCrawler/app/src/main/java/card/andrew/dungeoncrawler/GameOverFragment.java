package card.andrew.dungeoncrawler;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        EditText editText = new EditText(requireActivity());
        editText.setHint(R.string.enter_name);
        editText.setSingleLine();
        editText.setSelectAllOnFocus(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.game_over);
        builder.setView(editText);
        builder.setPositiveButton(R.string.submit_score, (dialog, which) -> {
            String name = editText.getText().toString();
            if (listener != null) {
                listener.onLeaderboardEntryEntered(name);
            }
        });
        return builder.create();
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
    }
}
