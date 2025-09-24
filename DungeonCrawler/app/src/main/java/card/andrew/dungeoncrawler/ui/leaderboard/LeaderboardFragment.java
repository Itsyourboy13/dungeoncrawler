package card.andrew.dungeoncrawler.ui.leaderboard;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import card.andrew.dungeoncrawler.R;

public class LeaderboardFragment extends Fragment
        implements NameFilterFragment.NameFilterListener{

    private LeaderboardViewModel mViewModel;
    private RecyclerView leaderboardRecycler;
    private Spinner filterSpinner;
    private LeaderboardAdapter adapter;
    private TextView title;

    public static LeaderboardFragment newInstance() {
        return new LeaderboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        leaderboardRecycler = view.findViewById(R.id.leaderboard_recycler);
        filterSpinner = view.findViewById(R.id.filter_spinner);
        Button applyFilterButton = view.findViewById(R.id.apply_filter_button);
        title = view.findViewById(R.id.leaderboard_title);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);

        // Set up RecyclerView
        leaderboardRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new LeaderboardAdapter(new ArrayList<>());
        adapter.setShowHeader(true);
        leaderboardRecycler.setAdapter(adapter);

        // Set up the filter spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.filter_options,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        // Observe leaderboard entries and update the adapter
        mViewModel.getLeaderboardEntries().observe(getViewLifecycleOwner(), entries -> {
            if (adapter != null) {
                adapter = new LeaderboardAdapter(entries);
                adapter.setShowHeader(true);
                leaderboardRecycler.setAdapter(adapter);
            }
        });

        // Observe filter and set initial value
        mViewModel.getFilter().observe(getViewLifecycleOwner(), filter -> {
            int position = spinnerAdapter.getPosition(filter);
            if (position >= 0) {
                filterSpinner.setSelection(position);
            }
            switch (filter) {
                case "Name":
                    title.setText(R.string.leaderboard_title_name);
                    break;
                case "All":
                    title.setText(R.string.leaderboard_title_all);
                    break;
                case "Today":
                    title.setText(R.string.leaderboard_title_today);
                    break;
                case "This week":
                    title.setText(R.string.leaderboard_title_week);
                    break;
                case "This month":
                    title.setText(R.string.leaderboard_title_month);
                    break;
                case "This year":
                    title.setText(R.string.leaderboard_title_year);
                    break;
            }
        });

        // Apply filter button listener
        Button applyFilterButton = view.findViewById(R.id.apply_filter_button);
        applyFilterButton.setOnClickListener(v -> {
            String selectedFilter = filterSpinner.getSelectedItem().toString();
            if (selectedFilter.equals("Name")){
                NameFilterFragment dialog = new NameFilterFragment();
                dialog.show(getChildFragmentManager(), "name_filter");
            } else {
                mViewModel.setFilter(selectedFilter);
            }
        });
    }

    @Override
    public void onNameFilterSelected(String name) {
        mViewModel.setName(name);
        mViewModel.setFilter("Name");
    }
}