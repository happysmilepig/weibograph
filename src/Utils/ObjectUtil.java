package Utils;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class ObjectUtil extends UriUtil {
	
	public ObjectUtil() {
		super();
	}
	
	public ObjectUtil(String namespace) {
		super(namespace);
	}
	
	public void setValueFactory(ValueFactory vf) {
		super.valueFactory = vf; 
	}
	
	public Literal getLiteral(String str) {
		return valueFactory.createLiteral(str);
	}
	
	//TODO
	public Literal getLiteral(String str, boolean langEnable) {
		if(langEnable) {
			String[] strSeq = str.split("@");
			return valueFactory.createLiteral(strSeq[0],strSeq[1]);
		} else {
			return valueFactory.createLiteral(str);
		}
	}
	
	public URI getObjUri(String objStr) {
		return super.getUri(objStr);
	}

}
