package com.example.novigrad;

public class Schedule implements Comparable {
    public String day;
    public String from;
    public String to;
    public int id;

    public Schedule(String day, String from, String to, int id) {
        this.day = day;
        this.from = from;
        this.to = to;
        this.id = id;
    }

    @Override
    public int compareTo(Object o) {
        Schedule c = (Schedule) o;
        return this.id - c.id;
    }
}
