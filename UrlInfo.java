import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Encoder;

/**
 * Makes a request to the Alexa Web Information Service UrlInfo action.
 */
public class UrlInfo {

    private static final String ACTION_NAME = "UrlInfo";
    private static final String RESPONSE_GROUP_NAME = "Rank,ContactInfo,LinksInCount";
    private static final String SERVICE_HOST = "awis.amazonaws.com";
    private static final String AWS_BASE_URL = "http://" + SERVICE_HOST + "/?";
    private static final String HASH_ALGORITHM = "HmacSHA256";

    private static final String DATEFORMAT_AWS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * Edited by Abhinav Ajgaonkar <abhinav316@gmail.com>
     */
    private static String ACCESS_KEY = "";
    private static String ACCESS_SEC = "";
    
    private String site;
    
    public UrlInfo(String site)	{
    	this.site = site;
    }
    /** End Edits **/

    /**
     * Generates a timestamp for use with AWS request signing
     *
     * @param date current date
     * @return timestamp
     */
    protected static String getTimestampFromLocalTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATEFORMAT_AWS);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(date);
    }

    /**
     * Computes RFC 2104-compliant HMAC signature.
     *
     * @param data The data to be signed.
     * @return The base64-encoded RFC 2104-compliant HMAC signature.
     * @throws java.security.SignatureException
     *          when signature generation fails
     */
    protected String generateSignature(String data)
            throws java.security.SignatureException {
        String result;
        try {
            // get a hash key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(
                    ACCESS_SEC.getBytes(), HASH_ALGORITHM);

            // get a hasher instance and initialize with the signing key
            Mac mac = Mac.getInstance(HASH_ALGORITHM);
            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes());

            // base64-encode the hmac
            // result = Encoding.EncodeBase64(rawHmac);
            result = new BASE64Encoder().encode(rawHmac);

        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : "
                    + e.getMessage());
        }
        return result;
    }

    /**
     * Makes a request to the specified Url and return the results as a String
     *
     * @param requestUrl url to make request to
     * @return the XML document as a String
     * @throws IOException
     */
    public static String makeRequest(String requestUrl) throws IOException {
        URL url = new URL(requestUrl);
        URLConnection conn = url.openConnection();
        InputStream in = conn.getInputStream();

        // Read the response
        StringBuffer sb = new StringBuffer();
        int c;
        int lastChar = 0;
        while ((c = in.read()) != -1) {
            if (c == '<' && (lastChar == '>'))
                sb.append('\n');
            sb.append((char) c);
            lastChar = c;
        }
        in.close();
        return sb.toString();
    }


    /**
     * Builds the query string
     */
    protected String buildQuery()
            throws UnsupportedEncodingException {
        String timestamp = getTimestampFromLocalTime(Calendar.getInstance().getTime());

        Map<String, String> queryParams = new TreeMap<String, String>();
        queryParams.put("Action", ACTION_NAME);
        queryParams.put("ResponseGroup", RESPONSE_GROUP_NAME);
        queryParams.put("AWSAccessKeyId", ACCESS_KEY);
        queryParams.put("Timestamp", timestamp);
        queryParams.put("Url", site);
        queryParams.put("SignatureVersion", "2");
        queryParams.put("SignatureMethod", HASH_ALGORITHM);

        String query = "";
        boolean first = true;
        for (String name : queryParams.keySet()) {
            if (first)
                first = false;
            else
                query += "&";

            query += name + "=" + URLEncoder.encode(queryParams.get(name), "UTF-8");
        }

        return query;
    }
    
    /**
     * Edited by Abhinav Ajgaonkar <abhinav316@gmail.com>
     */
    
    /**
     * Sets up your AWS credentials for use within this class
     */
    public static void setCredentials(String key, String secret)	{
    	UrlInfo.ACCESS_KEY = key;
    	UrlInfo.ACCESS_SEC = secret;
    }
    
    /** 
     * Gets the Alexa info for a url string 
     */
    public static String get(String site)	{
    	try	{
    		UrlInfo u = new UrlInfo(site);
    		String query = u.buildQuery();
            String toSign = "GET\n" + SERVICE_HOST + "\n/\n" + query;
            String signature = u.generateSignature(toSign);
            String uri = AWS_BASE_URL + query + "&Signature=" +
                    URLEncoder.encode(signature, "UTF-8");

            // Make the Request
            String xmlResponse = makeRequest(uri);
            System.out.println("Got info for " + site);
            return xmlResponse;
    	}
    	catch(Exception ex)	{ ex.printStackTrace(); }
    	return "";
    }
    
    /** End Edits **/
}
