package card.andrew.dungeoncrawler.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mHighestScore;
    private final MutableLiveData<String> mRecentScore;

    public HomeViewModel() {
        mHighestScore = new MutableLiveData<>();
        mRecentScore = new MutableLiveData<>();

        mHighestScore.setValue("Highest Score: 100");
        mRecentScore.setValue("Recent Score: 75");
    }

    public MutableLiveData<String> getHighestScore() {
        return mHighestScore;
    }

    public MutableLiveData<String> getRecentScore() {
        return mRecentScore;
    }

    public void setHighestScore(int highestScore) {
        mHighestScore.setValue("Highest Score: " + highestScore);
    }

    public void setRecentScore(int recentScore) {
        mRecentScore.setValue("Recent Score: " + recentScore);
    }
}