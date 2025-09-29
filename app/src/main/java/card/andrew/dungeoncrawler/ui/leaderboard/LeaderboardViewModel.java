package card.andrew.dungeoncrawler.ui.leaderboard;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel; // Changed from ViewModel to AndroidViewModel
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import card.andrew.dungeoncrawler.data.LeaderboardRepository;
import card.andrew.dungeoncrawler.data.model.LeaderboardEntry;

public class LeaderboardViewModel extends AndroidViewModel { // Changed here

    private final MutableLiveData<List<LeaderboardEntry>> mLeaderboardEntries;
    private final MutableLiveData<String> mFilter;
    private final LeaderboardRepository mRepository;
    private String name;
    private Observer<List<LeaderboardEntry>> currentObserver;

    public LeaderboardViewModel(@NonNull Application application) {
        super(application); // Added super call
        mLeaderboardEntries = new MutableLiveData<>();
        mFilter = new MutableLiveData<>("All");
        mRepository = LeaderboardRepository.getInstance(application.getApplicationContext());
        setFilter("All");
    }

    public LiveData<List<LeaderboardEntry>> getLeaderboardEntries() {
        return mLeaderboardEntries;
    }

    public LiveData<String> getFilter() {
        return mFilter;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilter(String filter) {
        mFilter.setValue(filter);

        if (currentObserver != null) {
            mLeaderboardEntries.removeObserver(currentObserver);
        }

        currentObserver = entries -> {
            Log.d("LeaderboardViewModel", "Observer triggered for filter: " + filter + ", Entries: " + (entries != null ? entries.size() : "null"));
            if (entries != null) {
                for (LeaderboardEntry entry : entries) {
                    Log.d("LeaderboardViewModel", "Entry: " + entry.playerName + ", Level: " + entry.topLevel + ", Enemies Killed: " + entry.enemiesKilled);
                }
                mLeaderboardEntries.setValue(entries);
            } else {
                Log.d("LeaderboardViewModel", "No entries received for filter: " + filter);
                mLeaderboardEntries.setValue(new ArrayList<>());
            }
        };
        switch (filter) {
            case "All":
                mRepository.getAllEntries().observeForever(currentObserver);
                break;
            case "Local":
                mRepository.getLocalEntries().observeForever(currentObserver);
                break;
            case "Today":
                mRepository.getTodayEntries().observeForever(currentObserver);
                break;
            case "This week":
                mRepository.getWeekEntries().observeForever(currentObserver);
                break;
            case "This month":
                mRepository.getMonthEntries().observeForever(currentObserver);
                break;
            case "This year":
                mRepository.getYearEntries().observeForever(currentObserver);
                break;
            case "Name":
                if (name != null) {
                    mRepository.getPersonEntries(name).observeForever(currentObserver);
                } else {
                    Toast.makeText(getApplication(), "No name provided", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (currentObserver != null) {
            mLeaderboardEntries.removeObserver(currentObserver);
        }
    }
}