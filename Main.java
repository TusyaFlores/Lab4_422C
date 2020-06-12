/* EE422C Assignment #4 submission by
* Tatiana Flores
* TH27979
*/

package assignment4;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Main {
    final static String URLEndpoint = "http://kevinstwitterclient2.azurewebsites.net/api/products";

    /**
     * We will not use your Main class to test your code
     * Feel free to modify this as much as you want
     * Here is an example of how you might test the code from main
     * for Problem 1 and Problem 2
     */
    public static void main(String[] args) throws Exception {

        // Problem 1: Returning Tweets from Server
        //
        // 	
	System.out.println("Gettting all tweets");
        TweetReader reader = new TweetReader();
        List<Tweets> tweetsList = reader.readTweetsFromWeb(URLEndpoint);
        System.out.println(tweetsList);

        // Problem 2:
        // Filter Tweets by Username
        System.out.println("Getting written by kevinyee");
        Filter filter = new Filter();
        List<Tweets> filteredUser = filter.writtenBy(tweetsList,"kevinyee");
        System.out.println(filteredUser);

        // Filter by Timespan
        System.out.println("Those between 2017-11-11 and 2017-11-12");
        Instant testStart = Instant.parse("2017-11-11T00:00:00Z");
        Instant testEnd = Instant.parse("2017-11-12T12:00:00Z");
        Timespan timespan = new Timespan(testStart,testEnd);
        List<Tweets> filteredTimeSpan = filter.inTimespan(tweetsList,timespan);
        System.out.println(filteredTimeSpan);

        //Filter by words containinng
        System.out.println("Those with good or luck");
        List<Tweets> filteredWords = filter.containing(tweetsList,Arrays.asList("good","luck"));
        System.out.println(filteredWords);
	//Problem 3
	System.out.println("\n\nProblem 3a top 20 ");
	List<String> s2=SocialNetwork.findKMostFollower(tweetsList,20);
	System.out.println(s2);	
	System.out.println("\n\n\nProblem 3b cliques");
	List<Set<String>> s=SocialNetwork.findCliques(tweetsList);
        System.out.println(s);

	

    }
}
