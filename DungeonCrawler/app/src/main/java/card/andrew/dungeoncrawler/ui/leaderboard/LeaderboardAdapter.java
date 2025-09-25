package card.andrew.dungeoncrawler.ui.leaderboard;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import card.andrew.dungeoncrawler.R;
import card.andrew.dungeoncrawler.data.model.LeaderboardEntry;

public class LeaderboardAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<LeaderboardEntry> entries;
    private boolean showHeader = true; // Toggle for header row

    public LeaderboardAdapter(List<LeaderboardEntry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (showHeader && position == 0) {
            // Header row
            holder.rankText.setText(R.string.rank);
            holder.playerNameText.setText(R.string.player);
            holder.levelText.setText(R.string.level);
            holder.enemiesKilledText.setText(R.string.enemies_killed);
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray));
        } else {
            // Data row (adjust position for header)
            int dataPosition = showHeader ? position - 1 : position;
            LeaderboardEntry entry = entries.get(dataPosition);
            holder.rankText.setText(String.valueOf(position)); // Rank by position
            holder.playerNameText.setText(entry.playerName);
            holder.levelText.setText(String.valueOf(entry.topLevel));
            holder.enemiesKilledText.setText(String.valueOf(entry.enemiesKilled));
            if (position % 2 == 0) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray));
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    public int getItemCount() {
        return showHeader ? entries.size() + 1 : entries.size();
    }

    public void setShowHeader(boolean show) {
        this.showHeader = show;
        notifyDataSetChanged();
    }
}

class ViewHolder extends RecyclerView.ViewHolder {
    TextView rankText, playerNameText, levelText, enemiesKilledText;

    ViewHolder(@NonNull View itemView) {
        super(itemView);
        rankText = itemView.findViewById(R.id.item_rank);
        playerNameText = itemView.findViewById(R.id.item_player_name);
        levelText = itemView.findViewById(R.id.item_level);
        enemiesKilledText = itemView.findViewById(R.id.item_enemies_killed);
    }
}
