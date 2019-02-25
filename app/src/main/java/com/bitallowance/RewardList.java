package com.bitallowance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Filter;

public class RewardList {

    // Variables
    private List<Reward> rewardList;
    private List<Entity> entityList;
    private Filter filter;
    private LocalDateTime timeFilter;
    private BigDecimal costFilter;

    // Private Methods
    private void setGoal () {

    }

    private void request() {

    }

    // Getters
    public List<Reward> getRewardList() {
        return rewardList;
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public Filter getFilter() {
        return filter;
    }

    public LocalDateTime getTimeFilter() {
        return timeFilter;
    }

    public BigDecimal getCostFilter() {
        return costFilter;
    }

    // Setters
    public void setRewardList(List<Reward> rewardList) {
        this.rewardList = rewardList;
    }

    public void setEntityList(List<Entity> entityList) {
        this.entityList = entityList;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setCostFilter(BigDecimal costFilter) {
        this.costFilter = costFilter;
    }

    // Viewing, Adding, Editing Rewards
    public void openViewReward() {

    }

    public void openAddReward() {

    }

    public void openEditReward() {
        
    }
}
