package xyz.mugai.redditfetcher.configuration;

public class Credentials {

    private final String username;
    private final String password;
    private final String clientId;
    private final String clientSecret;


    public Credentials(String username, String password, String clientId, String clientSecret) {
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

}
