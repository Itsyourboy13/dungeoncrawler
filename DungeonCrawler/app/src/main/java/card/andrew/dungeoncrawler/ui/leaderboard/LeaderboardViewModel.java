package card.andrew.dungeoncrawler.ui.leaderboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardViewModel extends ViewModel {

    private final MutableLiveData<List<String>> mLeaderboardEntries;
    private final MutableLiveData<String> mFilter;

    public LeaderboardViewModel() {
        mLeaderboardEntries = new MutableLiveData<>();
        mFilter = new MutableLiveData<>("All");

        // TODO--- Fetch and update leaderboard entries
        // TODO--- PLACEHOLDER
        loadPlaceholderData();
    }

    private void loadPlaceholderData() {
        // TODO--- PLACEHOLDER DELETE
        List<String> entries = new ArrayList<>();
        entries.add("Player 1: 100");
        entries.add("Player 2: 90");
        entries.add("Player 3: 80");
        entries.add("Player 4: 70");
        mLeaderboardEntries.setValue(entries);
    }

    public LiveData<List<String>> getLeaderboardEntries() {
        return mLeaderboardEntries;
    }

    public LiveData<String> getFilter() {
        return mFilter;
    }

    public void setFilter(String filter) {
        mFilter.setValue(filter);
        // TODO--- Filter leaderboard entries based on the selected filter
        // TODO--- PLACEHOLDER
        loadPlaceholderData();
    }
}