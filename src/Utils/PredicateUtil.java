package Utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDFS;

public class PredicateUtil extends UriUtil {
	private HashMap<String,URI> defaultUri;
	private ArrayList<String> propertyPred;
	
	public PredicateUtil() {
		super();
		defaultUri = new HashMap<String,URI>();
		propertyPred = new ArrayList<String>();
		initialize();
	}
	
	/*
	 * Read the default RDF file
	 * and create default RDF set.
	 */
	private void initialize() {
		//TODO
		defaultUri.put("name", RDFS.LABEL);
		defaultUri.put("weiboText", RDFS.LITERAL);
		defaultUri.put("commentContext", RDFS.COMMENT);
		
		//TODO
		propertyPred.add("name");
		propertyPred.add("friend");
		propertyPred.add("follower");
		propertyPred.add("createWeibo");
		propertyPred.add("createComment");
		propertyPred.add("commentTo");
	}
	
	/*
	 * To insert one default URI record.
	 */
	public void insertDefaultUri(String key, URI uri) {
		String uriKey = new String(key.toUpperCase());
		URI defUri = super.getUri(uri);
		defaultUri.put(uriKey, defUri);
	}
	
	public void insertPropertyPred(URI uri) {
		propertyPred.add(uri.toString());
	}
	
	public URI getPredUri(String predStr) {
		if(defaultUri.containsKey(predStr)) {
			URI rtn = defaultUri.get(predStr);
			return rtn;
		} else {
			return getUri(predStr);
		}
	}
	
	/*
	 * To judge if the object should be an URI.
	 */
	public boolean isObjUri(String predStr) {
		return propertyPred.contains(predStr);
	}

}
