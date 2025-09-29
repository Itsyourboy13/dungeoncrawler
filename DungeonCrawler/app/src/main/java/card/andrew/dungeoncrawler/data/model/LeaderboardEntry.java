package card.andrew.dungeoncrawler.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "leaderboard_entries")
public class LeaderboardEntry {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String playerName;
    public int topLevel;
    public int enemiesKilled;
    public boolean local;
    public long timestamp;

    public LeaderboardEntry(String playerName, int topLevel, int enemiesKilled, boolean local) {
        this.playerName = playerName;
        this.topLevel = topLevel;
        this.enemiesKilled = enemiesKilled;
        this.local = local;
        this.timestamp = System.currentTimeMillis();
    }
}
