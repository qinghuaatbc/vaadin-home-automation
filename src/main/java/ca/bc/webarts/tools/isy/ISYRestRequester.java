
/*
 *  $URL$
 *  $Author$
 *  $Revision$
 *  $Date$
 */
/*
 *
 *  Written by Tom Gutwin - WebARTS Design.
 *  Copyright (C) 2014-2016 WebARTS Design, North Vancouver Canada
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

package ca.bc.webarts.tools.isy;

import java.io.IOException;
import java.lang.Integer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

import ca.bc.webarts.widgets.Quick;
import ca.bc.webarts.tools.RestRequester;

import org.apache.commons.codec.binary.Base64;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.Serializer;


/**
 * This class wraps the communication to the REST interface of a
 * <a href="http://www.universal-devices.com">UDI ISY-994</a>.
 * It provides many prebuilt java methods that wrap a specific REST call or you can request any one that
 * is specified in the REST API -
 * <a href="http://wiki.universal-devices.com/index.php?title=ISY_Developers:API:REST_Interface">
 * http://wiki.universal-devices.com/index.php?title=ISY_Developers:API:REST_Interface</a>.<br />
 *  Written by Tom Gutwin - WebARTS Design.<br />
 *  Copyright &copy; 2014-2016 WebARTS Design, North Vancouver Canada<br />
 *  <a href="http://www.webarts.ca">http://www.webarts.ca</a>
 *
 * @author  Tom B. Gutwin
 **/


public class ISYRestRequester extends RestRequester
{
    //    protected static final String CLASSNAME = "ISYRestRequester"; //ca.bc.webarts.widgets.Util.getCurrentClassName();
    protected static final String CLASSNAME = "ca.bc.webarts.tools.isy.ISYRestRequester"; //ca.bc.webarts.widgets.Util.getCurrentClassName();
    public static final String LOG_TAG = "\n"+CLASSNAME; //+"."+ca.bc.webarts.android.Util.getCurrentClassName();

    /** DEFAULT ISY994 IP address to use: 192.168.1.89 .**/
    protected static final String DEFAULT_ISY994_IP = "192.168.1.89";
    /** DEFAULT ISY994 username to use: admin .**/
    protected static final String DEFAULT_ISY994_USERNAME = "admin";
    /** DEFAULT ISY994 password to use: admin .**/
    protected static final String DEFAULT_ISY994_PASSWORD = "admin";
    /** DEFAULT ISY994 rest URL to start the URL path: /rest .**/
    protected static final String DEFAULT_ISY994_REST_URL_PATHSTR = "/rest";

    protected static final String TOMS_ISY994_IP = "192.168.1.89";
    protected static final String TOMS_ISY994_USERNAME = "admin"; // not really
    protected static final String TOMS_ISY994_PASSWORD = "admin"; // not really

    protected static StringBuilder helpMsg_ = new StringBuilder(SYSTEM_LINE_SEPERATOR);
    protected static boolean debugOut_ = false;
    /** flag to indicate the use of TOMS_isy IP, userID, and password. **/
    protected static boolean tomsIsy_ = true;

    public static IsyNodes isyNodes_ = null;
    public static IsyVars isyIntVars_ = null;
    public static IsyVars isyStateVars_ = null;

    public static String [] ClimateCoverage = {
            "",
            "Areas of",
            "Brief",
            "Chance of",
            "Definite",
            "Frequent",
            "Intermittent",
            "Isolated",
            "Likely",
            "Numerous",
            "Occasional",
            "Patchy",
            "Periods of",
            "Slight chance of",
            "Scattered",
            "Nearby",
            "Widespread" };

    public static String [] ClimateIntensity = {
            "",
            "Very light",
            "Light",
            "Heavy",
            "Very heavy" };


    public static String [] ClimateWeatherConditions = {
            "",
            "Hail",
            "Blowing dust",
            "Blowing sand",
            "Mist",
            "Blowing snow",
            "Fog",
            "Frost",
            "Haze",
            "Ice Crystals",
            "Ice fog",
            "Ice pellets / sleet",
            "Smoke",
            "Drizzle",
            "Rain",
            "Rain showers",
            "Rain/snow mix",
            "Snow/sleet mix",
            "Wintry mix",
            "Snow",
            "Snow showers",
            "Thunderstorms",
            "Unknown Precipitation",
            "Volcanic ash",
            "Water spouts",
            "Freezing fog",
            "Freezing drizzle",
            "Freezing rain",
            "Freezing spray" };

    public static String [] ClimateCloudConditions = {
            "",
            "Clear	(0-7% of the sky)",
            "Fair/mostly sunny	(7-32%)",
            "Partly cloudy	(32-70%)",
            "Mostly cloudy	(70-95%)",
            "Cloudy" };


    /** The start path to use in therest URL. Over-ride this if you extend this class. **/
    protected String restUrlPath_ = DEFAULT_ISY994_REST_URL_PATHSTR;

    /**
     * Default constructor that authenticates the default ISY with the default user password (using the class vars)
     * UNLESS the tomsIsy_ class var is true to over-ride with TOMS _isy IP, userID, and password.
     * TOMS settings get 1st priority, and DEFAULTS if {@link  #tomsIsy_ tomsIsy_} class var is false.
     *
     * @see #DEFAULT_ISY994_IP
     * @see #DEFAULT_ISY994_USERNAME
     * @see #DEFAULT_ISY994_PASSWORD
     **/
    public ISYRestRequester()
    {
        authenticating_=true;
        setBaseUrl( "http://"+(tomsIsy_?TOMS_ISY994_IP:DEFAULT_ISY994_IP)+restUrlPath_);
        setUsername( (tomsIsy_?TOMS_ISY994_USERNAME:DEFAULT_ISY994_USERNAME));
        setPassword( (tomsIsy_?TOMS_ISY994_PASSWORD:DEFAULT_ISY994_PASSWORD));
    }


    /**
     * Default constructor that authenticates and connects the ISY with a choice of either the default user password
     * (using the class vars) or with TOMS _isy IP, userID, and password..
     *
     * @see #DEFAULT_ISY994_IP
     * @see #DEFAULT_ISY994_USERNAME
     * @see #DEFAULT_ISY994_PASSWORD
     **/
    public ISYRestRequester(boolean useDefault)
    {
        tomsIsy_=!useDefault;
        authenticating_=true;
        setBaseUrl( "http://"+(tomsIsy_?TOMS_ISY994_IP:DEFAULT_ISY994_IP)+restUrlPath_);
        setUsername( (tomsIsy_?TOMS_ISY994_USERNAME:DEFAULT_ISY994_USERNAME));
        setPassword( (tomsIsy_?TOMS_ISY994_PASSWORD:DEFAULT_ISY994_PASSWORD));
    }


    /**
     * Constructor to customize all connection settings.
     *
     **/
    public ISYRestRequester(String server, String user, String pass)
    {
        setBaseUrl( "http://"+server+restUrlPath_);
        authenticating_=true;
        setUsername(user);
        setPassword( pass);
    }


    /**
     * Set Method for class field {@link  #tomsIsy_ tomsIsy_}.
     *
     * @param tomsIsy is the value to set this class field to.
     *
     **/
    public static void setTomsIsy_(boolean tomsIsy)
    {
        tomsIsy_ = tomsIsy;
    }  // setTomsIsy Method


    /**
     * Get Method for class field 'tomsIsy_'.
     *
     * @return boolean - The value the class field {@link  #tomsIsy_ tomsIsy_}.
     *
     **/
    public static boolean getTomsIsy()
    {
        return tomsIsy_;
    }  // getTomsIsy Method


    /**
     * Set Method for class field 'restUrlPath_'.
     *
     * @param restUrlPath_ is the value to set this class field to.
     *
     **/
    public  void setRestUrlPath(String restUrlPath)
    {
        restUrlPath_ = restUrlPath;
    }  // setRestUrlPath Method


    /**
     * Get Method for class field 'restUrlPath_'.
     *
     * @return String - The value the class field 'restUrlPath_'.
     *
     **/
    public String getRestUrlPath()
    {
        return restUrlPath_;
    }  // getRestUrlPath Method


