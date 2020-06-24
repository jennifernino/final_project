package edu.brown.cs.student.client;

import java.io.File;
import java.io.IOException;

import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * The Main class of our project. This is where execution begins.
 *
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;
  private static final int MINUTES_UNTIL_SOCKET_TIMEOUT = 20;

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(this.args);
    runSparkServer();
  }

  /**
   * Gets the assigned port from Heroku (for deployment on Heroku).
   *
   * @return returns the default port if the heroku-port isn't set (i.e. if on
   *         localhost)
   */
  static int getHerokuAssignedPort() {
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (processBuilder.environment().get("PORT") != null) {
      return Integer.parseInt(processBuilder.environment().get("PORT"));
    }
    return 4567;
  }

  /**
   * Creates a freemarker engine.
   *
   * @return A freemarker engine.
   */
  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n", templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  /**
   * Runs the Spark server.
   */
  private static void runSparkServer() {
    Spark.port(getHerokuAssignedPort());
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new GUIHandlers.ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();
    Spark.webSocket("/websocket/cs32/sonic/skillz/users", GameWebSocket.class);
    Spark.webSocketIdleTimeoutMillis(MINUTES_UNTIL_SOCKET_TIMEOUT * 60000);

    // Spark routes
    Spark.path("/", () -> {
      Spark.get("", new GUIHandlers.HomepageHandler(), freeMarker);
      Spark.get("/page-not-found", new GUIHandlers.NotFoundHandler(), freeMarker);
      Spark.get("/error/:error", new GUIHandlers.GTSGeneralError(), freeMarker);

      // redirects to "/end-spotify-authorization"
      Spark.get("/start-spotify-authorization", new AuthorizationHandlers.StartAuthHandler());
      Spark.get("/end-spotify-authorization", new AuthorizationHandlers.EndAuthHandler());

      Spark.post("/begin-round", new PostHandlers.BeginRoundHandler());
      Spark.post("/get-next-round", new PostHandlers.NextRoundHandler());

      Spark.path("/guess-the-song", () -> {
        Spark.get("", new GUIHandlers.GuessTheSongHandler(), freeMarker);
        Spark.get("/new-session", new GUIHandlers.GTSNewSessionHandler(), freeMarker);
        Spark.get("/host-waiting-room", new GUIHandlers.GTSGetWaitingRoom(), freeMarker);
        Spark.get("/waiting-room", new GUIHandlers.GTSGetWaitingRoom(), freeMarker);
        Spark.get("/join-session", new GUIHandlers.GTSTryJoinSessionHandler(), freeMarker);
        Spark.get("/join-session/:error", new GUIHandlers.GTSErrorJoinSessionHandler(), freeMarker);
        Spark.get("/start-game", new GUIHandlers.GTSGetStartGameHandler(), freeMarker);
        Spark.get("/game-ready", new GUIHandlers.GTSGameReady(), freeMarker);
        Spark.get("/welcome-page", new GUIHandlers.GTSWelcomePage(), freeMarker);
        Spark.get("/game-round", new GUIHandlers.GTSNextRound(), freeMarker);
        Spark.get("/scoreboard/:correct", new GUIHandlers.GTSScoreBoard(), freeMarker);
        Spark.get("/final-score", new GUIHandlers.GTSFinalScore(), freeMarker);
        // POST routes
        // host starts session
        Spark.post("/host-waiting-room", new PostHandlers.HostWaitingRoomHandler());
        // guest user joins
        Spark.post("/waiting-room", new PostHandlers.WaitingRoomHandler());
        // start game - due to the way WaitingRoom.ftl is set up, only host would be
        // able to access this route
        Spark.post("/start-game", new PostHandlers.StartGameHandler());
        Spark.post("/guess-submitted", new PostHandlers.GuessSubmittedHandler());
      });
    });

    Spark.notFound("/page-not-found");
  }
}
