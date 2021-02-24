package io.dxnet.ciscoduo.controllers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.dxnet.ciscoduo.model.RequestCiscoDuo;
import io.dxnet.ciscoduo.utils.Http;

@RestController
public class CiscoDuoController {

    Logger Log = LoggerFactory.getLogger(CiscoDuoController.class);

    @Value("${property.path}")
    private String propertyPath;


    private String apiHost;
    private String apiKey;
    private String apiSecret;

    public static SimpleDateFormat RFC_2822_DATE_FORMAT
        = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z",
                               Locale.US);

    @RequestMapping(method = RequestMethod.POST, value = "/ciscoduo/{domain}/access", produces = {"application/json", "text/json"})
	public Object requestAccess(@PathVariable String domain, @RequestBody RequestCiscoDuo req, HttpServletResponse resp)
            throws IOException {
        Log.debug("CiscoDuoController.requestAccess: Starting...");

        Object result = new Object();
        //String async = "0";
        String device = "auto";
        String displayUserName = req.getUsername();
        String factor = "push";
        String type = "Open-Door";

        try{
           
            // load customer property file
            if(!this.loadCustomerProperties(domain)){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                req.setDomain(domain);
                req.setMessage("Domain is not configured");
                Log.debug("CiscoDuoController.requestAccess: Domain is not configured: " +  domain + ". Exiting...");
                return req;
            }
            

            if(null == req.getUsername() || req.getUsername() == ""){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                req.setMessage("Username field in body is mandatory");
                Log.debug("CiscoDuoController.requestAccess: Username field in body is mandatory. Domain: " +  domain + ". Exiting...");
                return req;
            }

            Http httpCiscoDuo = new Http("POST", this.apiHost, "/auth/v2/auth");
            httpCiscoDuo.addParam("device", device);
            httpCiscoDuo.addParam("display_username", displayUserName);
            httpCiscoDuo.addParam("factor", factor);
            httpCiscoDuo.addParam("type",type);
            httpCiscoDuo.addParam("username", req.getUsername());
            httpCiscoDuo.signRequest(this.apiKey, this.apiSecret);
            Unirest.setTimeouts(0, 0);
            HttpResponse<String> response = Unirest.post("https://" +this.apiHost + "/auth/v2/auth?&device=" + device + "&display_username=" + displayUserName + "&factor=" + factor  + "&type=" + type  + "&username=" + req.getUsername())
            .header("Authorization", httpCiscoDuo.getAuth())
            .header("Host", this.apiHost)
            .header("Date", httpCiscoDuo.getDate())
            .asString();

            result = response.getBody();
            resp.setStatus(response.getStatus());
            Log.debug("CiscoDuoController.requestAccess: Authentication processed successfully by CiscoDuo: username: " +  req.getUsername() +  " |  domain: " + domain +  " | Response: " +  result.toString());
            Log.debug("CiscoDuoController.requestAccess: Authentication processed successfully by CiscoDuo: username: " +  req.getUsername() +  " |  domain: " + domain +  ". Exiting...");
            return result;
        }catch(Exception e){
            System.out.println(e.getMessage());
            Log.error("CiscoDuoController.requestAccess: Exiting...");
            return null;
        }
    }

    
    private Boolean loadCustomerProperties(String domain) {
        try (InputStream input = new FileInputStream(this.propertyPath + "/" + domain + ".properties")){

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
           this.apiHost      = prop.getProperty("api.host");
           this.apiKey       = prop.getProperty("api.key");
           this.apiSecret    = prop.getProperty("api.secret");
           return true;

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}