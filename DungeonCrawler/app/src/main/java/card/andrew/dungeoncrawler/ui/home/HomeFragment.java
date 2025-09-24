package card.andrew.dungeoncrawler.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import card.andrew.dungeoncrawler.MainActivity;
import card.andrew.dungeoncrawler.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button startButton = binding.startButton;
        TextView highestScoreText = binding.highestScoreText;
        TextView recentScoreText = binding.recentScoreText;

        // Observe score data from ViewModel
        homeViewModel.getHighestScore().observe(getViewLifecycleOwner(), highestScoreText::setText);
        homeViewModel.getRecentScore().observe(getViewLifecycleOwner(), recentScoreText::setText);

        // Set start button listener
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}