package card.andrew.dungeoncrawler.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import card.andrew.dungeoncrawler.data.model.LeaderboardEntry;

@Database(entities = {LeaderboardEntry.class}, version = 1, exportSchema = false)
public abstract class LeaderboardDatabase extends RoomDatabase {
    public abstract LeaderboardDao leaderboardDao();
}
