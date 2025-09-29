package card.andrew.dungeoncrawler;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class LevelFinishedFragment extends DialogFragment {

    private static final String ARG_COMPLETED_LEVEL = "completed_level";
    private static final String ARG_PLAYER_LEVEL = "player_level";

    public interface OnLevelFinishedListener {
        void onLevelFinished();
    }

    private OnLevelFinishedListener listener;

    public static LevelFinishedFragment newInstance(int completedLevel, int playerLevel) {
        LevelFinishedFragment fragment = new LevelFinishedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COMPLETED_LEVEL, completedLevel);
        args.putInt(ARG_PLAYER_LEVEL, playerLevel);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Create a LinearLayout to hold the TextView and center it
        LinearLayout layout = new LinearLayout(requireActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER); // Center the content horizontally
        layout.setPadding(16, 16, 16, 16); // Add some padding

        final TextView textView = new TextView(requireActivity());
        textView.setGravity(Gravity.CENTER); // Center the text within the TextView
        textView.setTextSize(18); // Optional: Set a readable size

        if (getArguments() != null) {
            int completedLevel = getArguments().getInt(ARG_COMPLETED_LEVEL);
            int playerLevel = getArguments().getInt(ARG_PLAYER_LEVEL);
            String message = getString(R.string.level_completed) + " " + completedLevel + "!" + "\n" +
                    getString(R.string.you_reached_level) + " " + playerLevel + "!";
            textView.setText(message);
        } else {
            textView.setText(R.string.you_won_level);
        }

        layout.addView(textView);

        return new AlertDialog.Builder(requireActivity()).setTitle(R.string.you_won)
                .setView(layout)
                .setPositiveButton(R.string.next_level, (dialog, which) -> {
                    if (listener != null) {
                        listener.onLevelFinished();
                    }
                })
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnLevelFinishedListener) {
            listener = (OnLevelFinishedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
