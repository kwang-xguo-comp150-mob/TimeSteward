package edu.tufts.cs.kwangxguo.timesteward;

public class User {
    public int usagetime;
    public int timelimit;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(int usagetime, int timelimit) {
        this.usagetime = usagetime;
        this.timelimit = timelimit;
    }
}
