/*------------------------------------------------/
Script Name:  Retrieve Azure Blob Contents
Creation Date: XX-XX-XXXX
Author: Mitchell Franklin
Description: Dell Boomi Charges for Connections; this script allows an Azure Blob Item to be retrieved.
Last Update Date: XX-XX-XXXX
Last Update By: Mitchell Franklin
Last Update Description: Initial Script Creation
/-------------------------------------------------*/

/*Notes*/
//Update areas defined that start with <$ and end with $> i.e. <$Value$>//
//Use Process Property to Set Values and remember to set in Extensions of the Process Flow; use dynamic process properties (Set Properties) to read the values in to use in the below process.
//Set Decision Shape after this to check they try catch Dynamic Process Try Catch for errors - Boomi try Catch does not work on Custom Scriptting

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.Properties;
import java.io.ByteArrayInputStream;
import com.boomi.execution.ExecutionUtil;
import java.io.InputStreamReader;
import java.io.BufferedReader;


/*--------Boomi Do not touch--------*/
for (int i = 0; i < dataContext.getDataCount(); i++) {
    InputStream is = dataContext.getStream(i);
    Properties props = dataContext.getProperties(i);
    /*----------------------------------*/
    //Try to build and execute the request; as the Standards Boomi Try Catch does not capture errors in Data Process Scripts this will be done instead and if an error is capture stored in a-
    //Dynamic Process Property to allow the Decision / Business Rule Tasks to interpret for errors in the call so that it continues and not just ends.
    try {

        //Set a repsone buffer to handle the incoming response
        StringBuffer response = new StringBuffer();

        //Get and retrieve the required Process Properties that will build up the URL to be called
        String host = ExecutionUtil.getDynamicProcessProperty("<$Dynamic Process Property Name$>"); //Get the Blob URI that is the Host Name for the Storage Blob
        String blobName = ExecutionUtil.getDynamicProcessProperty("<$Dynamic Process Property Name$>"); //Get the Blob URI that is the Host Name for the Storage Blob
        String sv = ExecutionUtil.getDynamicProcessProperty("<$Dynamic Process Property Name$>"); //Get the Storage Version used for Blob
        String sr = ExecutionUtil.getDynamicProcessProperty("<$Dynamic Process Property Name$>"); //Get the Storage Resource i.e. c for container
        String si = ExecutionUtil.getDynamicProcessProperty("<$Dynamic Process Property Name$>"); //Get the  Storage ID used within the container
        String sig = ExecutionUtil.getDynamicProcessProperty("<$Dynamic Process Property Name$>"); //Get the Signtaure that allows the access to the Storage Blob
     


        //Create the Full URL to be called to Retrieve the BloB
        String azureURL = host + '/' + blobName + '?si=' + si + '&sv=' + sv + '&sr=' + sr + '&sig=' + sig



        //With everything set we will now create and open the connection - This part of the code is the actual call and return; the above code is just the actualy setup of the query for the Azure Blob
        URL obj;
        obj = new URL(azureURL);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //Now that the connection has opened we will set the connection to a GET in which will send the URL as a connection process and retrieve the response
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        BufferedReader inReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = inReader.readLine()) != null) {
            response.append(inputLine);
        }
        inReader.close();

        //Store Response back into stream for Boomi to be able to use (Current Payload)
        dataContext.storeStream(new ByteArrayInputStream(response.toString().getBytes()), props);


    } catch (Exception ex) {
    //Error has occure; capture the error and send it back out as a dynamic proecss property for it to be checked and handled.

        ExecutionUtil.setDynamicProcessProperty("<$Dynamic Process Property Trty Catch Name$>", ex.toString(), true);
        dataContext.storeStream(is, props);
    }


/*--------Boomi Do not touch--------*/
}
/*----------------------------------*/