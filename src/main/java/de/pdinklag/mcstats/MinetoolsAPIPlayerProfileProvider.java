package de.pdinklag.mcstats;

import de.pdinklag.mcstats.minetools.API;
import de.pdinklag.mcstats.minetools.APIRequestException;
import de.pdinklag.mcstats.minetools.EmptyResponseException;

/**
 * Provides player profiles via the Minetools.eu API.
 */
public class MinetoolsAPIPlayerProfileProvider implements PlayerProfileProvider {
    private final LogWriter log;

    /**
     * Constructs a new provider.
     */
    public MinetoolsAPIPlayerProfileProvider(LogWriter log) {
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
                log.writeError("Minetools.eu API profile request for player failed: " + player.getUuid(), e);
            }
        }
        return player.getProfile();
    }
}
