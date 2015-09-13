package com.danielan.crossfitlouvrebooking.connections;

import com.danielan.crossfitlouvrebooking.holder.Creneau;
import com.danielan.crossfitlouvrebooking.parser.CrossfitLouvreParser;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Daniel AN on 01/09/2015.
 */
public class HttpURLConnectionUtil {
    private List<String> cookies;
    private HttpURLConnection conn;
    private String mLogin;
    private String mPassword;
    private String mEmail;
    private String mMembreId;
    private String mCompteId;
    private String mLoginUrl = "http://www.multiresa.fr/~reebok2/index.php/creation-de-compte/login.html";
    private CrossfitLouvreParser mParser;

    private final String USER_AGENT = "Mozilla/5.0";

/*    public static void main(String[] args) throws Exception {
        HttpURLConnectionUtil http = new HttpURLConnectionUtil("SierraTangoSix", "Darkmoon93");

        System.out.println(http.getWeeklyScheduleContentForType("43"));
    }*/

    public HttpURLConnectionUtil(String login, String password) {
        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());
        this.mLogin = login;
        this.mPassword = password;
        setAccountParameters();
    }

    private void setAccountParameters() {
        connectToUrl(mLoginUrl, mLogin, mPassword);
        String content = GetPageContent("http://www.multiresa.fr/~reebok2/app/resa.php?format=html");
        this.mMembreId = content.split("idMembre")[1];
        Pattern pattern = Pattern.compile("\\d+");
        Matcher m = pattern.matcher(this.mMembreId);
        while (m.find()) {
            this.mMembreId = m.group();
            break;
        }
        this.mCompteId = content.split("idcompte")[1];
        m = pattern.matcher(this.mCompteId);
        while (m.find()) {
            this.mCompteId = m.group();
            break;
        }
        this.mEmail = content.split("mailMembre")[1];
        this.mEmail = this.mEmail.substring(1, this.mEmail.indexOf("&"));
        logout();
    }

    public Map<String, TreeSet<Creneau>> getWeeklyScheduleContentForType(String type) {
        Date date = new Date();
        String modifiedDate = new SimpleDateFormat("dd-MM-yyyy").format(date);
        String url = "http://www.multiresa.fr/~reebok2/app/req/reloadResa.php?isreload=1&idcompte=" + this.mCompteId + "&idMembre=" + this.mMembreId + "&mailMembre=" + this.mEmail + "&activite=" + type + "&zedate=" + modifiedDate + "&typecreno=1&callback=jQuery164027745886892080307_1441565923041&_=1441566047240";
        System.out.println(this.mLogin);
        System.out.println(this.mPassword);
        connectToUrl(mLoginUrl, this.mLogin, this.mPassword);
        String test = GetPageContent("http://www.multiresa.fr/~reebok2/app/resa.php?format=html");
        String schedule = GetPageContent(url);
        return mParser.parseWeeklyContent(schedule);
    }

    private void connectToUrl(String url, String login, String password) {
        // 1. Send a "GET" request, so that you can extract the form's data.
        String page = GetPageContent(url);
        String postParams = null;
        try {
            postParams = getFormParams(page, login, password);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 2. Construct above post's content and then send a POST request for
        // authentication
        sendPost(url, postParams);
    }

    private void logout() {
        GetPageContent("http://www.multiresa.fr/~reebok2/index.php/creation-de-compte/logout.html");
    }

    private void sendPost(String url, String postParams) {
        URL obj = null;
        try {
            obj = new URL(url);

            conn = (HttpURLConnection) obj.openConnection();
            // Acts like a browser
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("op2", "login");
            conn.setRequestProperty("lang", "french");
            conn.setRequestProperty("force_session", "1");
            conn.setRequestProperty("return", "B:aHR0cDovL3d3dy5tdWx0aXJlc2EuZnIvfnJlZWJvazIvaW5kZXgucGhwL2plLXJlc2VydmUuaHRtbC5odG1s");
            conn.setRequestProperty("message", "0");
            conn.setRequestProperty("loginfrom", "loginmodule");
            conn.setRequestProperty("cbsecuritym3", "cbm_03460b6c_7d3ea35e_8169b84abfb1a638af774ce671301440");
            //conn.setRequestProperty("Host", "accounts.google.com");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            //conn.setRequestProperty("Accept",
            //        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            //conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            if (this.cookies != null) {
                for (String cookie : this.cookies) {
                    conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
                }
            }
            conn.setRequestProperty("Connection", "keep-alive");
            //conn.setRequestProperty("Referer", "https://accounts.google.com/ServiceLoginAuth");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

            conn.setDoOutput(true);
            conn.setDoInput(true);

            // Send post request
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + postParams);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String GetPageContent(String url) {
        try {
            URL obj = new URL(url);
            conn = (HttpURLConnection) obj.openConnection();

            // default is GET
            conn.setRequestMethod("GET");

            conn.setUseCaches(false);

            // act like a browser
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            if (cookies != null) {
                for (String cookie : this.cookies) {
                    conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
                }
            }
            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Get the response cookies
            setCookies(conn.getHeaderFields().get("Set-Cookie"));

            return response.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getFormParams(String html, String username, String password)
            throws UnsupportedEncodingException {

        System.out.println("Extracting form's data...");

        Document doc = Jsoup.parse(html);

        // Google form id
        Element loginform = doc.getElementById("mod_loginformbox1");
        Elements inputElements = loginform.getElementsByTag("input");
        List<String> paramList = new ArrayList<String>();
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if (key.equals("username"))
                value = username;
            else if (key.equals("passwd"))
                value = password;
            paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }

        // build parameters list
        StringBuilder result = new StringBuilder();
        for (String param : paramList) {
            if (result.length() == 0) {
                result.append(param);
            } else {
                result.append("&" + param);
            }
        }
        return result.toString();
    }

    public List<String> getCookies() {
        return cookies;
    }

    public void setCookies(List<String> cookies) {
        this.cookies = cookies;
    }
}
