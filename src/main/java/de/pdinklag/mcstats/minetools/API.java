package de.pdinklag.mcstats.minetools;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import de.pdinklag.mcstats.PlayerProfile;
import de.pdinklag.mcstats.util.StreamUtils;

/**
 * Minetools.eu API.
 */
public class API {
    private static final String API_URL = "https://api.minetools.eu/profile/";
    private static final String SKIN_URL = "http://textures.minecraft.net/texture/";

    /**
     * Requests a player profile from the Minetools.eu API.
     * 
     * @param uuid the UUID of the player in question
     * @return the player profile associated to the given UUID.
     * @throws EmptyResponseException in case the Minetools.eu API gives an empty response
     * @throws APIRequestException    in case any error occurs trying to request the
     *                                profile
     */
    public static PlayerProfile requestPlayerProfile(String uuid) throws EmptyResponseException, APIRequestException {
        try {
            final String response;
            {
                URL url = new URL(API_URL + uuid);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                response = StreamUtils.readStreamFully(conn.getInputStream());
                conn.disconnect();
            }

            if (!response.isEmpty()) {
                JSONObject obj = new JSONObject(response);
                
                // Check if the response has the decoded profile
                if (!obj.has("decoded")) {
                    throw new EmptyResponseException();
                }
                
                JSONObject decoded = obj.getJSONObject("decoded");
                String name = decoded.getString("profileName");

                // Extract skin URL from decoded textures
                JSONObject textures = decoded.getJSONObject("textures");
                if (!textures.has("SKIN")) {
                    throw new EmptyResponseException();
                }
                
                String skinUrl = textures.getJSONObject("SKIN").getString("url");
                String skin = skinUrl.substring(SKIN_URL.length());

                // Use timestamp from decoded profile, or current time if not available
                long timestamp = decoded.optLong("timestamp", System.currentTimeMillis());

                return new PlayerProfile(name, skin, timestamp);
            } else {
                throw new EmptyResponseException();
            }
        } catch (EmptyResponseException e) {
            throw e; // nb: delegate
        } catch (Exception e) {
            throw new APIRequestException(e);
        }
    }
}