    /** Check connectivity to the ISY specified by the class parms.
     * @return true or false
     **/
    public boolean canConnect()
    {
        if(debugOut_) System.out.println(LOG_TAG+".canConnect("+getBaseUrl()+", "+getUsername()+", "+getPassword()+")");

        boolean retVal = false;
        if(isInit())
        {
            try
            {
                if(debugOut_) System.out.println(LOG_TAG+".init = true");
                String usrlStr = (baseUrl_+"/sys").replace(" " ,"%20");
                URL url = new URL(usrlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                if(acceptJSON_)
                    conn.setRequestProperty("Accept", "application/json");
                else
                    conn.setRequestProperty("Accept", "application/xml");

                if (authenticating_)
                {
                    //BASE64Encoder enc = new sun.misc.BASE64Encoder();
                    String userpassword = username_ + ":" + password_;
                    //String encodedAuthorization = android.util.Base64.encodeToString( userpassword.getBytes(), android.util.Base64.DEFAULT );
                    String encodedAuthorization = new String(Base64.encodeBase64( (userpassword.getBytes()) ));
                    conn.setRequestProperty("Authorization", "Basic "+ encodedAuthorization);
                }

                if (conn.getResponseCode() == 200)
                {
                    retVal=true;
                } // valid http response code
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
        }
        return retVal;
    }


    /** Sends a REST call to query all nodes and scenes; '/nodes'  . **/
    public StringBuilder queryAllNodes()
    {
        return serviceGet("/nodes");
    }


    /** Sends a REST call to query all scenes; '/nodes/scenes'  . **/
    public StringBuilder queryAllScenes()
    {
        return serviceGet("/nodes/scenes");
    }


    /** Sends a REST call to query all scenes; '/nodes/scenes'  . **/
    public StringBuilder queryAllNodesAndScenes()
    {
        return queryAllNodes().append(queryAllScenes());
    }


    /**
     * Parses all the NODEs in the ISY into the isyNodes_ var.  If not already parsed, it sends a REST call
     * to query all nodes abd scenes; '/nodes'  and parses the results into the class object to preapre for use.
     **/
    public IsyNodes parseAllNodes(){ return parseAllNodes(false);}
    /**
     * Parses all the NODEs in the ISY into the isyNodes_ var.  If not already parsed, it sends a REST call
     * to query all nodes; '/nodes'  and parses the results into the class object to preapre for use.
     *
     * @param reloadFromISY forces the re- query from the isy (else it just returns the existing isyNodes_.
     **/
    public IsyNodes parseAllNodes(boolean reloadFromISY)
    {
        IsyNodes retVal = isyNodes_;
        if(reloadFromISY || retVal==null)
        {
            String s = "";
            try
            {
                s = queryAllNodes().toString();
                s.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","");
                retVal = new IsyNodes(s);
                isyNodes_ = retVal;
            }
            catch( Exception ex)
            {
                // leave it as null
                ex.printStackTrace();

            }
        }
        if(debugOut_) System.out.println("Parsed Nodes:\n"+ isyNodes_);
        return retVal;
    }


    /** Sends a REST call to query all Variables defined; '/vars/definitions/<varType>'  . **/
    public StringBuilder queryAllVars(int varType)
    {
        parseAllNodes(); // fast return if already parsed
        return serviceGet("/vars/definitions/"+varType);
    }


    /** Sends a REST call to query all Variables values; 'vars/get/<varType>'  . **/
    public StringBuilder queryAllVarValues(int varType)
    {
        parseAllNodes(); // fast return if already parsed
        return serviceGet("/vars/get/"+varType);
    }


    /** Fully Queries and loads all Integer vars. **/
    public StringBuilder queryAllIntegerVars()
    {
        parseAllNodes(); // fast return if already parsed
        StringBuilder retVal = queryAllVars(IsyVars.VAR_TYPE_INT);
        StringBuilder vVals = queryAllVarValues(IsyVars.VAR_TYPE_INT);
        try
        {
            isyIntVars_ = new IsyVars(IsyVars.VAR_TYPE_INT, retVal, vVals) ;
        }
        catch (Exception ex)
        {
            retVal = null;
            isyIntVars_ = null;
            System.out.println("ERROR  querying Integer Vars");
            ex.printStackTrace();
        }
        return retVal;
    }


    /** Fully Queries and loads all state vars. **/
    public StringBuilder queryAllStateVars()
    {
        parseAllNodes(); // fast return if already parsed
        StringBuilder retVal = queryAllVars(IsyVars.VAR_TYPE_STATE);
        StringBuilder vVals = queryAllVarValues(IsyVars.VAR_TYPE_STATE);
        try
        {
            isyStateVars_ = new IsyVars(IsyVars.VAR_TYPE_STATE, retVal, vVals) ;
        }
        catch (Exception ex)
        {
            retVal = null;
            isyStateVars_ = null;
            System.out.println("ERROR  querying state Vars");
            ex.printStackTrace();
        }
        return retVal;
    }


    /** Returns class var 'isyIntVars_'.
     *
     * @return the class IsyVars var holding Integers.
     **/
    public IsyVars getIsyIntVars()
    {
        parseAllNodes(); // fast return if already parsed
        if(isyIntVars_==null) queryAllIntegerVars();
        return isyIntVars_;
    }


    /** Returns class var 'isyStateVars_'.
     *
     * @return the class IsyVars var holding the State Vars.
     **/
    public IsyVars getIsyStateVars()
    {
        parseAllNodes(); // fast return if already parsed
        if(isyStateVars_==null)queryAllStateVars();
        return isyStateVars_;
    }


    /** Not Implemented. **/
    public StringBuilder queryAllIntVarVals()
    {
        parseAllNodes(); // fast return if already parsed
        StringBuilder retVal = null;
        return retVal;
    }


    /**
     * Sends a REST call turn a Node or scene ON '/nodes/<addressOfNodeName>/cmd/DON' .
     *
     * @param nodeName the nodes NAME to turn ON
     **/
    public StringBuilder deviceOn(String nodeName)
    {
        parseAllNodes(); // fast return if already parsed
        String addr = isyNodes_.getNodeAddress(nodeName);
        if(debugOut_) System.out.println(LOG_TAG+".deviceOn "+nodeName+" "+"("+addr+")");
        return deviceAddressOn(addr);
    }


    /**
     * Sends a REST call turn a Node or scene OFF '/nodes/<addressOfNodeName>/cmd/DOF' .
     *
     * @param nodeName the nodes NAME to turn OFF
     **/
    public StringBuilder deviceOff(String nodeName)
    {
        parseAllNodes(); // fast return if already parsed
        String addr = isyNodes_.getNodeAddress(nodeName);
        if(debugOut_) System.out.println(LOG_TAG+".deviceOff "+nodeName+" "+"("+addr+")");
        return deviceAddressOff(addr);
    }


    /**
     * Sends a REST call turn a Node or scene ON '/nodes/<addressOfNodeName>/cmd/DON' .
     *
     * @param addr the nodes address (ie. 16 10 A0 1) to turn OFF
     **/
    public StringBuilder deviceAddressOn(String addr)
    {
        parseAllNodes(); // fast return if already parsed
        return serviceGet("/nodes/"+addr+"/cmd/DON");
    }


    /**
     * Sends a REST call turn a Node ON '/nodes/<addressOfNodeName>/cmd/DON' .
     *
     * @param addr the nodes address (ie. 16 10 A0 1) to turn On
     * @param brightness the brightness (0-255)
     **/
    public StringBuilder deviceAddressOn(String addr, int brightness)
    {
        parseAllNodes(); // fast return if already parsed
        if(brightness<0) brightness=0;
        else if(brightness>255) brightness=255;

        return serviceGet("/nodes/"+addr+"/cmd/DON/"+brightness);
    }


    /**
     * Sends a REST call turn a Node OFF '/nodes/<addressOfNodeName>/cmd/DOF' .
     *
     * @param addr the nodes address (ie. 16 10 A0 1) to turn OFF
     **/
    public StringBuilder deviceAddressOff(String addr)
    {
        parseAllNodes(); // fast return if already parsed
        return serviceGet("/nodes/"+addr+"/cmd/DOF");
    }


    /**
     * Toggles the state of the device/node NAMED nodeName.
     *
     * @param nodeName the nodes NAME to toggle its state
     **/
    public int deviceToggle(String nodeName)
    {
        parseAllNodes(); // fast return if already parsed
        String addr = isyNodes_.getNodeAddress(nodeName);
        if(debugOut_) System.out.println(LOG_TAG+".deviceToggle("+nodeName+")");
        //ca.bc.webarts.widgets.Util.sleep(400);
        return deviceAddressToggle(addr);
    }


    /** Toggles the ON/OFF state specified device. **/
    public int deviceAddressToggle(String addr)
    {
        parseAllNodes(); // fast return if already parsed
        int retVal = -1;
        StringBuilder restResponse = null;
        int offLevel = 5;
        if(debugOut_) System.out.println(LOG_TAG+".deviceAddressToggle("+addr+")");
        int stat = deviceAddressStatus(addr);
        if(debugOut_) System.out.println(LOG_TAG+".deviceAddressToggle stat="+stat);
        if (stat<offLevel) restResponse = deviceAddressOn(addr);
        else restResponse = deviceAddressOff(addr);

        boolean success = false;
        if(restResponse!=null && restResponse.toString().indexOf("succeeded=\"true\"><status>200</status>")!=-1) success = true;
        if (success && stat<offLevel) retVal = 100;
        else if (success) retVal = 0;

        // now to make sure the on level is correct
        if(retVal!=-1) retVal = deviceAddressStatus(addr);
        return retVal;
    }


    /**
     * Queries the status of the device/node NAMED nodeName.
     *
     * @param nodeName the nodes NAME to query its status
     **/
    public int deviceStatus(String nodeName)
    {
        parseAllNodes(); // fast return if already parsed
        int retVal = -1;
        String addr = isyNodes_.getNodeAddress(nodeName);
        return deviceAddressStatus(addr);
    }


    public int deviceAddressStatus(String addr){ return deviceAddressStatus(addr, true);}
    /** returns the on/off/% for a specified node address.
     * @return on=100, off=0, or any number in between 0-100
     **/
    public int deviceAddressStatus(String addr, boolean includeScenes)
    {
        parseAllNodes(); // fast return if already parsed
        int retVal = -1;
        StringBuilder resp =  serviceGet("/status/"+addr);
        /* The response holds and attribute called formatted that can be one of Off, On , %   */
        /*  <property id="ST" value="114" formatted="45" uom="%/on/off"/>   */
        //System.out.println("  DEBUG1   respStr: "+resp.toString());
        if (resp!=null)
        {
            String respStr = resp.toString();
            int fSpot = respStr.indexOf("formatted");
            String fVal = respStr.substring(fSpot+11,respStr.indexOf("\"", fSpot+12));
            if(debugOut_) System.out.println("STATUS: "+ addr+": "+fVal+"\n"+resp.toString());
            if(fVal.equalsIgnoreCase("Off") ) retVal = 0;
            else if (fVal.equalsIgnoreCase("On") ) retVal = 100;
            else if (fVal.trim().equals("") ) retVal = -1;
            else if (fVal.trim().indexOf(".")!=-1 )
                try{retVal = Integer.parseInt(fVal.substring(0,fVal.indexOf(".")));}
                catch(Exception ex){System.out.println("ERROR pulling the status value from "+fVal+" "+fVal.substring(0,fVal.indexOf(".")));}
            else
                try{retVal = Integer.parseInt(fVal);}
                catch(Exception ex){System.out.println("ERROR pulling the int status value from "+fVal);}
        }
        else if(includeScenes) // try it as a scene/group
            retVal = groupAddressStatus(addr);

        return retVal;
    }


    /** returns the on/off/% fora specified scene/group address.
     * @return on=100, off=0, or any number in between 0-100
     **/
    public int groupAddressStatus(String addr)
    {
        parseAllNodes(); // fast return if already parsed
        int retVal = -1;
        String respStr = "";
        String groupMembersXml = "";
        String addr2 = "";
        int fSpot = -1;
        String fVal = "";

        //StringBuilder resp =  serviceGet("/nodes/"+addr+"?members=true");
        /* The response holds the group members that can be one of Off, On , %   */
    /* <nodeInfo>
         <group flag="132">
           <address>36083</address>
           <name>UpUnderCabLights</name>
           <parent type="3">29673</parent>
           <deviceGroup>29</deviceGroup>
           <pnode>36083</pnode>
           <ELK_ID>F01</ELK_ID>
           <members>
             <link type="16">17 54 1 1</link>
             <link type="16">43 A2 FB 5</link>
           </members>
         </group>
       </nodeInfo> */
        // the isyNodes_ now has a method called getGroupMembers(sceneName) that can be used to check status
        String groupName = isyNodes_.getGroupName(addr);
        GroupMembers gMems = isyNodes_.getGroupMembers(groupName);
        String [] members = gMems.getMemberNodes();
        int memStats = 0;
        for(String memAddr : members)
        {
            memStats+=deviceAddressStatus(memAddr,false); // this could be recursive if not sent a false
        }
        if(members.length>0)
            retVal = memStats/members.length;
        else
            retVal = memStats;

    /*if (resp!=null)
    {
      respStr = resp.toString();
      System.out.println("  DEBUG2   respStr: "+respStr);
      groupMembersXml = respStr.substring(respStr.indexOf("<link type="), respStr.indexOf("</members>"));
      System.out.println("  DEBUG3   groupMembersXml: "+groupMembersXml);
      addr2 = groupMembersXml.substring(groupMembersXml.indexOf(">")+1, groupMembersXml.indexOf("<"));
      System.out.println("  DEBUG4   addr: "+addr2);
      retVal = deviceAddressStatus(addr2);
      if(debugOut_) System.out.println("STATUS: "+ addr+": "+retVal+"\n");
    }
    */

        return retVal;
    }


    /** Sets a ISY variable.
     * @param varName this will lookup the varID
     * @param varVal
     * @param varType send a 1 for Integer and 2 for State
     **/
  /*
  public StringBuilder setVariable(String varName, String varVal, int varType)
  {
    parseAllNodes(); // fast return if already parsed
    return setVariable(getVarId(varName), varVal, varType);
  }
  */

    /** Sets a ISY variable.
     * @param varName
     * @param varId
     * @param varType send a 1 for Integer and 2 for State
     **/
    public StringBuilder setVariable(int varId, String varVal, int varType)
    {
        StringBuilder sb = null;
        if (varId>0 && varVal!=null && (varType==1 || varType==2))
            sb = serviceGet("/vars/set/"+varType+"/"+varId+"/"+varVal);
        return sb;
    }


    /** Gets the current XML (StringBuffer) result for an ISY variable.
     * @param varID
     * @param varType send a 1 for Integer and 2 for State
     * @return the XML returned from the ISY for the var requested , OR null if incorrect varType specified.
     **/
    public StringBuilder getVariable(String varID, int varType)
    {
        StringBuilder sb = null;
        if (varID!=null && !varID.equals("") &&
                (varType==1 || varType==2))
            sb = serviceGet("/vars/get/"+varType+"/"+varID);
        return sb;
    }


    /** Gets the current XML (StringBuffer) result for an ISY variable.
     * @param varID
     * @param varType send a 1 for Integer and 2 for State
     * @return int current value
     **/
    public int getVariableIntValue(String varID, int varType) throws Exception
    {
        int retVal = 0;
        StringBuilder sb = null;
        if (varID!=null && !varID.equals("") &&
                (varType==1 || varType==2))
            sb = serviceGet("/vars/get/"+varType+"/"+varID);
        int valSpot = sb.toString().indexOf("<val>");
        if(valSpot!=-1)
        {
            String valStr = sb.toString().substring(valSpot+"<val>".length(),sb.toString().indexOf("</val>"));
            retVal = Integer.parseInt(valStr);
        }
        else throw new Exception((varType == 1?"Integer ":"State ")+"VarID: "+varID +" NOT found.");

        return retVal;
    }


    /** Gets a the next un=used/avaialbe varID.
     * if already parsed, this method will NOT re-query the vars unless told to.
     * @return an int for the next ID to use
     **/
    public int getNextAvailableStateVarID(){ return getNextAvailableStateVarID(false);}
    public int getNextAvailableStateVarID(boolean reQueryVars)
    {
        int retVal = 0;
        if(isyStateVars_==null || reQueryVars)
        {
            queryAllStateVars();
        }
        if(isyStateVars_!=null)
        {
            retVal = isyStateVars_.getNextUnusedID();
        }
        return retVal;
    }


    /** Gets a the next un=used/avaialbe varID.
     * if already parsed, this method will NOT re-query the vars unless told to.
     * @return an int for the next ID to use
     **/
    public int getNextAvailableIntVarID(){ return getNextAvailableIntVarID(false);}
    public int getNextAvailableIntVarID(boolean reQueryVars)
    {
        int retVal = 0;
        if(isyIntVars_==null || reQueryVars)
        {
            queryAllIntegerVars();
        }
        if(isyIntVars_!=null)
        {
            retVal = isyIntVars_.getNextUnusedID();
        }
        return retVal;
    }


    /** returns the status for all the nodes. **/
    public StringBuilder getStatus()
    {
        return serviceGet("/status");
    }


    /** queries all the nodes. **/
    public StringBuilder getQuery()
    {
        return serviceGet("/query");
    }


    /** queries the nodes with name.**/
    public StringBuilder getQueryNode(String nodeName)
    {
        String nAddr = isyNodes_.getNodeAddress(nodeName);
        return serviceGet("/query/"+nAddr);
    }


    /** queries the nodes with address.**/
    public StringBuilder getQueryNodeFromAddress(String nAddr)
    {
        return serviceGet("/query/"+nAddr);
    }


    /** returns all the nodes. **/
    public StringBuilder getNodes()
    {
        return serviceGet("/nodes");
    }


    /** returns isy config. **/
    public StringBuilder getConfig()
    {
        return serviceGet("/config");
    }


    /** returns a devices property Value (which is the unformatted value) String with /rest/nodes/<node>/<property> .
     *
     * @return the property value as an string
     **/
    public String devicePropertyValue(String nodeName, String prop)
    {
        parseAllNodes(); // fast return if already parsed
        String addr = isyNodes_.getNodeAddress(nodeName);
        IsyDeviceProperty devProp = deviceAddressProperty(addr, prop);
        return devProp.getValue();
    }


    /** returns a devices property formatted String with /rest/nodes/<node>/<property> .
     *
     * @return the property value as a String
     **/
    public String devicePropertyFormattedValue(String nodeName, String prop)
    {
        parseAllNodes(); // fast return if already parsed
        String addr = isyNodes_.getNodeAddress(nodeName);
        IsyDeviceProperty devProp = deviceAddressProperty(addr, prop);
        return devProp.getFormatted();
    }


    /** returns a devices property XML String with /rest/nodes/<node>/<property> .
     *
     * @return the property XML that gets returned from the rest call
     **/
    public String devicePropertyXML(String nodeName, String prop)
    {
        parseAllNodes(); // fast return if already parsed
        String addr = isyNodes_.getNodeAddress(nodeName);
        return deviceAddressPropertyXML(addr, prop);
    }


    /** returns a devices property XML String with /rest/nodes/<nodeID>/<property> .
     *
     * @return the property XML that gets returned from the rest call
     **/
    public String deviceAddressPropertyXML(String addr, String prop)
    {
        IsyDeviceProperty devProp = deviceAddressProperty(addr, prop);
        return devProp.getElementStr();
    }


    /** returns a devices property int value with /rest/nodes/<nodeID>/<property> .
     *
     * @return the property value as an int, or -1 if it is not an Integer type
     **/
    public int deviceAddressIntProperty(String addr, String prop)
    {
        IsyDeviceProperty devProp = deviceAddressProperty(addr, prop);
        return devProp.getIntValue();
    }


    /** returns a devices property int value with /rest/nodes/<nodeID>/<property> .
     *
     * @return the property value unit of measure (uom)
     **/
    public String deviceAddressPropertyUOM(String addr, String prop)
    {
        IsyDeviceProperty devProp = deviceAddressProperty(addr, prop);
        return devProp.getUom();
    }


    /** returns a devices property formatted String with /rest/nodes/<node>/<property> .
     *
     * @return the property formatted value as a String
     **/
    public IsyDeviceProperty deviceProperty(String nodeName, String prop)
    {
        parseAllNodes(); // fast return if already parsed
        String addr = isyNodes_.getNodeAddress(nodeName);
        return deviceAddressProperty(addr, prop);
    }


    /** returns a devices property with /rest/nodes/<nodeID>/<property> . **/
    public IsyDeviceProperty deviceAddressProperty(String addr, String prop)
    {
        int retVal = -1;
        String requestStr = "/nodes/" + addr +"/"+prop;
        if (debugOut_) System.out.println("Rest Reguest= "+getServiceUrl(requestStr));
        getQueryNodeFromAddress(addr); // this makkes sure the node status is up to date
        StringBuilder resp = serviceGet(requestStr);
        IsyDeviceProperty devProp = new IsyDeviceProperty(resp.toString());
        return devProp;
    }


    /**
     * Class main commandLine entry method that has a test command and some convienience commands, as well as a pure rest command.
     **/
    public static void main(String [] args)
    {
        final String methodName = CLASSNAME + ": main()";
        ISYRestRequester instance = new ISYRestRequester();
        IsyNodes isyNodes = instance.parseAllNodes(true);

        /* Simple way af parsing the args */
        if (args ==null || args.length<1)
            System.out.println(getHelpMsgStr());
            /* *************************************** */
        else
        {
            if (args[0].equalsIgnoreCase("test"))
            {
                instance.testCMD(args);
            }
            /* *************************************** */
            else if (args[0].equalsIgnoreCase("listNodes") || args[0].equalsIgnoreCase("nodes"))
            {
                instance.listNodesCMD(args);
            }
            /* *************************************** */
            else if (args[0].equalsIgnoreCase("listScenesAndNodes"))
            {
                instance.listScenesCMD(args);
                instance.listNodesCMD(args);
            }
            /* *************************************** */
            else if (args[0].equalsIgnoreCase("listScenes") || args[0].equalsIgnoreCase("scenes"))
            {
                instance.listScenesCMD(args);
            }
            /* *************************************** */
            else if (args[0].equalsIgnoreCase("getNodeAddress") && args.length>1)
            {
                try { System.out.println(args[1]+" : " +isyNodes.getNodeAddress(args[1]));}
                catch(Exception ex){ System.out.println("Error retrieving Node Address for : "+args[1]+"\n"+Arrays.toString(args));}
            }
            /* *************************************** */
            else if (args[0].equalsIgnoreCase("listVars"))
            {
                instance.listVarsCMD(args);
            }
            /* *************************************** */
            else if (args[0].equalsIgnoreCase("getVar"))
            {
                try { instance.getVarCMD(args);}
                catch(Exception ex){ System.out.println("Error retrieving VAR: "+Arrays.toString(args));}
            }
            /* *************************************** */
            else if (args[0].equalsIgnoreCase("getProperty"))
            {
                instance.getPropertyCMD(args);
            }
            /* *************************************** */
            else if (args[0].equalsIgnoreCase("toggle"))
            {
                instance.toggleCMD(args);
            }
            /* *************************************** */
            else if (args[0].equalsIgnoreCase("status"))
            {
                instance.statusCMD(args);
            }
            /* *************************************** */
            else
            {
                instance.restCMD(args);
            }
        }
    } // main


    /**
     * commandLine command executor method for the test Command.
     * @param args the array of commandLine args that got passed in
     **/
    protected void testCMD(String [] args)
    {
        final String methodName = CLASSNAME + ": testCMD(String [])";
        parseAllNodes(); // fast return if already parsed


        System.out.println("Testing ISY Rest Service: "+ "/sys");
        StringBuilder resp =  serviceGet("/sys");
        System.out.println(resp.toString()); System.out.println();
        System.out.println("Testing ISY Rest Service: "+ "Turn the Downstairs Kitchen Lights on: "+ isyNodes_.getNodeName(DWNKITCHEN_ADDR) +" ("+DWNKITCHEN_ADDR+")");
        resp = deviceAddressOn(DWNKITCHEN_ADDR);
        System.out.println(resp.toString());  System.out.println();
        //ca.bc.webarts.widgets.Util.sleep(400);
        System.out.println("Testing ISY Rest Service: "+ "checking status of the "+GAMESROOMLIGHTS_NODENAME);
        int devStatus = deviceStatus(GAMESROOMLIGHTS_NODENAME);
        System.out.println("GAMESROOMLIGHTS level:"+devStatus); System.out.println();

    }


    /**
     * commandLine command executor method for the listNodes Command.
     * @param args the array of commandLine args that got passed in
     **/
    protected void listNodesCMD(String [] args)
    {
        final String methodName = CLASSNAME + ": listNodesCMD(String [])";
        if (debugOut_) System.out.println("ISY Rest Services: "+ "listNodes");
        parseAllNodes(); // fast return if already parsed
        if (isyNodes_!=null)
        {
            String [] resp =  isyNodes_.getNodeNamesCopy(true); // true means sorted
            String [] resp2 =  isyNodes_.getGroupNamesCopy(true); // true means sorted
            if(false)
            {
                if (debugOut_) System.out.println("---- BEFORE SORT:");
                for(int i=0; i< resp.length;i++)
                    System.out.println(resp[i]+" ("+isyNodes_.getNodeTypeStr(resp[i])+"): "+isyNodes_.getNodeAddress(resp[i]));
                if (debugOut_) System.out.println("\n*******\n---- AFTER SORT:");
            }
            //Quick.sort(resp);
            if (resp!=null)
                for(int i=0; i< resp.length;i++)
                    System.out.println(resp[i]+" ("+isyNodes_.getNodeTypeStr(resp[i])+"): "+isyNodes_.getNodeAddress(resp[i]));
            if (resp2!=null)
                for(int i=0; i< resp2.length;i++)
                    System.out.println(resp2[i]+" : "+isyNodes_.getGroupAddress(resp2[i]));
        }
        else
            System.out.println("   ERROR Parsing isyNodes.");

    }


    /**
     * commandLine command executor method for the listScenes Command.
     * @param args the array of commandLine args that got passed in
     **/
    protected void listScenesCMD(String [] args)
    {
        final String methodName = CLASSNAME + ": listScenesCMD(String [])";
        if (debugOut_) System.out.println("ISY Rest Services: "+ "listScenes");
        parseAllNodes(); // fast return if already parsed
        if (isyNodes_!=null)
        {
            String [] resp =  isyNodes_.getGroupNamesCopy(true); // true means sorted
            //Quick.sort(resp);
            if (resp!=null)
                for(int i=0; i< resp.length;i++)
                    System.out.println(resp[i]+": "+isyNodes_.getSceneAddress(resp[i]));
        }
        else
            System.out.println("   ERROR Parsing isyNodes.");

    }


    /**
     * commandLine command executor method for the listVars Command.
     * @param args the array of commandLine args that got passed in
     **/
    protected void listVarsCMD(String [] args)
    {
        final String methodName = CLASSNAME + ": listVarsCMD(String [])";
        if (debugOut_) System.out.println("ISY Rest Services: "+ "listVars");
        queryAllIntegerVars();
        queryAllStateVars();
        //System.out.println(instance.isyIntVars_.toString());
        //System.out.println(instance.isyStateVars_.toString());

        if (isyIntVars_!=null)
        {
            System.out.println("\n"+isyIntVars_.getNumVars()+" INTEGER Vars");
            System.out.println("------------");
            String [] resp =  isyIntVars_.getVarNames();

            if (resp!=null)
            {
                Quick.sort(resp);
                for(int i=0; i< resp.length;i++)
                {
                    try{ System.out.println(resp[i]+": "+isyIntVars_.getVarValue(resp[i]));}
                    catch(Exception ex){System.out.println(resp[i]+": "+"NOT FOUND");}
                }
            }
        }
        else
            System.out.println("   ERROR Parsing Integer Vars.");

        if (isyStateVars_!=null)
        {
            System.out.println("\n"+isyStateVars_.getNumVars()+" STATE Vars");
            System.out.println("------------");
            String [] resp =  isyStateVars_.getVarNames();
            if (resp!=null)
            {
                Quick.sort(resp);
                for(int i=0; i< resp.length;i++)
                {
                    try{ System.out.println(resp[i]+": "+isyStateVars_.getVarValue(resp[i]));}
                    catch(Exception ex){System.out.println(resp[i]+": "+"NOT FOUND");}
                }
            }
        }
        else
            System.out.println("   ERROR Parsing State Vars.");

    }


    /**
     * commandLine command executor method for the toggle Command.
     * @param args the lightname to toggle
     **/
    public int toggleLight(String  arg)
    {
        String [] args = {"toggle", arg};
        return toggleCMD(args);
    }


    /**
     * commandLine command executor method for the toggle Command.
     * @param args the array of commandLine args that got passed in
     **/
    //public int toggleCMD(String [] args)
     protected int toggleCMD(String [] args)
    {
        final String methodName = CLASSNAME + ": toggleCMD(String [])";
        int devStatus = -1;
        if (args.length>1 && args[0].equals("toggle"))  parseAllNodes();
        if (isyNodes_!=null && args.length>1)
        {
            StringBuilder nName = new StringBuilder(args[1].trim());
            if (args.length>2) for (int i=2;i<args.length; i++) {nName.append(" ");nName.append(args[i]);}
            if ( isyNodes_.hasNodeName(nName.toString()) || isyNodes_.hasGroupName(nName.toString()))
            {
                String nAddr = isyNodes_.getNodeAddress( nName.toString());
                System.out.println("Toggling: "+ nName.toString()+" ( "+ nAddr+" )");
                devStatus = deviceAddressToggle(nAddr);
                //devStatus = deviceAddressStatus(nAddr);
                System.out.println(nName.toString().trim()+" level:"+devStatus);

            }
            else
                System.out.println("   ERROR can't find '"+args[1].trim()+"' in isyNodes");
        }
        else
            System.out.println("   ERROR Parsing isyNodes.");

        return devStatus;
    }


    /**
     * commandLine status command method for the status Command.
     * @param args the array of commandLine args that got passed in
     *
     * @param args is an array of stringas that get concatenated into the single NODE name to get status for.
     **/
    protected void statusCMD(String [] args)
    {
        final String methodName = CLASSNAME + ": statusCMD(String [])";
        parseAllNodes(); // fast return if already parsed
        if (debugOut_) System.out.println("ISY Rest Services: "+ "node status");
        if (args.length>1 && args[0].equals("status")) parseAllNodes();
        if (isyNodes_!=null && args.length>1)
        {
            StringBuilder nName = new StringBuilder(args[1]);
            if (args.length>2) for (int i=2;i<args.length; i++) {nName.append(" ");nName.append(args[i]);}
            if ( isyNodes_.hasNodeName(nName.toString()) || isyNodes_.hasGroupName(nName.toString()))
            {
                String nAddr = isyNodes_.getNodeAddress( nName.toString());
                //System.out.println(" for node: "+nAddr.toString()+" ( "+ isyNodes.getNodeName( nAddr.toString()+" )"));
                int devStatus = deviceAddressStatus(nAddr);
                System.out.println("  "+nName.toString()+" ( "+ nAddr+" ) status is "+devStatus);
            }
            else
                System.out.println("   ERROR Can't find that node NAME = "+ nName);
        }
        else
            System.out.println("   ERROR Parsing node NAME ");
    }


    /**
     * commandLine command executor method for the default rest Command.
     * It treats each arg as a part of a single rest command and passes it along to the ISY.
     * @param args the array of commandLine args that got passed in
     **/
    protected void restCMD(String [] args)
    {
        final String methodName = CLASSNAME + ": restCMD(String [])";
        // Parse the command
        String allcommands = args[0];
        for (int i=1;i< args.length;i++) allcommands+=" "+args[i];
        if (debugOut_) System.out.print("Sending ISY Rest Service: "+allcommands);
        String passedCommand = (allcommands.startsWith(restUrlPath_+"/")?allcommands.substring(restUrlPath_.length()):allcommands);
        System.out.println(" ("+passedCommand+")");
        passedCommand = (passedCommand.startsWith("/")?passedCommand:"/"+passedCommand);
        StringBuilder resp =  serviceGet(passedCommand);
        if (resp!=null)
        {
            System.out.println(responseIndenter(resp).toString());
            System.out.println();
        }
        else
        {
            System.out.println("Response Error");
            System.out.println();
        }

    }


    protected int getVarCMD(String arg) throws Exception
    {
        String [] args = {arg};
        return getVarCMD(args);
    }


    /**
     * commandLine command executor method for the getVar Command.
     * @param args the array of commandLine args that got passed in
     **/
    protected int getVarCMD(String [] args) throws Exception
    {
        final String methodName = CLASSNAME + ": getVarCMD(String [])";
        parseAllNodes(); // fast return if already parsed
        int retVal = -999;
        if (debugOut_) System.out.println("ISY Rest Services: "+ "getVar");
        queryAllIntegerVars();
        queryAllStateVars();

        retVal = isyIntVars_.getVarValue(args[0]);
        System.out.println(args[0]+" = "+retVal);
        return retVal;
    }


    /**
     * commandLine command executor method for the getProperty Command.
     * @param args the array of commandLine args that got passed in
     **/
    protected String getPropertyValue(String nodeName, String propName)
    {
        final String methodName = CLASSNAME + ": getPropertyValue(String [])";
        String retVal = "";

        if(debugOut_) System.out.println("ISY Rest Services: "+ "node property");
        parseAllNodes();
        if (isyNodes_!=null && nodeName!=null && propName!=null
                && !"".equals(nodeName) && !"".equals(propName))
        {
            String nName = nodeName;
            String pName = propName;

            if(debugOut_) System.out.println("Looking for property '"+pName+"' on nodeName '"+nName+"'");
            IsyDeviceProperty isyProp = deviceProperty(nName, pName);
            String propElementStr = isyProp.getElementStr();
            if(debugOut_) System.out.println(nName);
            if(debugOut_) System.out.println(propElementStr);
            if(debugOut_) System.out.println("             Value="+isyProp.getValue());
            if(debugOut_) System.out.println("   Formatted Value="+isyProp.getFormatted()+" "+isyProp.getUom());
            if(debugOut_) System.out.println("     Integer Value="+isyProp.getIntValue());
            retVal = isyProp.getValue();
        }
        else
            System.out.println("ERROR on comnmandLine: getProperty command requires a nodeName and a propertyName");

        return retVal;
    }


    /**
     * commandLine command executor method for the getProperty Command.
     * @param args the array of commandLine args that got passed in
     **/
    protected String getPropertyCMD(String [] args)
    {
        final String methodName = CLASSNAME + ": getPropertyCMD(String [])";
        String retVal = "";

        if(debugOut_) System.out.println("ISY Rest Services: "+ "node property");
        if (args.length>2 && args[0].equalsIgnoreCase("getProperty")) parseAllNodes();
        if (isyNodes_!=null && args.length>2)
        {
            String nName = args[1];
            for (int i=2;i< args.length-1;i++) nName+=" "+args[i];
            String pName = args[args.length-1];

            if(debugOut_) System.out.println("Looking for property '"+pName+"' on nodeName '"+nName+"'");
            IsyDeviceProperty isyProp = deviceProperty(nName, pName);
            String propElementStr = isyProp.getElementStr();
            System.out.println(nName);
            System.out.println(propElementStr);
            System.out.println("             Value="+isyProp.getValue());
            System.out.println("   Formatted Value="+isyProp.getFormatted()+" "+isyProp.getUom());
            System.out.println("     Integer Value="+isyProp.getIntValue());
            retVal = isyProp.getValue();
        }
        else
            System.out.println("ERROR on comnmandLine: getProperty command requires a nodeName and a propertyName");

        return retVal;
    }


    /**
     * commandLine command executor method for the getProperty Command.
     * @param args the array of commandLine args that got passed in
     **/
    protected String getPropertyUOM(String [] args)
    {
        final String methodName = CLASSNAME + ": getPropertyCMD(String [])";
        String retVal = "";

        if(debugOut_) System.out.println("ISY Rest Services: "+ "node property");
        if (args.length>2 && args[0].equalsIgnoreCase("getProperty")) parseAllNodes();
        if (isyNodes_!=null && args.length>2)
        {
            String nName = args[1];
            for (int i=2;i< args.length-1;i++) nName+=" "+args[i];
            String pName = args[args.length-1];

            if(debugOut_) System.out.println("Looking for property '"+pName+"' on nodeName '"+nName+"'");
            IsyDeviceProperty isyProp = deviceProperty(nName, pName);
            String propElementStr = isyProp.getElementStr();
            System.out.println(nName);
            System.out.println(propElementStr);
            System.out.println("             Value="+isyProp.getValue());
            System.out.println("   Formatted Value="+isyProp.getFormatted()+" "+isyProp.getUom());
            System.out.println("     Integer Value="+isyProp.getIntValue());
            retVal = isyProp.getUom();
        }
        else
            System.out.println("ERROR on comnmandLine: getProperty command requires a nodeName and a propertyName");

        return retVal;
    }


    /**
     * Template method for future commandLine command executor methods.
     * @param args the array of commandLine args that got passed in
     **/
    protected void templateCMD(String [] args)
    {
        final String methodName = CLASSNAME + ": testCMD(String [])";

    }


    /** gets the help as a String.
     * @return the helpMsg in String form
     **/
    protected static String getHelpMsgStr() {return getHelpMsg().toString();}


    /** initializes and gets the helpMsg_
     class var.
     * @return the class var helpMsg_
     **/
    protected static StringBuilder getHelpMsg()
    {
        helpMsg_ = new StringBuilder(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("---  WebARTS "+CLASSNAME+" Class  -----------------------------------------------------");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("--- + $Revision$ $Date$ ---");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("-------------------------------------------------------------------------------");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("WebARTS ca.bc.webarts.tools.isy.ISYRestRequester Class");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("SYNTAX:");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("   java ");
        helpMsg_.append(CLASSNAME);
        helpMsg_.append(" command or {restCommand}");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("Available commands:");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("    test");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("    listNodes ");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("    listScenes ");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("    listScenesAndNodes ");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("    status noneName");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("    listVars");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("    getVar varName");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("    getProperty nodeName propertyName");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("    toggle nodeName");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("Available restCommands:");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("    see: http://wiki.universal-devices.com/index.php?title=ISY_Developers:API:REST_Interface");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("  Example: java ca.bc.webarts.android.ISYRestRequester /sys ");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);
        helpMsg_.append("---------------------------------------------------------");
        helpMsg_.append("----------------------");
        helpMsg_.append(SYSTEM_LINE_SEPERATOR);

        return helpMsg_;
    }

    public static final String TVDIM_ADDR = "42032";
    public static final String GAMESROOMLIGHTS_NODENAME = "Game"; // "26534";
    public static final String GAMESROOMLIGHTS_ADDR = "15 39 FB 1"; // "26534";
    public static final String DWNKITCHEN_NODENAME = "DwnKitchen";
    public static final String DWNKITCHEN_ADDR = "16 45 A4 1";
    public static final String THERMOSTAT_ADDR = "14 13 C6 1";
    public static final String FAMILYMAINLIGHTS_ADDR = "13 A8 32 1";
    public static final String VALENCEFRONTLIGHTS_ADDR = "16 43 93 1";
    public static final String VALENCESURROUNDLIGHTS_ADDR = "16 46 AE 1";
}


/* ********************************************************************* */

/** An object to represent a ISY Node with name, Address, type, folder, and group - to simply help with name lookups.
 *  NO rest requests or call to the ISy are initiated from this class.
 **/
class IsyDeviceProperty extends Object
{
    String id_ = "";
    String value_ = "";
    String formatted_ = "";
    String uom_ = "";
    boolean isInteger_ = false;
    int intValue_=-1;
    String elementStr_ = "";

    public IsyDeviceProperty()
    {
    }


    public IsyDeviceProperty(String id,String value,String formatted,String uom)
    {
        id_ = id;
        value_ = value;
        formatted_ = formatted;
        uom_ = uom;
    }


    /**
     * Reads in Property values from the the XML property element from the rest response.
     *
     *<pre>
     *        <property id="ST" value="0" formatted="Off" uom="%/on/off"/>
     *</pre>
     **/
    public IsyDeviceProperty(String properyXmlStr)
    {
        int valueVal = -1;
        int formattedVal = -1;
        /* Parse Out id */
        int iSpot = properyXmlStr.indexOf(" id");
        String iVal = properyXmlStr.substring(iSpot+5,properyXmlStr.indexOf("\"", iSpot+6));
        setId(iVal);

        /* Parse Out value */
        int vSpot = properyXmlStr.indexOf(" value");
        String vVal = properyXmlStr.substring(vSpot+8,properyXmlStr.indexOf("\"", vSpot+9));
        if(vVal.equalsIgnoreCase("Off") ) valueVal = 0;
        else if (vVal.equalsIgnoreCase("On") ) valueVal = 100;
        else if (vVal.trim().equals("") ) valueVal = -1;
        else if (vVal.trim().indexOf(".")!=-1 )
            try{valueVal = Integer.parseInt(vVal.substring(0,vVal.indexOf(".")));}
            catch(Exception ex){System.out.println("ERROR pulling the value from "+vVal+" "+vVal.substring(0,vVal.indexOf(".")));}
        else
            try{valueVal = Integer.parseInt(vVal);}
            catch(Exception ex){System.out.println("ERROR pulling the int value from "+vVal);}
        setValue(vVal);

        /* Parse Out formatted */
        int fSpot = properyXmlStr.indexOf(" formatted");
        String fVal = properyXmlStr.substring(fSpot+12,properyXmlStr.indexOf("\"", fSpot+13));
        if(fVal.equalsIgnoreCase("Off") ) formattedVal = 0;
        else if (fVal.equalsIgnoreCase("On") ) formattedVal = 100;
        else if (fVal.trim().equals("") ) formattedVal = -1;
        else if (fVal.trim().indexOf(".")!=-1 )
            try{formattedVal = Integer.parseInt(fVal.substring(0,fVal.indexOf(".")));}
            catch(Exception ex){System.out.println("ERROR pulling the formattedValue from "+fVal+" "+fVal.substring(0,fVal.indexOf(".")));}
        else
            try{formattedVal = Integer.parseInt(fVal);}
            catch(Exception ex){System.out.println("ERROR pulling the int vformattedValue from "+fVal);}
        setFormatted(fVal);
        if(formattedVal!=-1) setIntValue(formattedVal);

        /* Parse Out uom */
        int uSpot = properyXmlStr.indexOf(" uom");
        String uVal = properyXmlStr.substring(uSpot+6,properyXmlStr.indexOf("\"", uSpot+7));
        setUom(uVal);
    }


    public String getElementStr()
    {
        return "<property id=\""+getId()+"\" value=\""+getValue()+"\" formatted=\""+getFormatted()+"\" uom=\""+getUom()+"\"/>";
    }


    /**
     * Set Method for class field 'id_'.
     *
     * @param name_ is the value to set this class field to.
     *
     **/
    public  void setId(String id)
    {
        this.id_ = id;
    }  // setId Method


    /**
     * Get Method for class field 'id_'.
     *
     * @return String - The value the class field 'id_'.
     *
     **/
    public String getId()
    {
        return id_;
    }  // getId Method


    /**
     * Set Method for class field 'value_'.
     *
     * @param value_ is the value to set this class field to.
     *
     **/
    public  void setValue(String value)
    {
        this.value_ = value;
    }  // setValue Method


    /**
     * Get Method for class field 'value_'.
     *
     * @return String - The value the class field 'value_'.
     *
     **/
    public String getValue()
    {
        return value_;
    }  // getValue Method

    /**
     * Set Method for class field 'formatted_'.
     *
     * @param formatted_ is the value to set this class field to.
     *
     **/
    public  void setFormatted(String formatted)
    {
        this.formatted_ = formatted;
    }  // setFormatted Method


    /**
     * Get Method for class field 'formatted_'.
     *
     * @return String - The value the class field 'formatted_'.
     *
     **/
    public String getFormatted()
    {
        return formatted_;
    }  // getFormatted Method


    /**
     * Set Method for class field 'uom_'.
     *
     * @param uom_ is the value to set this class field to.
     *
     **/
    public  void setUom(String uom)
    {
        this.uom_ = uom;
    }  // setUom Method


    /**
     * Get Method for class field 'uom_'.
     *
     * @return String - The value the class field 'uom_'.
     *
     **/
    public String getUom()
    {
        return uom_;
    }  // getUom Method


    /**
     * Set Method for class field 'isInteger_'.
     *
     * @param isInteger_ is the value to set this class field to.
     *
     **/
    public  void setIsInteger(boolean isInteger)
    {
        this.isInteger_ = isInteger;
    }  // setIsInteger Method


    /**
     * Get Method for class field 'isInteger_'.
     *
     * @return boolean - The value the class field 'isInteger_'.
     *
     **/
    public boolean getIsInteger()
    {
        return isInteger_;
    }  // getIsInteger Method


    /**
     * Set Method for class field 'intValue_'.
     *
     * @param intValue_ is the value to set this class field to.
     *
     **/
    public  void setIntValue(int intValue)
    {
        this.intValue_ = intValue;
        if (intValue!=-1) setIsInteger(true);
    }  // setIntValue Method


    /**
     * Get Method for class field 'intValue_'.
     *
     * @return int - The value the class field 'intValue_'.
     *
     **/
    public int getIntValue()
    {
        return intValue_;
    }  // getIntValue Method

}


/* ********************************************************************* */

/** An object to represent a ISY Node with name, Address, type, folder, and group - to simply help with name lookups.
 *  NO rest requests or call to the ISy are initiated from this class.
 **/
class IsyNodes extends Object
{
    public static final int SWITCH_TYPE = 1;
    public static final int DIMMER_TYPE = 2;
    public static final int PLUG_TYPE = 3;
    public static final int SENSOR_TYPE = 4;
    public static final int THERMOSTAT_TYPE = 5;
    public static final int METER_TYPE = 6;
    Document nodeDoc_ = null;
    Element rootElem_ = null;
    Builder xmlBuilder_ = new Builder();
    String [] elementNames_ = {"root","folder","node","group"};
    String [] nodeNames_ = null;
    String [] nodeAddresses_  = null;
    int    [] nodeTypes_ = null;
    String [] folderNames_ = null;
    String [] folderAddresses_ = null;
    String [] groupNames_ = null;
    String [] groupAddresses_ = null;
    GroupMembers [] groupMembers_ = null;
    Elements folderElements_ = null;
    Elements nodeElements_ = null;
    Elements groupElements_ = null;

    public IsyNodes()
    {
    /*
    StringBuilder nodeXmlSB = ISYRestRequester.serviceGet("/nodes");
    if(nodeXmlSB!=null && nodeXmlSB.length()>7)
    {
      nodeDoc_ = parseXMLNodes(nodeXmlSB.toString());
    }
    */
    }


    public IsyNodes(String nodeXmlStr) throws ParsingException, IOException
    {
        if(nodeXmlStr!=null && nodeXmlStr.length()>7)
        {

            nodeDoc_ = parseXMLNodes(nodeXmlStr);
        }
    }


    public IsyNodes(StringBuilder nodeXmlSB) throws ParsingException, IOException
    {
        if(nodeXmlSB!=null && nodeXmlSB.length()>7)
        {
            nodeDoc_ = parseXMLNodes(nodeXmlSB.toString());
        }
    }


    public String toString()
    {
        String retVal = "";
        for (int i=0; i<nodeNames_.length; i++)
        {
            retVal+=nodeNames_[i];
            retVal+=" {";
            retVal+=nodeTypes_[i];
            retVal+=") : ";
            retVal+= getNodeAddress(nodeNames_[i]);
            retVal+="\n";
        }
        return retVal;
    }


    protected boolean hasNodeName(String nodeName)
    {
        boolean retVal = false;
        //System.out.println("Checking ");
        for(int i=0; i< nodeNames_.length;i++)
        {
            //System.out.print("    "+nodeNames_[i]);
            if(nodeNames_[i].equalsIgnoreCase(nodeName)) { retVal = true;i=nodeNames_.length;}
            //System.out.println("  "+retVal);
        }
        return retVal;
    }


    protected boolean hasGroupName(String groupName)
    {
        boolean retVal = false;
        //System.out.println("Checking ");
        for(int i=0; i< groupNames_.length;i++)
        {
            //System.out.print("    "+nodeNames_[i]);
            if(groupNames_[i].equalsIgnoreCase(groupName)) { retVal = true;i=groupNames_.length;}
            //System.out.println("  "+retVal);
        }
        return retVal;
    }

    protected boolean hasSceneName(String sceneName)
    {
        return hasGroupName(sceneName);
    }


    /** Returns the class var nodeNames_. **/
    protected String [] getNodeNames()
    {
        return nodeNames_;
    }


    /** Returns the class var nodeAddresses_. **/
    protected String [] getNodeAddresses()
    {
        return nodeAddresses_;
    }


    /** Returns a verbatim COPY of the class var nodeNames_. **/
    protected String [] getNodeNamesCopy(){ return getNodeNamesCopy(false);}
    /** Returns a verbatim COPY of the class var nodeNames_. **/
    protected String [] getNodeNamesCopy(boolean sorted)
    {
        String [] retVal = new String[nodeNames_.length];
        java.lang.System.arraycopy(nodeNames_, 0, retVal, 0, nodeNames_.length);
        if(sorted) Quick.sort(retVal);
        return retVal;
    }


    /** Returns a verbatim COPY of the class var sceneNames_. **/
    protected String [] getSceneNamesCopy(){ return getSceneNamesCopy(false);}
    /** Returns a verbatim COPY of the class var sceneNames_. **/
    protected String [] getSceneNamesCopy(boolean sorted)
    {
        return getGroupNamesCopy(sorted);
    }


    /** Returns a verbatim COPY of the class var nodeAddresses_. **/
    protected String [] getNodeAddressesCopy()
    {
        String [] retVal = new String[nodeAddresses_.length];
        java.lang.System.arraycopy(nodeAddresses_, 0, retVal, 0, nodeAddresses_.length);
        return retVal;
    }


    /** Returns a verbatim COPY of the class var groupAddresses_. **/
    protected String [] getSceneAddressesCopy()
    {
        return getGroupAddressesCopy();
    }


    /** Returns the class var folderNames_. **/
    protected String [] getFolderNames()
    {
        return folderNames_;
    }


    /** Returns a verbatim COPY of the class var folderNames_. **/
    protected String [] getFolderNamesCopy(){ return getNodeNamesCopy(false);}
    /** Returns a verbatim COPY of the class var folderNames_. **/
    protected String [] getFolderNamesCopy(boolean sorted)
    {
        String [] retVal = new String[folderNames_.length];
        java.lang.System.arraycopy(folderNames_, 0, retVal, 0, folderNames_.length);
        if(sorted) Quick.sort(retVal);
        return retVal;
    }


    /** Returns the class var groupNames_. **/
    protected String [] getGroupNames()
    {
        return groupNames_;
    }


    /** Returns a verbatim COPY of the class var groupNames_. **/
    protected String [] getGroupNamesCopy(){ return getNodeNamesCopy(false);}
    /** Returns a verbatim COPY of the class var groupNames_. **/
    protected String [] getGroupNamesCopy(boolean sorted)
    {
        String [] retVal = new String[groupNames_.length];
        java.lang.System.arraycopy(groupNames_, 0, retVal, 0, groupNames_.length);
        if(sorted) Quick.sort(retVal);
        return retVal;
    }


    /** Returns a verbatim COPY of the class var groupAddresses_. **/
    protected String [] getGroupAddressesCopy()
    {
        String [] retVal = new String[groupAddresses_.length];
        java.lang.System.arraycopy(groupAddresses_, 0, retVal, 0, groupAddresses_.length);
        return retVal;
    }


    protected Element getNodeWithName(String nodeName)
    {
        Element retVal = null;
        if (nodeNames_!=null && nodeNames_.length>0)
        {
            int i=0;
            while( retVal==null  && i<nodeNames_.length)
            {
                if(nodeNames_[i].equals(nodeName)) retVal=nodeElements_.get(i);
                i++;
            }
        }
        return retVal;
    }


    protected Element getSceneWithName(String sceneName)
    {
        Element retVal = null;
        if (groupNames_!=null && groupNames_.length>0)
        {
            int i=0;
            while( retVal==null  && i<groupNames_.length)
            {
                if(groupNames_[i].equals(sceneName)) retVal=groupElements_.get(i);
                i++;
            }
        }
        return retVal;
    }


    protected String getSceneAddress(String sceneName)  { return getGroupAddress(sceneName); }
    protected String getGroupAddress(String sceneName)
    {
        String retVal = "";
        if (groupNames_!=null && groupNames_.length>0)
        {
            //System.out.print("\n   >>> getGroupAddress( "+sceneName+" )  ");
            //System.out.println("\n       groupNames_.length="+groupNames_.length);
            int i=0;
            while( retVal.equals("") && i<groupNames_.length )
            {
                if(groupNames_[i].equalsIgnoreCase(sceneName))
                {
                    retVal=groupAddresses_[i];
                    //System.out.print(""+i+"] "+groupNames_[i]+"="+retVal);
                }
                i++;
            }
        }
        //System.out.print("\n");
        return retVal;
    }


    protected String getNodeAddress(String nodeName)
    {
        String retVal = "";
        if (nodeNames_!=null && nodeNames_.length>0)
        {
            //System.out.print("\n   >>> getNodeAddress( "+nodeName+" )  ");
            //System.out.println("\n       nodeNames_.length="+nodeNames_.length);
            int i=0;
            while( retVal.equals("") && i<nodeNames_.length )
            {
                if(nodeNames_[i].equalsIgnoreCase(nodeName))
                {
                    retVal=nodeAddresses_[i];
                    //System.out.print(""+i+"] "+nodeNames_[i]+"="+retVal);
                }
                i++;
            }
        }
        // not found in NODES, look in groups
        if("".equals(retVal))
        {
            retVal = getGroupAddress(nodeName);
        }
        //System.out.print("\n");
        return retVal;
    }


    protected int getNodeRef(String nodeName)
    {
        int retVal=-1;
        int i=0;
        if (nodeNames_!=null && nodeNames_.length>0)
        {
            //System.out.print("\n   >>> getNodeAddress( "+nodeName+" )  ");
            //System.out.println("\n       nodeNames_.length="+nodeNames_.length);
            while( retVal==-1 && i<nodeNames_.length )
            {
                if(nodeNames_[i].equalsIgnoreCase(nodeName))
                {
                    retVal=i;
                    //System.out.print(""+i+"] "+nodeNames_[i]+"="+retVal);
                }
                i++;
            }
        }
        return i;
    }


    /** gets the array ref num for the given scene Name.
     *
     * @return the ref num for the array OR -1 if scene not found
     **/
    protected int getGroupRef(String sceneName)
    {
        int retVal=-1;
        int i=0;
        if (groupNames_!=null && groupNames_.length>0)
        {
            //System.out.print("\n   >>> getGroupAddress( "+sceneName+" )  ");
            //System.out.println("\n       groupNames_.length="+groupNames_.length);
            while( retVal==-1 && i<groupNames_.length )
            {
                if(groupNames_[i].equalsIgnoreCase(sceneName))
                {
                    retVal=i;
                    //System.out.print(""+i+"] "+groupNames_[i]+"="+retVal);
                }
                i++;
            }
        }
        //System.out.print("\n");
        return retVal;
    }


    GroupMembers getGroupMembers(String sceneName)
    {
        GroupMembers retVal = null;
        if(getGroupRef(sceneName)>-1) retVal = groupMembers_[getGroupRef(sceneName)];
        return retVal;
    }


    protected int getNodeType(String nodeName)
    {
        int retVal = -1;
        if (nodeNames_!=null && nodeNames_.length>0)
        {
            int i=0;
            while( retVal==-1  && i<nodeNames_.length)
            {
                if(nodeNames_[i].equals(nodeName)) retVal=nodeTypes_[i];
                i++;
            }
        }
        return retVal;
    }


    protected String getNodeTypeStr(String nodeName)
    {
        String retVal = "";
        int t = getNodeType( nodeName);
        if (t==SWITCH_TYPE) retVal="SWITCH_TYPE";
        else if (t==DIMMER_TYPE) retVal="DIMMER_TYPE";
        else if (t==PLUG_TYPE) retVal="PLUG_TYPE";
        else if (t==SENSOR_TYPE) retVal="SENSOR_TYPE";
        else if (t==THERMOSTAT_TYPE) retVal="THERMOSTAT_TYPE";
        else if (t==METER_TYPE) retVal="METER_TYPE";
        return retVal;
    }


    protected String getNodeName(String nodeAddress)
    {
        String retVal = "";
        if (nodeAddresses_!=null && nodeAddresses_.length>0)
        {
            int i=0;
            while( retVal.equals("")  && i<nodeAddresses_.length)
            {
                if(nodeAddresses_[i].equals(nodeAddress)) retVal=nodeNames_[i];
                i++;
            }
        }
        return retVal;
    }


    protected String getGroupName(String sceneAddress) { return getSceneName(sceneAddress);}
    protected String getSceneName(String sceneAddress)
    {
        String retVal = "";
        if (groupAddresses_!=null && groupAddresses_.length>0)
        {
            int i=0;
            while( retVal.equals("")  && i<groupAddresses_.length)
            {
                if(groupAddresses_[i].equals(sceneAddress)) retVal=groupNames_[i];
                i++;
            }
        }
        return retVal;
    }


    /**
     * Reads in the XML Config file.
     *
     *<pre>
     *    <node flag="128">
     *      <address>1 81 CF 1</address>
     *      <name>HangingLight-E</name>
     *      <parent type="3">28566</parent>
     *      <type>1.7.55.0</type>
     *      <enabled>true</enabled>
     *      <deviceClass>0</deviceClass>
     *      <wattage>0</wattage>
     *      <dcPeriod>0</dcPeriod>
     *      <pnode>1 81 CF 1</pnode>
     *      <ELK_ID>C07</ELK_ID>
     *      <property id="ST" value="0" formatted="Off" uom="%/on/off" />
     *    </node>
     *</pre>
     **/
    protected nu.xom.Document parseXMLNodes(String nodeXmlStr) throws ParsingException, IOException
    {
        //String methodName = className_+"."+Util.getCurrentMethodName();
        //log_.startMethod(methodName);
        try
        {
            nodeDoc_ = xmlBuilder_.build(nodeXmlStr,null);
        }
        catch (nu.xom.ParsingException xomEx)
        {
            xomEx.printStackTrace();
            System.out.println("MAJor Error: parsing nodeXmlStr");
            System.out.println(nodeXmlStr);
            System.out.println("MAJor Error: parsing nodeXmlStr");
        }
        if (nodeDoc_!=null)
        {
            rootElem_ = nodeDoc_.getRootElement();
            // load the data
            if (rootElem_ != null && rootElem_ instanceof Element && rootElem_.getLocalName().equals("nodes"))
            {
                folderElements_ = rootElem_.getChildElements("folder");
                nodeElements_ = rootElem_.getChildElements("node");
                groupElements_ = rootElem_.getChildElements("group");

                Element currE = null;
                Element stateE = null;
                int numNodes = nodeElements_.size();
                if (nodeElements_!=null && nodeElements_.size()>0)
                {
                    nodeNames_ = new String[numNodes];
                    nodeAddresses_ = new String[numNodes];
                    nodeTypes_ = new int[numNodes];

                    //System.out.println("\nParsing Nodes:");
                    for (int i=0; i< numNodes; i++)
                    {
                        currE = null;
                        currE = nodeElements_.get(i);
                        if (currE !=null)
                        {
                            try
                            {
                                nodeNames_[i] = currE.getFirstChildElement("name").getValue();
                                nodeAddresses_[i] = currE.getFirstChildElement("address").getValue();

                                //System.out.println("  > "+nodeNames_[i]+"  "+nodeAddresses_[i]);

                                stateE = currE.getFirstChildElement("property");  // should be the 'ST' property
                                if(nodeNames_[i].toUpperCase().indexOf("PLUG")!=-1){ nodeTypes_[i] = PLUG_TYPE;}
                                else if(nodeNames_[i].toUpperCase().indexOf("SENSE")!=-1){nodeTypes_[i] = SENSOR_TYPE;}
                                else if(nodeNames_[i].contains("Thermostat")||
                                        (stateE!=null&&stateE.getAttribute("uom").getValue().equals("degrees")) )
                                { nodeTypes_[i] = THERMOSTAT_TYPE;}
                                else if((stateE!=null&&stateE.getAttribute("uom").getValue().indexOf("%")!=-1 )) { nodeTypes_[i] = DIMMER_TYPE;}
                                else if((stateE!=null&&stateE.getAttribute("uom").getValue().equals("W") )) { nodeTypes_[i] = METER_TYPE;}
                                else { nodeTypes_[i] = SWITCH_TYPE;}
                            }
                            catch(java.lang.NullPointerException npEx)
                            {
                                System.out.println("Minor Error: problem parsing "+i+"\n     nodeName: "+nodeNames_[i]+"\n     nodeAddresses_"+nodeAddresses_[i]);
                                System.out.println("             child Element "+stateE);
                            }
                        }
                    }
                }
                if (folderElements_!=null && folderElements_.size()>0)
                {
                    folderNames_ = new String[folderElements_.size()];
                    folderAddresses_ = new String[folderElements_.size()];
                    for (int i=0; i< folderElements_.size(); i++)
                    {
                        currE = null;
                        currE = folderElements_.get(i);
                        if (currE !=null)
                        {
                            folderNames_[i] = currE.getFirstChildElement("name").getValue();
                            folderAddresses_[i] = currE.getFirstChildElement("address").getValue();
                        }
                    }
                }
                if (groupElements_!=null && groupElements_.size()>0)
                {
                    groupNames_ = new String[groupElements_.size()];
                    groupAddresses_ = new String[groupElements_.size()];
                    groupMembers_ = new GroupMembers[groupElements_.size()];
                    for (int i=0; i< groupElements_.size(); i++)
                    {
                        currE = null;
                        currE = groupElements_.get(i);
                        if (currE !=null)
                        {
                            Element members = null;
                            Elements links = null;
                            try
                            {
                                groupNames_[i] = currE.getFirstChildElement("name").getValue();
                                groupAddresses_[i] = currE.getFirstChildElement("address").getValue();
                                // the group nodes are wrapped in:
                  /*
                    <members>
                      <link type="16">13 EB 11 1</link>
                      <link type="16">43 A2 FB 3</link>
                      <link type="32">13 57 A7 1</link>
                      <link type="32">20 B8 93 1</link>
                    </members>
                    // type is a relationship type
                    // 16 is a switch/controller
                    // 32 is a node
                  */
                                members = currE.getFirstChildElement("members");
                                links = members.getChildElements("link");
                                groupMembers_[i] = new GroupMembers(links);
                            }
                            catch(java.lang.NullPointerException npEx)
                            {
                                System.out.println("Minor Error: problem parsing "+i+"\n     groupName: "+groupNames_[i]+"\n     groupAddresses: "+groupAddresses_[i]);
                                System.out.println("             members Element "+members);
                                System.out.println("             links "+links);
                                System.out.println("             currE "+currE);
                                npEx.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        //log_.endMethod(methodName);
        return nodeDoc_;
    }
}

/* ********************************************************************* */

/* holds the addresses of scene/group member nodes and switches */
class GroupMembers extends Object
{
    String [] memberNodes_ = null;
    String [] memberSwitchNodes_ = null;
    boolean debugOut_ = false;

    public GroupMembers(Elements links)
    {
        int devCount = 0;
        int swCount = 0;
        for (int i = 0; i<links.size(); i++)
        {
            // ONLY include switchable nodes type='32'
            try
            {
                if("32".equals(links.get(i).getAttribute("type").getValue()))  devCount++;
                else swCount++;
            }
            catch(java.lang.NullPointerException npEx) {if(debugOut_) npEx.printStackTrace();}
        }
        memberNodes_ = new String[devCount];
        memberSwitchNodes_ = new String[swCount];
        devCount = 0;
        swCount = 0;
        for (int i = 0; i<links.size(); i++)
        {
            // ONLY include switchable nodes type='32'
            try
            {
                if("32".equals(links.get(i).getAttribute("type").getValue()))
                {
                    memberNodes_[devCount++] = links.get(i).getValue();
                }
                else
                {
                    memberSwitchNodes_[swCount++] = links.get(i).getValue();
                }
            }
            catch(java.lang.NullPointerException npEx) {if(debugOut_) npEx.printStackTrace();}
        }

    }// constructor

    String [] getMemberNodes() { return memberNodes_;}
    String [] getMemberSwitchNodes() { return memberSwitchNodes_;}
    public String toString()
    {
        String retVal = "node addresses: ";
        for(String memAddr : memberNodes_) retVal += memAddr+", ";
        retVal=retVal.substring(0,retVal.length()-2);
        retVal += System.getProperty("line.separator")+"switch/controller addresses: ";
        for(String memAddr : memberSwitchNodes_) retVal += memAddr+", ";
        retVal=retVal.substring(0,retVal.length()-2);
        return retVal;
    }
} // class GroupMembers


/* ********************************************************************* */


/** an object to represent an ISY Node. **/
class IsyVars extends Object
{
    public static final int VAR_TYPE_INT = 1;
    public static final int VAR_TYPE_STATE = 2;
    int varType_ = VAR_TYPE_INT;
    int maxID_ = 0;
    Document varDoc_ = null;
    Document varValueDoc_ = null;
    Element rootElem_ = null;
    Builder xmlBuilder_ = new Builder();
    Hashtable <String, Integer>  varName_ = null;
    String [] varValue_ = null;
    Elements varElements_ = null;


    public IsyVars()
    {

    }


    public IsyVars( int varType, StringBuilder varXmlSB, StringBuilder varValuesXmlSB) throws ParsingException, IOException
    {
        if(  (varType==VAR_TYPE_INT || varType==VAR_TYPE_STATE) && varXmlSB!=null && varXmlSB.length()>7 && varValuesXmlSB!=null && varValuesXmlSB.length()>7 )
        {
            varType_ = varType;
            parseXMLVars(varXmlSB.toString(),varValuesXmlSB.toString());
        }
    }


    /**
     * Reads in the XML Config files - a definition XML and a var Value XML.
     *
     *<pre>
     * <CList type="VAR_INT">
     *     <e id="1" name="ir9Count" />
     *     <e id="2" name="Int_UpKeypadTemp" />
     *     <e id="3" name="Int_DwnKeypadTemp" />
     *     <e id="4" name="VacationMode" />
     *     <e id="5" name="iMeterCurrentValue" />
     *     <e id="6" name="PowerFailure" />
     *     <e id="7" name="LastPowerFailure" />
     *     <e id="8" name="ChristmasMode" />
     *     <e id="9" name="StairSensor" />
     *     <e id="10" name="PowerFailureEmailSent" />
     *     <e id="11" name="WakeWarp4" />
     *     <e id="12" name="tomsMorningLights" />
     *     <e id="13" name="iDay.Of.Year" />
     *     <e id="14" name="iDay.Of.Month" />
     *     <e id="15" name="iYear" />
     *     <e id="17" name="iDay.Of.Week" />
     *     <e id="16" name="iLeap.Year" />
     *     <e id="19" name="iDay.Counter" />
     *     <e id="18" name="iMonth" />
     *     <e id="21" name="iEvery.Third.Day.Counter" />
     *     <e id="20" name="iEvery.Other.Day.Counter" />
     *     <e id="23" name="iEvery.Fifth.Day.Counter" />
     *     <e id="22" name="iEvery.Fourth.Day.Counter" />
     *     <e id="25" name="iWeek.Counter" />
     *     <e id="24" name="iWeek.Of.Month" />
     *     <e id="27" name="iEvery.Third.Week.Counter" />
     *     <e id="26" name="iEvery.Other.Week.Counter" />
     *     <e id="29" name="iOdd.Even.Day" />
     *     <e id="28" name="iEvery.Fourth.Week.Counter" />
     *     <e id="31" name="iSync" />
     *     <e id="30" name="iHoliday" />
     *     <e id="34" name="elk.zone.down.window.violated" />
     *     <e id="35" name="elk.zone.water.violated" />
     *     <e id="32" name="elk.zone.door.violated" />
     *     <e id="33" name="elk.zone.up.window.violated" />
     *     <e id="36" name="elk.zone.smoke.violated" />
     *     <e id="37" name="elk.zone.up.bedroomWindows.bypass" />
     * </CList>
     *
     *  <vars>
     *      <var type="1" id="1">
     *        <init>0</init>
     *        <val>0</val>
     *        <ts>20160131 17:15:16</ts>
     *      </var>
     *      <var type="1" id="2">
     *        <init>0</init>
     *        <val>22</val>
     *        <ts>20160313 09:58:26</ts>
     *      </var>
     *      ....
     *   </vars>
     *</pre>
     **/
    protected void parseXMLVars(String varXmlStr, String varValuesXmlStr) throws ParsingException, IOException
    {
        //String methodName = className_+"."+Util.getCurrentMethodName();
        //log_.startMethod(methodName);
        String tmpNm = "";
        String tmpId = "";
        varDoc_ = xmlBuilder_.build(varXmlStr,null);
        if (varDoc_!=null)
        {
            rootElem_ = varDoc_.getRootElement();
            // load the var definitions
            if (rootElem_ != null && rootElem_ instanceof Element && rootElem_.getLocalName().equals("CList"))
            {
                varElements_ = rootElem_.getChildElements("e");

                Element currE = null;
                if (varElements_!=null && varElements_.size()>0)
                {
                    varName_ = new Hashtable<String, Integer>();
                    for (int i=0; i< varElements_.size(); i++)
                    {
                        currE = varElements_.get(i);
                        if (currE !=null)
                        {
                            tmpNm = currE.getAttribute("name").getValue();
                            tmpId = currE.getAttribute("id").getValue().trim();
                            //System.out.println("Adding "+tmpNm+":"+tmpId);
                            varName_.put(tmpNm, new Integer(tmpId));
                            if(Integer.parseInt(tmpId)>maxID_) maxID_ = Integer.parseInt(tmpId);
                        }
                    }
                }
            }
        }
        varValueDoc_ = xmlBuilder_.build(varValuesXmlStr,null);
        if (varDoc_!=null)
        {
            rootElem_ = varValueDoc_.getRootElement();
            // load the var definitions
            if (rootElem_ != null && rootElem_ instanceof Element && rootElem_.getLocalName().equals("vars"))
            {
                varElements_ = rootElem_.getChildElements("var");
            }
        }
    }


    public int getNumVars()
    {
        int retVal = 0;
        if(varName_!=null) retVal = varName_.size();
        return retVal;
    }


    public String [] getVarNames()
    {
        String [] retVal = new String[varName_.size()];
        Set<String> namesSet = varName_.keySet();
        retVal = (String []) namesSet.toArray(retVal);
        return retVal;
    }


    public int getVarValue(String varName) throws Exception
    {
        int retVal = 0;
        //System.out.println(" DEBUG: Varname: "+varName+" VarVal: ");
        int varID = 0;
        Integer vI = null;

        Set<String> namesSet = varName_.keySet();
        //System.out.println(Arrays.toString(namesSet.toArray()));
        if(varName_!=null && varName_.containsKey(varName))
        {
            try
            {
                vI = ((Integer)varName_.get(varName));
                if(vI !=null)
                    varID = vI.intValue();
                else
                    System.out.println("\nCrapped Out on  ("+varName+") "+varName_.get(varName));
            }
            catch ( java.lang.NullPointerException nEx)
            {
                System.out.println("\nERROR with "+varName);
                nEx.printStackTrace();
                throw new Exception("VarName not found");
            }
        }
        else throw new Exception("VarName not found");

        if (varID>0)
        {
            Element currE = null;
            Attribute currID = null;
            if (varElements_!=null && varElements_.size()>0)
            {
                // varName_ = new Hashtable<String, Integer>();
                for (int i=0; i< varElements_.size(); i++)
                {
                    currE = varElements_.get(i);
                    if (currE !=null)
                    {
                        currID = currE.getAttribute("id");
                        if( currID!=null && Integer.parseInt(currID.getValue())==varID)
                        {
                            retVal =  Integer.parseInt(currE.getFirstChildElement("val").getValue());
                            i = varElements_.size();
                        }
                    }
                }
            }
        }
        return retVal;
    }


    /** the max ID value held as a class var. **/
    public int getMaxID()
    {
        return maxID_;
    }


    /** the next unused var id value that can be used to create a new var. **/
    public int getNextUnusedID()
    {
        return getMaxID()+1;
    }


    public String toString()
    {
        return varName_.toString().replace(", ",",\n ").replace("{","{\n ").replace("}","\n}");
    }


}