package com.bitallowance;

import java.util.List;

public class GiveReward {

    // Variables
    private List<Transaction> rewardList;
    private List<Entity> entityList;

    // Getters
    public List<Entity> getEntityList() {
        return this.entityList;
    }

    public List<Transaction> getRewardList() {
        return this.rewardList;
    }

    // Setters
    public void setEntityList(List<Entity> entityList) {
        this.entityList = entityList;
    }

    public void setRewardList(List<Transaction> rewardList) {
        this.rewardList = rewardList;
    }

    // Giving a reward method
    public void giveReward() {

    }
}
