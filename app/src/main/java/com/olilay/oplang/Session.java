package com.olilay.oplang;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by Oliver Layer on 20.08.2015.
 */
public class Session extends AsyncTask<String, Void, HttpCookie>
{
    private HttpCookie Cookie;

    private boolean checkForTeacher;
    private LoginActivity loginActivity;


    public Session(String loginName, String loginPassword, boolean checkForTeacher, LoginActivity loginActivity)
    {
        this.loginActivity = loginActivity;
        this.checkForTeacher = checkForTeacher;

        execute(loginName, loginPassword);
    }


    protected HttpCookie doInBackground(String... loginData)
    {
        try
        {
            return Create(loginData[0], loginData[1]);
        }
        catch (UnknownHostException ue)
        {
            //no internet connection or website down
            return null;
        }
        catch (Exception e)
        {
            new ExceptionSender(e);
            return null;
        }
    }

    protected void onPostExecute(HttpCookie cookie)
    {
        if(cookie != null)
        {
            Cookie = cookie;

            if(checkForTeacher)
            {
                Log.v("OPlanG", "Checking if user is teacher...");
                new AccessChecker(getCookie(), loginActivity);
            }
            else
            {
                Log.v("OPlanG", "Not checking if user is teacher...");
                loginActivity.onLoginCompleted();
            }
        }
        else
        {
            loginActivity.onLoginFailed();
        }
    }


    private HttpCookie Create(String loginName, String loginPassword) throws IOException
    {
        loginName = loginName.replace(' ', '+');
        loginPassword = loginPassword.replace(' ', '+');

        String postString = "username=" + loginName + "&" + "password=" + loginPassword;
        byte[] data = postString.getBytes("UTF-8");

        HttpURLConnection connection = prepareConnection(new URL("https://secure.gymnasium-pullach.de/index.php"), data.length);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.write(data, 0, data.length);
        wr.close();

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        while((line = rd.readLine()) != null)
        {
            response.append(line);
            response.append('\r');
        }
        rd.close();

        String result = response.toString();

        if(result.contains("Fehler"))
        {
            return null; //Login failed
        }
        else
        {
            String[] values = connection.getHeaderFields().get("Set-Cookie").get(1).split(";");
            values = values[0].split("=");

            return new HttpCookie("PHPSESSID", values[1]);
        }
    }


    private HttpURLConnection prepareConnection(URL url, int dataLength) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", Integer.toString(dataLength));
        connection.setRequestProperty("Content-Language", "de-DE");

        connection.setDoOutput(true);

        return connection;
    }



    public String getCookie()
    {
        return Cookie.getName() + "=" + Cookie.getValue();
    }

}
