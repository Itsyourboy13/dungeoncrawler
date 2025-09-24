package card.andrew.dungeoncrawler.data;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import card.andrew.dungeoncrawler.data.model.LeaderboardEntry;

public class LeaderboardRepository {

    private static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(4);
    private static LeaderboardRepository instance;
    private final LeaderboardDao leaderboardDao;

    public static LeaderboardRepository getInstance(Context context) {
        if (instance == null) {
            instance = new LeaderboardRepository(context);
            //if database is empty
            databaseExecutor.execute(() -> {
                if (instance.leaderboardDao.countEntries() == 0) addStarterData();
            });
        }
        return instance;
    }

    private static void addStarterData() {
        LeaderboardEntry entry1 = new LeaderboardEntry("Godly Circle", 100, 3, false);
        LeaderboardEntry entry2 = new LeaderboardEntry("Devil Circle", 66, 6, false);
        LeaderboardEntry entry3 = new LeaderboardEntry("Holy Circle", 10, 2, false);
        LeaderboardEntry entry4 = new LeaderboardEntry("Angel Circle", 5, 3, false);
        LeaderboardEntry entry5 = new LeaderboardEntry("Normal Circle", 3, 4, false);
        LeaderboardEntry entry6 = new LeaderboardEntry("Oval", 2, 2, false);
        LeaderboardEntry entry7 = new LeaderboardEntry("Rectangle", 2, 0, false);
        LeaderboardEntry entry8 = new LeaderboardEntry("Trapezoid", 1, 3, false);
        LeaderboardEntry entry9 = new LeaderboardEntry("Square", 1, 2, false);
        LeaderboardEntry entry10 = new LeaderboardEntry("Triangle", 1, 1, false);

        LeaderboardEntry[] entries = {entry1, entry2, entry3, entry4, entry5, entry6, entry7, entry8, entry9, entry10};

        for (LeaderboardEntry entry : entries) {
            instance.insertEntry(entry);
        }

    }

    private LeaderboardRepository(Context context) {
        LeaderboardDatabase database = Room.databaseBuilder(context,
                LeaderboardDatabase.class, "leaderboard_database").build();
        leaderboardDao = database.leaderboardDao();
    }

    public LiveData<List<LeaderboardEntry>> getAllEntries() {
        return leaderboardDao.getAllEntries();
    }

    public LiveData<List<LeaderboardEntry>> getLocalEntries() {
        return leaderboardDao.getLocalEntries();
    }

    public LiveData<List<LeaderboardEntry>> getTodayEntries() {
        return leaderboardDao.getTodayEntries();
    }

    public LiveData<List<LeaderboardEntry>> getWeekEntries() {
        return leaderboardDao.getWeekEntries();
    }

    public LiveData<List<LeaderboardEntry>> getMonthEntries() {
        return leaderboardDao.getMonthEntries();
    }

    public LiveData<List<LeaderboardEntry>> getYearEntries() {
        return leaderboardDao.getYearEntries();
    }

    public LiveData<List<LeaderboardEntry>> getPersonEntries(String playerName) {
        return leaderboardDao.getPersonEntries(playerName);
    }

    public void insertEntry(LeaderboardEntry entry) {
        databaseExecutor.execute(() -> {
            leaderboardDao.insertEntry(entry);
        });
    }
}
