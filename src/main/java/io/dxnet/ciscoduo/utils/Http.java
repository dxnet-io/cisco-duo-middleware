package io.dxnet.ciscoduo.utils;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

public class Http {
    public final static int BACKOFF_FACTOR = 2;
    public final static int INITIAL_BACKOFF_MS = 1000;
    public final static int MAX_BACKOFF_MS = 32000;
    public final static int DEFAULT_TIMEOUT_SECS = 60;
    private final static int RATE_LIMIT_ERROR_CODE = 429;

    public final static String HmacSHA1 = "HmacSHA1";
    public final static String HmacSHA512 = "HmacSHA512";

    private String method;
    private String host;
    private String uri;
    private String signingAlgorithm;
    private Headers.Builder headers;
    Map<String, String> params = new HashMap<String, String>();
    private Random random = new Random();
    private OkHttpClient httpClient;

    private String header = "";
    private String date = "";

    public static SimpleDateFormat RFC_2822_DATE_FORMAT
        = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z",
                               Locale.US);

    public static MediaType FORM_ENCODED = MediaType.parse("application/x-www-form-urlencoded");

    public Http(String in_method, String in_host, String in_uri) {
        this(in_method, in_host, in_uri, DEFAULT_TIMEOUT_SECS);
    }

    public Http(String in_method, String in_host, String in_uri, int timeout) {
        method = in_method.toUpperCase();
        host = in_host;
        uri = in_uri;
        signingAlgorithm = "HmacSHA1";

        headers = new Headers.Builder();
        headers.add("Host", host);

        httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(timeout, TimeUnit.SECONDS);
        httpClient.setWriteTimeout(timeout, TimeUnit.SECONDS);
        httpClient.setReadTimeout(timeout, TimeUnit.SECONDS);
    }

   

   

    protected void sleep(long ms) throws Exception {
        Thread.sleep(ms);
    }

    public void signRequest(String ikey, String skey)
      throws UnsupportedEncodingException {
        signRequest(ikey, skey, 2);
    }

    public void signRequest(String ikey, String skey, int sig_version)
      throws UnsupportedEncodingException {
        this.date = formatDate(new Date());
        String canon = canonRequest(this.date, sig_version);
        String sig = signHMAC(skey, canon);

        String auth = ikey + ":" + sig;
        this.header = "Basic " + Base64.encodeBytes(auth.getBytes());
        addHeader("Authorization", this.header);
        if (sig_version == 2) {
            addHeader("Date", this.date);
        }
    }

    public String getAuth(){
        return this.header;
    }

    public String getDate(){
        return this.date;
    }

    protected String signHMAC(String skey, String msg) {
        try {
            byte[] sig_bytes = Util.hmac(signingAlgorithm,
                                         skey.getBytes(),
                                         msg.getBytes());
            String sig = Util.bytes_to_hex(sig_bytes);
            return sig;
        } catch (Exception e) {
            return "";
        }
    }

    private String formatDate(Date date) {
        // Could use ThreadLocal or a pool of format objects instead
        // depending on the needs of the application.
        synchronized (RFC_2822_DATE_FORMAT) {
            return RFC_2822_DATE_FORMAT.format(date);
        }
    }

    public void addHeader(String name, String value) {
        headers.add(name, value);
    }

    public void addParam(String name, String value) {
      params.put(name, value);
    }

    public void setProxy(String host, int port) {
        this.httpClient.setProxy(
            new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port))
        );
    }

    public void setSigningAlgorithm(String algorithm)
      throws NoSuchAlgorithmException {
        if (algorithm != HmacSHA1 && algorithm != HmacSHA512) {
            throw new NoSuchAlgorithmException(algorithm);
        }
        signingAlgorithm = algorithm;
    }

    protected String canonRequest(String date, int sig_version)
      throws UnsupportedEncodingException {
        String canon = "";
        if (sig_version == 2) {
            canon += date + "\n";
        }
        canon += method.toUpperCase() + "\n";
        canon += host.toLowerCase() + "\n";
        canon += uri + "\n";
        canon += createQueryString();

        return canon;
    }

    private String createQueryString()
        throws UnsupportedEncodingException {
      ArrayList<String> args = new ArrayList<String>();
      ArrayList<String> keys = new ArrayList<String>();

      for (String key : params.keySet()) {
        keys.add(key);
      }

      Collections.sort(keys);

      for (String key : keys) {
        String name = URLEncoder
            .encode(key, "UTF-8")
            .replace("+", "%20")
            .replace("*", "%2A")
            .replace("%7E", "~");
        String value = URLEncoder
            .encode(params.get(key), "UTF-8")
            .replace("+", "%20")
            .replace("*", "%2A")
            .replace("%7E", "~");
        args.add(name + "=" + value);
      }

      return Util.join(args.toArray(), "&");
    }
}
