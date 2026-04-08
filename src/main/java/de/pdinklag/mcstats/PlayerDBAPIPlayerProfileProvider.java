package de.pdinklag.mcstats;

import de.pdinklag.mcstats.playerdb.API;
import de.pdinklag.mcstats.playerdb.APIRequestException;
import de.pdinklag.mcstats.playerdb.EmptyResponseException;

/**
 * Provides player profiles via the PlayerDB API.
 */
public class PlayerDBAPIPlayerProfileProvider implements PlayerProfileProvider {
    private final LogWriter log;

    /**
     * Constructs a new provider.
     */
    public PlayerDBAPIPlayerProfileProvider(LogWriter log) {
        this.log = log;
    }

    @Override
    public PlayerProfile getPlayerProfile(Player player) {
        if (player.getAccountType().maybeMojangAccount()) {
            try {
                PlayerProfile profile = API.requestPlayerProfile(player.getUuid());
                player.setAccountType(AccountType.MOJANG);
                return profile;
            } catch (EmptyResponseException e) {
                player.setAccountType(AccountType.OFFLINE);
            } catch (APIRequestException e) {
                log.writeError("PlayerDB API profile request for player failed: " + player.getUuid(), e);
            }
        }
        return player.getProfile();
    }
}
