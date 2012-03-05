package com.dral.Chatter.integration;

import com.dral.Chatter.Chatter;
import com.ensifera.animosity.craftirc.EndPoint;
import com.ensifera.animosity.craftirc.RelayedMessage;

import java.util.List;

public class ChatterCraftIRC implements EndPoint {
    Chatter Chatter;

    public ChatterCraftIRC (Chatter Chatter){
        this.Chatter = Chatter;
    }
    
    public void initIRC() {
        try {
            Chatter.craftirchandler.registerEndPoint("Chatter", this);
            RelayedMessage rm = Chatter.craftirchandler.newMsg(this, null, "generic");
            rm.setField("message", "I'm aliiive!");
            rm.post();
            Chatter.craftircenabled = true;
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void relaymsg(String eventType, String sender, String message) {
        relaymsg(eventType, sender, message, "");
    }

    public void relaymsg(String eventType, String sender, String message, String mod) {
        RelayedMessage rm = Chatter.craftirchandler.newMsg(this, null, eventType);
        rm.setField("message", message);
        rm.setField("sender", sender);
        rm.setField("moderator", mod);
        rm.post();
    }



    @Override
    public Type getType() {
        return EndPoint.Type.MINECRAFT;
    }

    @Override
    public void messageIn(RelayedMessage msg) {
        if (msg.getEvent() == "join") {
            Chatter.getServer().broadcastMessage(msg.getField("sender") + " joined da game!");
        }
    }

    @Override
    public boolean userMessageIn(String username, RelayedMessage msg) {
        return false;
    }

    @Override
    public boolean adminMessageIn(RelayedMessage msg) {
        return false;
    }

    @Override
    public List<String> listUsers() {
        return null;
    }

    @Override
    public List<String> listDisplayUsers() {
        return null;
    }
}
