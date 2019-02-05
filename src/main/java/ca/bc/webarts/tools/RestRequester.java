/*
 *  $URL$
 *  $Author$
 *  $Revision$
 *  $Date$
 */
/*
 *
 *  Written by Tom Gutwin - WebARTS Design.
 *  Copyright (C) 2014 WebARTS Design, North Vancouver Canada
 *  http://www.webarts.ca
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without_ even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package ca.bc.webarts.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.lang.StringBuilder;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

//import android.util.Base64;
import org.apache.commons.codec.binary.Base64;

//import ca.bc.webarts.widgets.Util;


/** A class to encapsulate the calls to Restful Web Services. It is kept very basic with low overhead to live in android apps.
 **/
public class RestRequester
{
    protected static String CLASSNAME = "ca.bc.webarts.tools.RestRequester"; //ca.bc.webarts.widgets.Util.getCurrentClassName();
    public static final String LOG_TAG = CLASSNAME;
    protected static boolean debugOut_ = false;

    /**  A holder for this clients System File Separator.  */
    public final static String SYSTEM_FILE_SEPERATOR = File.separator;

    /**  A holder for this clients System line termination separator.  */
    public final static String SYSTEM_LINE_SEPERATOR =
            System.getProperty("line.separator");

    protected static String baseUrl_ = ""; // http://isy994
    public static boolean authenticating_ = true;
    protected static String username_ = "";
    protected static String password_ = "";
    protected static boolean acceptJSON_ = false;

    public void setUsername(String uName){username_=uName;}
    public void setPassword(String uPasswd){password_=uPasswd;}
    public void setBaseUrl(String url){baseUrl_=url;}
    public void setAcceptJSON(boolean acceptJson){acceptJSON_=acceptJson;}
    public String getUsername(){return username_;}
    public String getPassword(){return password_;}
    public String getBaseUrl(){return baseUrl_;}
    public boolean getAcceptJSON(){return acceptJSON_;}


    public RestRequester()
    {
    }


    public RestRequester(String baseUrl)
    {
        setBaseUrl( baseUrl);
        authenticating_=false;
    }


    public RestRequester(String baseUrl,String uName,String uPasswd)
    {
        setBaseUrl( baseUrl);
        authenticating_=true;
        setUsername( uName);
        setPassword( uPasswd);
    }


    public boolean isInit()
    {
        boolean retVal = true;
        if( baseUrl_.equals("") ||
                (authenticating_ &&
                        (username_.equals("") || password_.equals(""))
                )
        )
            retVal=false;
        return retVal;
    }


    /** Stitches together the URL that will get sent as the fuul service request. **/
    public String getServiceUrl(String serviceStr){return baseUrl_+serviceStr;}


    /** Sends the rest service GET request off and returns the results.
     * @param serviceName is the service (string) to append to the baseURL - example /rest/sys
     * @return the serviceResult as a stringBuilder, null if error
     **/
    public StringBuilder serviceGet(String  serviceName)
    { return callService(serviceName, true);}


    /** Sends the rest service POST request off and retruns the results.
     * @param serviceName is the service (string) to append to the baseURL - example /rest/sys
     * @return the serviceResult as a stringBuilder, null if error
     **/
    public StringBuilder servicePost(String  serviceName)
    { return callService(serviceName, false);}


    /** Sends the rest service request off and returns the results.
     * @param serviceName is the service (string) to append to the baseURL - example /rest/sys
     * @param getNotPost is a flag to tell this method to do a get or post based on this flag - true does a GET, false does a POST
     * @return the serviceResult as a stringBuilder, null if error
     **/
    public StringBuilder callService(String  serviceName, boolean getNotPost){ return callService(baseUrl_, serviceName, getNotPost);}
    public StringBuilder callService(String  baseUrl, String  serviceName, boolean getNotPost)
    {
        StringBuilder retVal = null;
        if(isInit())
            try
            {
                String usrlStr = (baseUrl+serviceName).replace(" " ,"%20");
                URL url = new URL(usrlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if(getNotPost)
                    conn.setRequestMethod("GET");
                else
                    conn.setRequestMethod("POST");
                if(acceptJSON_)
                    conn.setRequestProperty("Accept", "application/json");
                else
                    conn.setRequestProperty("Accept", "application/xml");

                //BASE64Encoder enc = new sun.misc.BASE64Encoder();
                String userpassword = username_ + ":" + password_;
                //String encodedAuthorization = android.util.Base64.encodeToString( userpassword.getBytes(), android.util.Base64.DEFAULT );
                String encodedAuthorization = new String(Base64.encodeBase64( (userpassword.getBytes()) ));
                conn.setRequestProperty("Authorization", "Basic "+ encodedAuthorization);

                if (debugOut_) System.out.println("callService to: "+usrlStr);
                if (debugOut_) System.out.println("         with : "+userpassword);
                if (conn.getResponseCode() == 200)
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    if (br!=null)
                    {
                        retVal = new StringBuilder();
                        String output;
                        if (debugOut_) System.out.println("Output from Server .... \n");
                        while ((output = br.readLine()) != null)
                        {
                            if (debugOut_) System.out.println(output);
                            retVal.append(output);
                            retVal.append("\n");
                        }
                    }
                } // valid http response code
                else
                {
                    if (debugOut_) System.out.println("\n*!*! Rest Connection error: "+conn.getResponseCode());
                }
                conn.disconnect();
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        return retVal;
    }


    /** Indents/spaces out an XML result. **/
    public StringBuilder responseIndenter(StringBuilder sb)
    {
        StringBuilder retVal = new StringBuilder("");
        if (sb!=null)
        {
            int indent = -1;
            boolean opening = false;
            boolean closing = false;
            boolean lf = false;
            char [] sbChar = sb.toString().toCharArray();

            for (int i=0; i< sbChar.length;i++)
            {
                opening = false;
                closing = false;
                lf = false;
                if ((sbChar[i]=='<'&&sbChar[i+1]=='/') )
                {
                    retVal.append("\n");
                    for (int j=0;j<indent;j++) retVal.append("  ");
                    retVal.append(sbChar[i]);
                    indent--; //indent--;
                }
                else if(sbChar[i]=='<')
                {
                    indent++;
                    retVal.append("\n");
                    for (int j=0;j<indent;j++) retVal.append("  ");
                    retVal.append(sbChar[i]);
                }
                else if ((sbChar[i]=='/'&&sbChar[i+1]=='>') )
                {
                    indent--; //indent--;
                    retVal.append(sbChar[i]);
                }
                else if (sbChar[i]=='>')
                {
                    retVal.append(sbChar[i]);
                    //for (int j=0;j<indent;j++) retVal.append("  ");
                }
                else if (sbChar[i]!='\n')
                {
                    retVal.append(sbChar[i]);
                }
            }
        }
        return retVal;
    }

}