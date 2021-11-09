package me.TurtlesAreHot.Duels;

import java.util.UUID;

public class Invite {
    private UUID inviter;
    private UUID invited;

    public Invite(UUID inviter, UUID invited) {
        this.inviter = inviter;
        this.invited = invited;
    }

    public UUID getInviter() { return this.inviter; }

    public UUID getInvited() { return this.invited; }

}
