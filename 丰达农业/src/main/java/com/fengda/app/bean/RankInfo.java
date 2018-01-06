package com.fengda.app.bean;

/**
 * Created by yuguang on 2017/10/28.
 */

public class RankInfo implements Comparable<RankInfo>  {

   private int rank_id;
    private String       rank_name;
    private int       min_number;
    private int       max_number;
    private int number;
    private String        price_rank;

    public int getRank_id() {
        return rank_id;
    }

    public String getRank_name() {
        return rank_name;
    }

    public int getMin_number() {
        return min_number;
    }

    public int getMax_number() {
        return max_number;
    }

    public String getPrice_rank() {
        return price_rank;
    }

    public int getNumber() {
        return number;
    }

    public int compareTo(RankInfo arg0) {
        return this.getNumber() - arg0.getNumber();//排序
    }
}
