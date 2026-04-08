package de.pdinklag.mcstats.playerdb;

import java.net.URI;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import de.pdinklag.mcstats.PlayerProfile;
import de.pdinklag.mcstats.util.StreamUtils;

/**
 * PlayerDB API.
 */
public class API {
    private static final String API_URL = "https://playerdb.co/api/player/minecraft/";
    private static final String TEXTURES_URL_PREFIX = "https://textures.minecraft.net/texture/";
    private static final String USER_AGENT = "MinecraftStats";

    /**
     * Requests a player profile from the PlayerDB API.
     * 
     * @param id the UUID or username of the player in question
     * @return the player profile associated to the given id
     * @throws EmptyResponseException in case the PlayerDB API gives an empty response
     * @throws APIRequestException    in case any error occurs trying to request the
     *                                profile
     */
    public static PlayerProfile requestPlayerProfile(String id) throws EmptyResponseException, APIRequestException {
        try {
            final String response;
            {
                URI uri = URI.create(API_URL + id);
                java.net.URL url = uri.toURL();
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", USER_AGENT);
                response = StreamUtils.readStreamFully(conn.getInputStream());
                conn.disconnect();
            }

            if (response.isEmpty()) {
                throw new EmptyResponseException();
            }

            JSONObject root = new JSONObject(response);
            JSONObject data = root.optJSONObject("data");
            JSONObject player = (data == null) ? null : data.optJSONObject("player");
            if (player == null) {
                throw new EmptyResponseException();
            }

            String name = player.optString("username", "");
            if (name.isEmpty()) {
                throw new EmptyResponseException();
            }

            String skin = null;
            String skinTexture = player.optString("skin_texture", "");
            if (skinTexture.startsWith(TEXTURES_URL_PREFIX) && skinTexture.length() > TEXTURES_URL_PREFIX.length()) {
                skin = skinTexture.substring(TEXTURES_URL_PREFIX.length());
            }

            return new PlayerProfile(name, skin, System.currentTimeMillis());
        } catch (EmptyResponseException e) {
            throw e; // nb: delegate
        } catch (Exception e) {
            throw new APIRequestException(e);
        }
    }
}
