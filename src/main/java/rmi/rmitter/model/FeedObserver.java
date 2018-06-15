package rmi.rmitter.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by affo on 13/03/18.
 *
 * Observes the updates generated from events generated by posts and hashtags on the feed of some User
 */
public interface FeedObserver extends Remote {
    void onFolloweePost(Post post) throws RemoteException;
    void onHashtagUse(Hashtag hashtag, Post post) throws RemoteException;
}
