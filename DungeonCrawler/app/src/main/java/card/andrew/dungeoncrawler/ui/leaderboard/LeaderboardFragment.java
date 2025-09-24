package card.andrew.dungeoncrawler.ui.leaderboard;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import card.andrew.dungeoncrawler.R;

public class LeaderboardFragment extends Fragment {

    private LeaderboardViewModel mViewModel;
    private ListView leaderboardList;
    private Spinner filterSpinner;

    public static LeaderboardFragment newInstance() {
        return new LeaderboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        leaderboardList = view.findViewById(R.id.leaderboard_list);
        filterSpinner = view.findViewById(R.id.filter_spinner);
        Button applyFilterButton = view.findViewById(R.id.apply_filter_button);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);

        // Set up the filter spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.filter_options,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        // Set up ListView adapter
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                new ArrayList<>()
        );
        leaderboardList.setAdapter(listAdapter);

        // Observe leaderboard entries and update the ListView
        mViewModel.getLeaderboardEntries().observe(getViewLifecycleOwner(), entries -> {
            listAdapter.clear();
            if (entries != null) {
                listAdapter.addAll(entries);
            }
            listAdapter.notifyDataSetChanged();
        });

        // Observe filter and set initial value
        mViewModel.getFilter().observe(getViewLifecycleOwner(), filter -> {
            int position = spinnerAdapter.getPosition(filter);
            if (position >= 0) {
                filterSpinner.setSelection(position);
            }
            Toast.makeText(requireContext(), "Filter: " + filter, Toast.LENGTH_SHORT).show();
        });

        // Apply filter button listener
        Button applyFilterButton = view.findViewById(R.id.apply_filter_button);
        applyFilterButton.setOnClickListener(v -> {
            String selectedFilter = filterSpinner.getSelectedItem().toString();
            mViewModel.setFilter(selectedFilter);
        });
    }

}