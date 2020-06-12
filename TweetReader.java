/* EE422C Assignment #4 submission by
* Tatiana Flores
* TH27979
*/

package assignment4;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.util.regex.Pattern;
import java.time.Instant;

/**
 * TweetReader contains method used to return tweets from method
 * Do not change the method header
 */
public class TweetReader {
    //MyTweets is a simple class to read JSON into it has no error checking
    static public class MyTweets {
	public String Id;
	public String Name;
	public String Date;
	public String Text;	
	public MyTweets(){}
    }
    /**
     * Find tweets written by a particular user. 
     *
     * @param url
     *            url used to query a GET Request from the server
     * @return return list of tweets from the server
     *
     */
    public static List<Tweets> readTweetsFromWeb(String url) throws Exception
    {
	//Read a web request in stolen from web
	OkHttpClient client=new OkHttpClient();
	Request request=new Request.Builder().url("http://kevinstwitterclient2.azurewebsites.net/api/products").build();
	Response response=client.newCall(request).execute();
	String data=response.body().string();
	//use Jackson to read the json
	ObjectMapper mapper= new ObjectMapper();
	//Read in all the tweets
	List<MyTweets> list1 = mapper.readValue(data, new TypeReference<List<MyTweets>>(){});
        List<Tweets> tweetList = new ArrayList<Tweets>();
	for (MyTweets mt: list1){
	    //Create a new Tweets and add to list. An error throws Exception which stops processing for that MyTweets.
	    try{
		Tweets t=new Tweets();
		//Invalid if id <=0		
		if(Integer.parseInt(mt.Id)<=0){throw new Exception();}
		t.setId(Integer.parseInt(mt.Id));	
		//Invalid if name length>0 and conists only of valid characters
		if(mt.Name!=null && (mt.Name.length()>0) && Pattern.matches("^[a-zA-Z0-9_]+$",mt.Name)){
		    t.setName(mt.Name);
		}else { throw new Exception();}
		//Let Instant throw an error if it can't parse the date
		Instant.parse(mt.Date);
		//Otherwise store the date as a string
		t.setDate(mt.Date);
		//Ensure the length <=140 and it's not null
		if(mt.Text!=null && mt.Text.length()<=140){
			t.setText(mt.Text);
		}else {
		    //Otherwise throw an eception
		    throw new Exception();
		}
		//No Exception has been thrown so add to tweet list
		tweetList.add(t);
	    }catch(Exception e){}
	}
	return tweetList; 
    }
}
