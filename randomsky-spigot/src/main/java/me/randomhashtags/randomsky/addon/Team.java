package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;

import java.util.List;

public interface Team extends Itemable {
    List<String> getHaveNotChosenTeamMsg();
    List<String> getContributorsMsg();
    int getMaxShownDailyTopContributors();
    int getMaxShownWeeklyTopContributors();

    double getSelectedPercent();
    void setSelectedPercent(double percent);
    void calculateSelectedPercent();
}
