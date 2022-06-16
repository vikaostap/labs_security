package labs567;

import com.sun.net.httpserver.*;
import common.Utils;
import org.apache.commons.math3.util.Pair;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class WebServer {

    private static Database db;
    // Username <==> (Token, Date of creation (in milliseconds))
    private static final HashMap<String, Pair<String, Long>> authTokenCache = new HashMap<>();
    private static final int tokenValidTimeInSeconds = 360;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyStoreException,
            CertificateException, UnrecoverableKeyException, KeyManagementException {
        // create database
        db = new Database();

        HttpsServer webServer = HttpsServer.create(new InetSocketAddress(3000), 0);
        HttpContext context = webServer.createContext("/");
        context.setHandler(WebServer::httpRequestsHandler);

        System.out.println("Configuring TLS on our web server");
        SSLContext sslContext = SSLContext.getInstance("TLS");
        char[] password = "lab7Password".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");

        // the command generates selfsigned-ks.jks file
        // keytool.exe -genkey -keyalg RSA -keysize 2048 -alias selfsigned -keystore selfsigned-ks.jks -storepass lab7Password -validity 360
        FileInputStream fis = new FileInputStream("src/labs567/conf/selfsigned-ks.jks");
        ks.load(fis, password);

        // setup the key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);

        // setup the trust manager factory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        // setup the HTTPS context and parameters
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        webServer.setHttpsConfigurator(new TlsConfigurator(sslContext));

        SSLEngine engine = sslContext.createSSLEngine();
        System.out.println("Web Server supports:");
        System.out.print("protocols: ");
        System.out.println(Arrays.toString(engine.getEnabledProtocols()));
        System.out.print("cipher suites:");
        System.out.println(Arrays.toString(engine.getEnabledCipherSuites()));

        System.out.println("Start web server on " + webServer.getAddress().toString());
        webServer.start();
    }

    private static void httpRequestsHandler(HttpExchange request) throws IOException {
        String urlPath = request.getRequestURI().getPath();
        String method = request.getRequestMethod();
        System.out.println("Received " + method + " request to " + urlPath);

        byte[] responseDataBytes = null;
        String responseData = null;
        int statusCode;

        if (urlPath.isEmpty() || urlPath.equals("/")) {
            // request for main page
            statusCode = 200;
            responseData = Utils.readWholeFile("src/labs567/resources/index.html");
        } else if (urlPath.equals("/style.css")) {
            statusCode = 200;
            responseData = Utils.readWholeFile("src/labs567/resources/style.css");
        } else if (urlPath.equals("/script.js")) {
            statusCode = 200;
            responseData = Utils.readWholeFile("src/labs567/resources/script.js");
        } else if (urlPath.equals("/favicon.ico")) {
            statusCode = 200;
            responseDataBytes = Files.readAllBytes(Paths.get("src/labs567/resources/page-icon.png"));
        } else if (urlPath.equals("/register")) {
            if (method.equals("POST")) {
                String postData = getPostData(request);
                HashMap<String, String> params = parseURLParameters(postData);
                System.out.println("register username: " + params.get("username"));
                System.out.println("register password: " + params.get("password"));
                if (params.get("username") == null || params.get("password") == null) {
                    System.out.println("ERROR: missing parameters - please check username, password in " + postData);
                    statusCode = 400;
                    responseData = "Error occurred! Invalid parameters.";
                } else if (db.userExists(params.get("username"))) {
                    statusCode = 400;
                    responseData = "User is already exist";
                } else {
                    String result = UserData.isPasswordReliable(params.get("password"));
                    if (result == "OK") {
                        db.createNewUser(params.get("username"), params.get("password"));
                        statusCode = 200;
                        responseData = "User '" + params.get("username") + "' registered";
                    } else {
                        statusCode = 400;
                        responseData = result;
                    }
                }
            } else {
                // we only except POST request for register
                System.out.println("/register does not support " + method + " request");
                statusCode = 405; // 405 "Method Not Allowed"
                responseData = "Method Not Allowed";
            }
            System.out.println("Response code: " + statusCode);
            System.out.println("Response text: " + responseData);
        } else if (urlPath.equals("/login")) {
            if (method.equals("POST")) {
                String postData = getPostData(request);
                HashMap<String, String> params = parseURLParameters(postData);
                System.out.println("login username: " + params.get("username"));
                System.out.println("login password: " + params.get("password"));
                if (params.get("username") == null || params.get("password") == null) {
                    System.out.println("ERROR: missing parameters - please check username, password in " + postData);
                    statusCode = 400;
                    responseData = "Error occurred! Invalid parameters.";
                } else if (db.userExists(params.get("username"))) {
                    UserData ud = db.getUserData(params.get("username"));
                    if (ud.isPasswordCorrect(params.get("password"))) {
                        statusCode = 200;
                        responseData = "User logged in!";
                        request.getResponseHeaders().add("Auth-Token", generateNewAuthToken(params.get("username")));
                    } else {
                        statusCode = 400;
                        responseData = "Password is not correct";
                    }
                } else {
                    statusCode = 400;
                    responseData = "User does not exist";
                }
            } else {
                // we only except POST request for login
                System.out.println("/login does not support " + method + " request");
                statusCode = 405; // 405 "Method Not Allowed"
                responseData = "Method Not Allowed";
            }
            System.out.println("Response code: " + statusCode);
            System.out.println("Response text: " + responseData);
        } else if (urlPath.equals("/personalInfo.html")) {
            if (method.equals("GET")) {
                String data = request.getRequestURI().getQuery();
                System.out.println("GET request params: " + data);
                HashMap<String, String> params = parseURLParameters(data);
                if (params.get("username") == null || params.get("token") == null) {
                    System.out.println("ERROR: missing parameters - please check username, token in " + data);
                    statusCode = 400;
                    responseData = "Error occurred! Invalid parameters.";
                } else if (isTokenValid(params.get("username"), params.get("token"))) {
                    UserData ud = db.getUserData(params.get("username"));

                    responseData = Utils.readWholeFile("src/labs567/resources/personalInfo.html");
                    responseData = responseData.replace("{username}", params.get("username"));
                    responseData = responseData.replace("{token}", params.get("token"));
                    responseData = responseData.replace("{address}", ud.getHomeAddress());
                    responseData = responseData.replace("{phoneNumber}", ud.getPhoneNumber());
                    //System.out.println("responseData: " + responseData);
                    statusCode = 200;
                } else {
                    statusCode = 400;
                    responseData = "Invalid token";
                }
            } else {
                // we only except GET request for personalInfo page
                System.out.println("/personalInfo does not support " + method + " request");
                statusCode = 405; // 405 "Method Not Allowed"
                responseData = "Method Not Allowed";
            }
            System.out.println("Response code: " + statusCode);
        } else if (urlPath.equals("/updatePersonalInfo")) {
            if (method.equals("POST")) {
                String postData = getPostData(request);
                System.out.println("updatePersonalInfo: " + postData);
                HashMap<String, String> params = parseURLParameters(postData);
                if (params.get("username") == null || params.get("token") == null ||
                        params.get("address") == null || params.get("phoneNumber") == null) {
                    System.out.println("ERROR: missing parameters - please check username, token, address, phoneNumber in " + postData);
                    statusCode = 400;
                    responseData = "Error occurred! Invalid parameters.";
                } else if (isTokenValid(params.get("username"), params.get("token"))) {
                    db.updateUserInfo(params.get("username"), params.get("address"), params.get("phoneNumber"));
                    statusCode = 200;
                    responseData = "Data were updated!";
                } else {
                    statusCode = 400;
                    responseData = "Error occurred! Invalid token.";
                }
            } else {
                // we only except POST request for login
                System.out.println("/updatePersonalInfo does not support " + method + " request");
                statusCode = 405; // 405 "Method Not Allowed"
                responseData = "Method Not Allowed";
            }
            System.out.println("Response code: " + statusCode);
            System.out.println("Response text: " + responseData);
        } else {
            statusCode = 404;
            responseData = "Resource not found!";
        }

        if (responseDataBytes == null) {
            responseDataBytes = responseData.getBytes();
        }

        request.sendResponseHeaders(statusCode, responseDataBytes.length);
        OutputStream os = request.getResponseBody();
        os.write(responseDataBytes);
        os.close();
    }

    private static HashMap<String, String> parseURLParameters(String data) {
        HashMap<String, String> args = new HashMap<>();
        String[] list = data.split("&");
        for (String l : list) {
            String[] pair = l.split("=");
            // Expect to get key-value pair
            if (pair.length == 2) {
                pair[0] = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                pair[1] = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                args.put(pair[0], pair[1]);
            } else {
                System.out.println("ERROR: Could not parse arg: " + l);
            }
        }
        return args;
    }

    private static String getPostData(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream ios = exchange.getRequestBody();
        int i;
        while ((i = ios.read()) != -1) {
            sb.append((char) i);
        }
        return sb.toString();
    }

    private static String generateNewAuthToken(String username) {
        String token = Utils.generateRandomBytes();
        authTokenCache.put(username, new Pair<>(token, System.currentTimeMillis()));
        return token;
    }

    /* The token is valid only 120 seconds */
    private static boolean isTokenValid(String username, String token) {
        if (!authTokenCache.containsKey(username)) {
            return false;
        }
        var pair = authTokenCache.get(username);
        if (!pair.getFirst().equals(token)) {
            return false;
        }
        long diffTimeInSeconds = (System.currentTimeMillis() - pair.getSecond()) / 1000;
        return diffTimeInSeconds > 0 && diffTimeInSeconds < tokenValidTimeInSeconds;
    }
}
