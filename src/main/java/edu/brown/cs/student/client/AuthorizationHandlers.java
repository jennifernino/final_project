package edu.brown.cs.student.client;

import java.io.IOException;
import java.util.List;

import edu.brown.cs.student.Constants;
import edu.brown.cs.student.sonicSkillz.APIHelper;
import edu.brown.cs.student.sonicSkillz.gameunits.GameSession;
import edu.brown.cs.student.sonicSkillz.gameunits.User;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

/**
 * Contains all helper handlers for GET Spark routes related to Spotify
 * authorization.
 */
public class AuthorizationHandlers {

  /**
   * Handler for "/start-spotify-authorization". Redirects user to Spotify
   * authorization page, and upon completion, redirects user to
   * "/end-spotify-authorization".
   */
  protected static class StartAuthHandler implements Route {
    @Override
    public String handle(Request request, Response response) {
      String userId = request.cookies().get("userID");
      // store gameCode in cookie again (was getting lost)
      String gameCode = GameHandlers.userIdToUser.get(userId).getGameCode();
      response.cookie("gameCode", gameCode);
      System.out.println("Start auth == userId:" + userId + " gameCode:" + gameCode);
      // protect GET endpoint
      if (GameHandlers.userIdToUser.get(userId) == null) {
        Spark.halt(401, "Unauthorized access."); // replace with popup and/or redirect
      }

      String authRequest = "https://accounts.spotify.com/authorize?client_id=" + Constants.CLIENT_ID
          + "&redirect_uri=" + Constants.REDIRECT_URI
          + "&scope=streaming%20user-read-email%20user-top-read" + "%20user-read-playback-position"
          + "%20user-read-recently-played%20user-follow-read"
          + "%20user-library-read%20user-read-private" + "&state=";

      // pass user id in state to validate the response and ensure that both the
      // request and
      // response originated in the same browser
      String endRequest = "&response_type=code&show_dialog=true";
      String fullRequest = authRequest + userId + endRequest;
      response.redirect(fullRequest);
      return null;
    }
  }

  /**
   * Handler for "/end-spotify-authorization". If user declined to authorize, will
   * print error and halt user. If user successfully authorized, will set user's
   * Spotify-related info and redirect them to the waiting room page.
   */
  protected static class EndAuthHandler implements Route {
    @Override
    public String handle(Request request, Response response) throws IOException {
      String userId = request.cookies().get("userID");
      System.out.println("userId:" + userId);
      User user = GameHandlers.userIdToUser.get(userId);
      // protect GET endpoint
      if (user == null) {
        Spark.halt(401, "Unauthorized access.");
      }

      User currUser = GameHandlers.userIdToUser.get(userId);
      APIHelper api = new APIHelper();
      if (request.queryParams("error") != null) { // user declined to authenticate
        System.err.println("ERROR: " + request.queryParams("error"));
        Spark.halt(401, "Cannot proceed without Spotify authorization.");
      } else {
        String authCode = request.queryParams("code");
        // set User's Spotify-related info here
        currUser.setApiAuthorizationCode(authCode);
        currUser.setApiRefreshToken(api.getRefreshToken(currUser, Constants.REDIRECT_URI));
        currUser.setApiAccessToken(api.refreshAccessToken(currUser));
        // currUser.setSpotifyAccountType(api.getAccountType(currUser));
        currUser.setSpotifyProfileImageUrl(api.getUserImage(currUser));

        // add User to game
        String gameCode = currUser.getGameCode();
        System.out.println(">>>atuh gameCode:" + gameCode);
        // if user has not been added to game session (eg is not host)
        GameSession gameSession = GameHandlers.gameCodeToGameSession.get(gameCode);
        List<User> users = gameSession.getUsersInSession();
        if (!users.contains(user)) {
          System.out.println("Users does not contain user:" + user.getNickname());
          GameHandlers.addUser(userId, gameCode, user.getNickname());
        }

        // redirect user to waiting room page
        if (currUser.isHost()) {
          response.redirect("/guess-the-song/host-waiting-room"); // GET
        } else {
          WebSocketHandlers.replyNewMember(currUser.getGameCode(), userId);
          response.redirect("/guess-the-song/waiting-room"); // GET
        }
      }
      return null;
    }
  }

}
