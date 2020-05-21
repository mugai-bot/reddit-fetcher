package xyz.mugai.redditfetcher;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.*;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import xyz.mugai.redditfetcher.configuration.Config;
import xyz.mugai.redditfetcher.configuration.Credentials;
import xyz.mugai.redditfetcher.database.Database;

import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        // load the database
        new Database();
        // get credentials
        Credentials credentials = new Credentials(Config.get("rusername"), Config.get("password"), Config.get("clientId"), Config.get("clientSecret"));

        // get the reddit oauth credentials
        net.dean.jraw.oauth.Credentials oauthCreds = net.dean.jraw.oauth.Credentials.script(credentials.getUsername(), credentials.getPassword(), credentials.getClientId(), credentials.getClientSecret());

        // create a unique user-agent for out bot
        UserAgent userAgent = new UserAgent("Mugai Reddit Fetcher v1.0 DiscordLinkerBot");

        // network adapter
        NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);

        // authenticate our client
        RedditClient client = OAuthHelper.automatic(adapter, oauthCreds);


        DefaultPaginator<Submission> cats = client.subreddit(Config.get("subreddit")).posts().limit(15)
                .sorting(SubredditSort.HOT)
                .timePeriod(TimePeriod.DAY)
                .build();
        List<Listing<Submission>> list = cats.accumulate(1);
        Listing<Submission> submissions = list.get(0);
        for(Submission submission : submissions){
            if(submission.isArchived()){
                System.out.println("[ OK ] Skipping submission " + submission.getId() + " because it's archived.");
                continue;
            } else if (submission.isStickied()){
                System.out.println("[ OK ] Skipping submission " + submission.getId() + " because it's stickied.");
                continue;
            } else if (!submission.getDistinguished().equals(DistinguishedStatus.NORMAL)){
                System.out.println("[ OK ] Skipping submission " + submission.getId() + " because it's distinguished.");
                continue;
            } else if (!isCorrectLink(submission.getUrl())){
                System.out.println("[ OK ] Skipping submission " + submission.getId() + " because it's not a valid image link.");
                continue;
            }
            if (Database.insertCat(submission.getUrl(), submission.getTitle(), submission.getAuthor(), "https://reddit.com" + submission.getPermalink())){
                System.out.println("[ OK ] Inserted " + submission.getId() + ".");
            }else {
                System.out.println("[ OK ] Failed to insert " + submission.getId() + ", it probably already exists.");
            }
        }

        Thread.sleep(5000);
        System.exit(1);

    }

    private static boolean isCorrectLink(String link){
        if(link.endsWith(".png") || link.endsWith(".jpg")) return true;
        if(link.startsWith("https://i.redd.it/") || link.startsWith("https://i.imgur.com/")) return true;

        return false;
    }
}
