package card.andrew.dungeoncrawler.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel; // Changed from ViewModel to AndroidViewModel
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import card.andrew.dungeoncrawler.data.LeaderboardRepository;

public class HomeViewModel extends AndroidViewModel { // Changed here

    private final MutableLiveData<Integer> mHighestScore;
    private final MutableLiveData<Integer> mRecentScore;
    private final LeaderboardRepository mRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application); // Added super call
        mHighestScore = new MutableLiveData<>();
        mRecentScore = new MutableLiveData<>();
        mRepository = LeaderboardRepository.getInstance(application.getApplicationContext());
        mRepository.getHighestScore().observeForever(mHighestScore::setValue);
        mRepository.getRecentScore().observeForever(mRecentScore::setValue);
    }

    public MutableLiveData<Integer> getHighestScore() {
        if (mHighestScore.getValue() != null) {
            return mHighestScore;
        }
        MutableLiveData<Integer> none = new MutableLiveData<>();
        none.setValue(0);
        return none;
    }

    public MutableLiveData<Integer> getRecentScore() {
        if (mRecentScore.getValue() != null) {
            return mRecentScore;
        }
        MutableLiveData<Integer> none = new MutableLiveData<>();
        none.setValue(0);
        return none;
    }
}
