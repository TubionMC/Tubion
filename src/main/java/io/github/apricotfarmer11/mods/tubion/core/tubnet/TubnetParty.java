package io.github.apricotfarmer11.mods.tubion.core.tubnet;

import io.github.apricotfarmer11.mods.tubion.core.helper.PlayerHelper;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TubnetParty {
    private static final Logger LOGGER = LoggerFactory.getLogger("Tubion/Party");
    public TubnetPlayer partyOwner;
    public ArrayList<TubnetPlayer> members = new ArrayList<>();

    // regex
    private static final Pattern PARTY_JOINED_MSG_MATCHER = Pattern.compile("([a-zA-Z0-9_]{2,16}) has joined the party!");
    private static final Pattern PARTY_LEFT_MSG_MATCHER = Pattern.compile("([a-zA-Z0-9_]{2,16}) left the party\\.");
    public boolean deleted = false;
    public UUID partyId;
    public UUID partyIdSecret;
    public TubnetParty(TubnetPlayer owner, @Nullable UUID partyId, @Nullable UUID partyIdSecret) {
        this.partyId = partyId;
        this.partyIdSecret = partyIdSecret;
        if (this.partyId == null) this.partyId = UUID.randomUUID();
        if (this.partyIdSecret == null) this.partyIdSecret = UUID.randomUUID();
        partyOwner = owner;
        members.add(partyOwner);
    }
    public void invitePlayer(String name) {
        if (this.deleted) return;
        for (Iterator<TubnetPlayer> it = this.members.iterator(); it.hasNext(); ) {
            TubnetPlayer member = it.next();
            if (member.username == name) {
                return;
            }
        }

        PlayerHelper.sendCommand("party invite" + name);
    }
}
