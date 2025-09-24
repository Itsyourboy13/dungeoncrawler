package card.andrew.dungeoncrawler.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import card.andrew.dungeoncrawler.data.model.LeaderboardEntry;

@Dao
public interface LeaderboardDao {

    @Query("SELECT * FROM leaderboard_entries ORDER BY topLevel DESC, enemiesKilled DESC, timestamp DESC")
    LiveData<List<LeaderboardEntry>> getAllEntries();

    @Query("SELECT * FROM leaderboard_entries WHERE local = 1 ORDER BY topLevel DESC, enemiesKilled DESC, timestamp DESC")
    LiveData<List<LeaderboardEntry>> getLocalEntries();

    @Query("SELECT * FROM leaderboard_entries WHERE date(datetime(timestamp / 1000, 'unixepoch')) = date('now') ORDER BY topLevel DESC, enemiesKilled DESC, timestamp DESC")
    LiveData<List<LeaderboardEntry>> getTodayEntries();

    @Query("SELECT * FROM leaderboard_entries WHERE date(datetime(timestamp / 1000, 'unixepoch')) BETWEEN date('now', '-7 days') AND date('now') ORDER BY topLevel DESC, enemiesKilled DESC, timestamp DESC")
    LiveData<List<LeaderboardEntry>> getWeekEntries();

    @Query("SELECT * FROM leaderboard_entries WHERE date(datetime(timestamp / 1000, 'unixepoch')) BETWEEN date('now', '-30 days') AND date('now') ORDER BY topLevel DESC, enemiesKilled DESC, timestamp DESC")
    LiveData<List<LeaderboardEntry>> getMonthEntries();

    @Query("SELECT * FROM leaderboard_entries WHERE date(datetime(timestamp / 1000, 'unixepoch')) BETWEEN date('now', '-365 days') AND date('now') ORDER BY topLevel DESC, enemiesKilled DESC, timestamp DESC")
    LiveData<List<LeaderboardEntry>> getYearEntries();

    @Query("SELECT * FROM leaderboard_entries WHERE playerName LIKE '%' || :playerName || '%' ORDER BY topLevel DESC, enemiesKilled DESC, timestamp DESC")
    LiveData<List<LeaderboardEntry>> getPersonEntries(String playerName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEntry(LeaderboardEntry entry);

    @Query("SELECT COUNT(*) FROM leaderboard_entries")
    int countEntries();
}
