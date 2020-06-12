/* EE422C Assignment #4 submission by
* Tatiana Flores
* TH27979
*/

package assignment4;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Social Network consists of methods that filter users matching a
 * condition.
 *
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {
    //3 modes of operations
    final static int cliquemode=0;
    //0=normal
    //1=remove all single person cliques
    //2=remove all single person cliques unless they self mention 
    //Do they count as their own follower for popularity
    final static boolean countself=false;    
    //A graph node is just here to let us builda really simple graph of twitterusers
    //It implements comparable so I can sort by # of followers
    static class Node implements Comparable{
	//The users's name
	final String name; 
	//Do they mention themselves?
	boolean narcisist=false;
	//A set of users who follow them
	public Set<Node> followedby=new HashSet<Node>(); 
	//A set of users they follow
	public Set<Node> follows=new HashSet<Node>(); 
	//A set of  users who are both followed and followers. Not needed but makes some operations simple	
	public Set<Node> mutual=new HashSet<Node>();
	//A dictionary of user names=>Nodes
	private static HashMap<String,Node> dictionary=new HashMap<String,Node>();
	//Resets the dictionary so we can build a new graph
	public static void init(){
	    dictionary=new HashMap<String,Node>();
	}
	//Nodes print as strings
	public String toString(){return(name);}
	//Compare to compares the numbe of followers
	//If they are the same compares the names
	public int compareTo(Object o){
	    Node n=(Node)o;
	    int myfollowers=followedby.size();
	    //Add 1 to my followers if I follow myself(narcisist and countself is true
	    if(narcisist &&countself){myfollowers++;}
		//Ditto for their followers
	    int theirfollowers=n.followedby.size();
	    if(n.narcisist && countself){theirfollowers++;}
		//First compare # of followers
	    if(myfollowers<theirfollowers){
		return -1;
	    }else if (myfollowers>theirfollowers){
		return 1;
	    }
		//If same size return name just so we have a fixed order
	    return(name.compareTo(n.name));
	}
	//This is only used by getnode
	private Node(String name){
	    this.name=name.toLowerCase();
	    dictionary.put(this.name,this);
	}
	//getNode returns the node for a user. Creates one if it doesn't already exist.
	static public Node getNode(String name){
	    //Ensure name is lower case
	    name=name.toLowerCase();
	    //If it already exists return it
	    if(dictionary.containsKey(name)){return dictionary.get(name);}
	    //Otherwise create a new node (which adds to dictionary) and return it
	    return(new Node(name));
	}
	
	//Adds a user that this person follows
	public void follows(String name){
	    //They can't follow themself  but set the narcisist flag if they do
	    if(this.name.equals(name)){
		narcisist=true;
		return;
	    }
	    //Find the person they follow
	    Node n=getNode(name);
	    //If they already follow this person then we're done
	    if(follows.contains(n)){
		return;
	    }else{
		//Otherwise add the new user to the set of users we follow
		follows.add(n);
		//and add us foller of the other user
		n.followedby.add(this);
		//If they follow us then it's mutual and add to both mutual sets
		if(followedby.contains(n)){
		    mutual.add(n);
		    n.mutual.add(this);
		}
	    }
	}
    }
    

    /**
     * Get K most followed Users.
     *
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @param k
     *            integer of most popular followers to return
     * @return the set of usernames who are most mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getName()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like ethomaz@utexas.edu does NOT
     *         contain a mention of the username.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static List<String> findKMostFollower(List<Tweets> tweets, int k) {
	//Build a graph of followers
	buildFollowers(tweets);	
	//Create a list of all the users
	List<Node> list=new ArrayList<Node>();
	list.addAll(Node.dictionary.values());
	//Sort this list by # of followers (default sort method for Node)
	Collections.sort(list);       
	List<String> mostFollowers = new ArrayList<>();
	//Grab the top k users and return
	//If k<list_size use list size instead
	for(int i=0;i<k & i<list.size();i++){
	    mostFollowers.add(list.get(list.size()-i-1).name);
	}
        return mostFollowers;
    }

    /**
     * Find all cliques in the social network.
     *
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     *
     * @return list of set of all cliques in the graph
     */
    public static List<Set<String>> findCliques(List<Tweets> tweets) {
	buildFollowers(tweets);	
	//Execute the BronKerbosh algorithim
	List<Set<String>> result=BronKerbosch(new HashSet<Node>(),new HashSet(Node.dictionary.values()),new HashSet<Node>());
	//3 possibilities
	if(cliquemode<=0){
	    //Normal
	    return(result);
	}
	//Otherwise remove all single node sets
	List<Set<String>> groups=new ArrayList<Set<String>>();
	List<Set<String>> narcisists=new ArrayList<Set<String>>();
	for(Set<String> s: result){
	    if(s.size()>1){
		groups.add(s);
		//If they self mention and we are in mode 1
	    }else if(s.size()==1 && cliquemode==1){
		//Get the user from the set
		String name=(String)s.toArray()[0];
		if(Node.getNode(name).narcisist){
		    //If they are a narcisist and the mode is 1 then add them back
		    Set<String> hs=new HashSet<String>();
		    hs.add(name);
		    groups.add(hs);
		}
	    }	
	}
	return groups;
    }
    //Take a list of tweets and build a Node graph
    public static void buildFollowers(List<Tweets> tweets){
	//Init in case this list is different then previous lists
	Node.init();	
	//For each tweet
	for(Tweets t: tweets){
	    //Get the name
	    Node n=Node.getNode(t.getName());
	    //Split it on valid characters
	    for(String s: t.getText().toLowerCase().split("[^a-zA-Z0-0_@]+")){
		//If it is @ then it's a valid mention
		if(Pattern.matches("^@.+",s)){
		    //@a@b (a is valid)
		    //@a@b@c ( a is valid)
		    //@a@@b (a and b are valid)
		    
		    boolean valid=true;
		    for(String loop:s.split("@")){
			if(loop.length()==0){valid=true;}
			else if(valid){
			    n.follows(loop);
			}
		    }
		}
	    }
	}
    }
    //Run the BronKerbosh algorithim
    public static List<Set<String>> BronKerbosch(HashSet<Node> R, HashSet<Node> P, HashSet<Node> X){
        List<Set<String>> result=new ArrayList<Set<String>>(); 
	//If P and X ae both empty
	if(P.size()==0 && X.size()==0){
	    //report R as maxium clique
	    Set<String>r=new HashSet<String>();
	    for(Node n: R){
		r.add(n.name);
	    } 
	    result.add(r);
	    return(result);
	}
	//for each vertex v in P
	//System.out.println("Running over:"+P.size());
	for(Node v: P.toArray(new Node[0])){
	    //BronKerbosch(R U {v}, P ^ N(v), X ^ N(v)
	    HashSet<Node> myP=(HashSet<Node>)P.clone();	    
	   
	    //System.out.println("Removeing:"+v.name+", myPSize"+myP.size());
	    HashSet<Node>myR=(HashSet<Node>)R.clone();
	    HashSet<Node> myX=(HashSet<Node>)X.clone();
	    myR.add(v);
	    myP.retainAll(v.mutual);
	    myX.retainAll(v.mutual);
	    result.addAll(BronKerbosch(myR, myP, myX));
	    //P = P -{v}
	    P.remove(v);
	    //X= X U {v}
	    X.add(v);
	}	    
	return(result);
	
    }
	
}


