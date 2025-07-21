package model;

public class Reward {
    private String namaReward;
    private int pointReward;

    public Reward(String namaReward, int pointReward) {
        this.namaReward = namaReward;
        this.pointReward = pointReward;
    }

    public String getNamaReward() {
        return namaReward;
    }

    public void setNamaReward(String namaReward) {
        this.namaReward = namaReward;
    }

    public int getPointReward() {
        return pointReward;
    }

    public void setPointReward(int pointReward) {
        this.pointReward = pointReward;
    }
}
