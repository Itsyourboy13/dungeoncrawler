package card.andrew.dungeoncrawler.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
// MutableLiveData might still be needed if you intend to allow the ViewModel to directly change these values later,
// but for read-only from repository, LiveData is sufficient for exposure.
// For this refactor, we assume the primary source is the repository.
// import androidx.lifecycle.MutableLiveData; 

import card.andrew.dungeoncrawler.data.LeaderboardRepository;

public class HomeViewModel extends AndroidViewModel {

    private final LiveData<Integer> mHighestScore;
    private final LiveData<Integer> mRecentScore;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        LeaderboardRepository repository = LeaderboardRepository.getInstance(application.getApplicationContext());

        mHighestScore = repository.getHighestScore(); 
        mRecentScore = repository.getRecentScore();
    }

    public LiveData<Integer> getHighestScore() {
        return mHighestScore;
    }

    public LiveData<Integer> getRecentScore() {
        return mRecentScore;
    }
}
