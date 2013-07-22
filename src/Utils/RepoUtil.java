package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;


import Const.Const;

public class RepoUtil {
	private Repository repo;
	private NativeStore natStore;
//	private MemoryStore memStore;
	private File repoFile;
	private RepositoryConnection repoConn;
	private SubjectUtil subjUtil;
	private PredicateUtil predUtil;
	private ObjectUtil objUtil;
	
	private ValueFactory valueFactory;
	private LinkedHashModel model;
	
	public RepoUtil() {
		repoFile = new File(Const.repoPath);
		natStore = new NativeStore(repoFile);
//		memStore = new MemoryStore();
		repo = new SailRepository(natStore);
//		repo = new SailRepository(memStore);
		subjUtil = new SubjectUtil();
		predUtil = new PredicateUtil();
		objUtil = new ObjectUtil();
		valueFactory = new ValueFactoryImpl();
		model = new LinkedHashModel();
		repoInitialize();
	}
	
//	public RepoUtil(String repoPath) {
//		repoFile = new File(repoPath);
//		natStore = new NativeStore(repoFile);
//		repo = new SailRepository(natStore);
//		subjUtil = new SubjectUtil();
//		predUtil = new PredicateUtil();
//		objUtil = new ObjectUtil();
//		valueFactory = new ValueFactoryImpl();
//		model = new LinkedHashModel();
//		repoInitialize();
//	}
	
	private void repoInitialize() {
		try {
			repo.initialize();
		} catch(RepositoryException e) {
			
		}
	}

	public RepositoryConnection getConnection() {
		try {
			return repo.getConnection();
		} catch (RepositoryException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * To set the subject namespace
	 * and type.
	 */
	public void setSubjNsAndType(String ns, String type) {
		subjUtil.setNameSpace(ns);
		subjUtil.setType(type);
	}
	
	public void setNameSpace(String ns) {
		setNameSpace(ns,ns,ns);
	}
	
	public void setNameSpace(String subjNs, String predNs, String objNs) {
		subjUtil.setNameSpace(subjNs);
		predUtil.setNameSpace(predNs);
		objUtil.setNameSpace(objNs);
	}
	
	public void setSubjType(String type) {
		subjUtil.setType(type);
	}
	
	/*
	 * To set the predicate namespace
	 * and type.
	 */
	public void setPredNsAndType(String ns, String type) {
		predUtil.setNameSpace(ns);
		predUtil.setType(type);
	}
	
	public void setPredType(String type) {
		predUtil.setType(type);
	}
	
	/*
	 * To set the object namespace
	 * and type.
	 */
	public void setObjNsAndType(String ns, String type) {
		objUtil.setNameSpace(ns);
		objUtil.setType(type);
	}
	
	public void setObjType(String type) {
		objUtil.setType(type);
	}
	
	/*
	 * The URI-URI-Literal format SPO record.
	 */
	public void addRecord(URI subj, URI pred, Literal obj) {
		try {
			repoConn = repo.getConnection();
			repoConn.add(subj, pred, obj);
			repoConn.close();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} 
	}
	
	/*
	 * The URI-URI-URI format SPO record.
	 */
	public void addRecord(URI subj, URI pred, URI obj) {
		try {
			repoConn = repo.getConnection();
			repoConn.add(subj, pred, obj);
			repoConn.close();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * The Str-Str-Str format SPO record.
	 */
	public void addRecord(String subjStr, String predStr, String objStr, boolean uriFlag) {
		URI subj;
		URI pred;
		URI objUri;
		Literal objLit;
		try {
			repoConn = repo.getConnection();
//			repoConn.isActive()
			subj = subjUtil.getUri(subjStr);
			pred = predUtil.getPredUri(predStr);
			if(uriFlag) {
				objUri = objUtil.getObjUri(objStr);
				objLit = null;
				repoConn.add(subj,pred,objUri);
				model.add(valueFactory.createStatement(subj, pred, objUri));
			} else {
				objUri = null;
				objLit = objUtil.getLiteral(objStr);
				repoConn.add(subj, pred,objLit);
				model.add(valueFactory.createStatement(subj, pred, objLit));
			}
			repoConn.close();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * The RDF reader for insert records
	 * with stream reader.
	 */
	public void addRecords(String rdfPath, String baseURI, RDFFormat format) {
		File rdfFile = new File(rdfPath);
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(rdfFile)));
			repoConn = repo.getConnection();
			repoConn.add(reader, baseURI, format);
			repoConn.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (RDFParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Read the RDF file with SPO format in stream.
	 */
	public void addRecords(String spoFilePath) {
//		File spoFile = new File(spoFilePath);
		InputStream in = null;
		try {
			in = new FileInputStream(spoFilePath);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		BufferedReader reader = null;
		String line = null;
		String[] segments = null;
		String subjStr, predStr, objStr;
		URI subj, pred;
		Literal lit = null;
		Statement stat = null;
//		ArrayList<Statement> model = new ArrayList<Statement>();
		LinkedHashModel model = new LinkedHashModel();
		
		System.out.println("Start reading file in line!");
		reader = new BufferedReader(new InputStreamReader(in));
		
		try {
			while( (line = reader.readLine()) != null) {
				if(line.length() <= 0) {
					continue;
				}
				//TODO
				segments = line.split("\t");
				subjStr = segments[0];
				predStr = segments[1];
				objStr = segments[2];
				ValueFactory vf = repo.getValueFactory();
				subj = vf.createURI(subjStr);
				pred = vf.createURI(predStr);
				lit = vf.createLiteral(objStr);
				stat = vf.createStatement(subj, pred, lit);
				model.add(stat);
			}
			reader.close();
			in.close();
			repoConn = repo.getConnection();
			repoConn.add(model);
			saveRDFTurtle(model, ".//test.n3", RDFFormat.N3);
			repoConn.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Save triples into RDF files 
	 * with the specific RDF format.
	 */
	public void saveRDFTurtle(LinkedHashModel model, String filePath, RDFFormat rdfFormat) {
		try {
			FileOutputStream out = new FileOutputStream(filePath);
			RDFWriter writer = null;
			writer = Rio.createWriter(rdfFormat, out);
			writer.startRDF();
			for(Statement stat: model) {
				writer.handleStatement(stat);
			}
			writer.endRDF();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
		
	}
	
	public void saveRDFTurtle(String filePath, RDFFormat rdfFormat) {
		try {
			FileOutputStream out = new FileOutputStream(filePath);
			RDFWriter writer = null;
			writer = Rio.createWriter(rdfFormat, out);
			writer.startRDF();
			for(Statement stat: model) {
				writer.handleStatement(stat);
			}
			writer.endRDF();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * To save the triples in RDF turtle format
	 * directly from the Sesame database. 
	 */
	public void saveRDFTurtle() {
		//TODO
	}
	
	
}
