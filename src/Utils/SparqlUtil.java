package Utils;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;


public class SparqlUtil {
	private RepositoryConnection repoConn;
	private StringBuilder query;
	
	public SparqlUtil(RepoUtil repo) {
		repoConn = repo.getConnection();
		query = new StringBuilder();
	}
	
	public TupleQueryResult query(String[] prefix, String select, 
									String where, String orderBy, String limit) 
									throws QueryEvaluationException, RepositoryException, 
									MalformedQueryException {
		//PREFIX
		if(prefix != null) { 
			query.append("PREFIX ");
			for(int i=0; i<prefix.length/2; i++) {
				query.append(prefix[2*i]);
				query.append(":");
				query.append("<");
				query.append(prefix[2*i+1]);
				query.append(">");
			}
		}
		//SELECT
		query.append("SELECT ");
		query.append(select);
		
		//WHERE
		if(where != null) {
			query.append("WHERE { ");
			query.append(where);
			query.append(" }");
		}
		
		//ORDER BY
		if(orderBy != null) {
			query.append("ORDER BY ");
			query.append(orderBy);
		}
		
		if(limit != null) {
			query.append("LIMIT ");
			query.append(limit);
		}
		
		return repoConn.prepareTupleQuery(QueryLanguage.SPARQL, query.toString()).evaluate();
		
	}
	
	
	public TupleQueryResult Query(String[] PREFIX,
			            String[] Args,
						String[] ANDargs, String[] ORargs, String[] OPTIONAL,
						String RANarg, String ran1, String ran2,
						String EXISTarg, String EXIST,
						String ORDERBY,	String LIMIT) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		StringBuilder RESULT = new StringBuilder();

		if (PREFIX != null) {
			RESULT.append("PREFIX ");
			for (int i = 0; i < PREFIX.length; i++) {
				RESULT.append(PREFIX[i]);
			}//前缀
		}

		if (Args != null) {
			RESULT.append("SELECT ");
			for (int i = 0; i < Args.length; i++) {
				RESULT.append("?" + Args[i] + " ");
			}//需要输出的参数
		}

		RESULT.append("WHERE {");

		if (ANDargs != null) {
			for (int i = 0; i < ANDargs.length; i++) {
				RESULT.append(ANDargs[i] + " . ");
			}//必须满足的条件
		}

		if (ORargs != null) {
			RESULT.append("{ " + ORargs[0] + " } ");
			for (int i = 1; i < ORargs.length; i++) {
				RESULT.append("UNION { " + ORargs[i] + " } ");
			}
			RESULT.append(" . ");//只需满足一个的OR条件
		}

		if (OPTIONAL != null) {
			RESULT.append("OPTIONAL {");
			for (int i = 0; i < OPTIONAL.length; i++) {
				RESULT.append(OPTIONAL[i] + " . ");
			}
			RESULT.append(" } . ");//可选的条件（用于内容不存在的时候）
		}

		if (RANarg != null && ran1 != null && ran2 != null) {
			RESULT.append("FILTER(" + RANarg + " ");
			if (ran1 != null) {
				RESULT.append("> \"" + ran1 + "\"");
				if (ran2 != null) {
					RESULT.append(" && " + RANarg + " ");
				}
			}
			if (ran2 != null) {
				RESULT.append("< \"" + ran2 + "\"");
			}
			RESULT.append(") . ");//范围条件
		}

		if (EXISTarg != null && EXIST != null) {
			RESULT.append("FILTER regex (" + EXISTarg + ", \"" + EXIST + "\")");//必须包含某一字符串的条件
		}

		RESULT.append("} ");

		if (ORDERBY != null) {
			RESULT.append("ORDER BY " + ORDERBY + " ");//以什么为关键字排序
		}

		if (LIMIT != null) {
			RESULT.append("LIMIT " + LIMIT + " ");//最多输出多少条
		}

//		return RESULT.toString();
		return repoConn.prepareTupleQuery(QueryLanguage.SPARQL, RESULT.toString()).evaluate();
	}



	public TupleQueryResult Query(String[] PREFIX,
		            String[] Args, String[] ANDargs,
					String[] ORargs, String[] OPTIONAL,
					String RANarg, String ran1, String ran2,
					String EXISTarg, String EXIST,
					String ORDERBY,	String LIMIT,
					String Language) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		
		StringBuilder RESULT = new StringBuilder();

		if (PREFIX != null) {
			RESULT.append("PREFIX ");
			for (int i = 0; i < PREFIX.length; i++) {
				RESULT.append(PREFIX[i]);
			}//前缀
		}

		if (Args != null) {
			RESULT.append("SELECT ");
			for (int i = 0; i < Args.length; i++) {
				RESULT.append("?" + Args[i] + " ");
			}//需要输出的参数
		}

		RESULT.append("WHERE {");

		if (ANDargs != null) {
			for (int i = 0; i < ANDargs.length; i++) {
				RESULT.append(ANDargs[i] + "@" + Language + " . ");
			}//必须满足的条件
		}

		if (ORargs != null) {
			RESULT.append("{ " + ORargs[0] + "@" + Language + " } ");
			for (int i = 1; i < ORargs.length; i++) {
				RESULT.append("UNION { " + ORargs[i] + "@" + Language + " } ");
			}
			RESULT.append(" . ");//只需满足一个的OR条件
		}

		if (OPTIONAL != null) {
			RESULT.append("OPTIONAL {");
			for (int i = 0; i < OPTIONAL.length; i++) {
				RESULT.append(OPTIONAL[i] + "@" + Language + " . ");
			}
			RESULT.append(" } . ");//可选的条件（用于内容不存在的时候）
		}

		if (RANarg != null && ran1 != null && ran2 != null) {
			RESULT.append("FILTER(" + RANarg + " ");
			if (ran1 != null) {
				RESULT.append("> \"" + ran1 + "\"");
				if (ran2 != null) {
					RESULT.append(" && " + RANarg + " ");
				}
			}
			if (ran2 != null) {
				RESULT.append("< \"" + ran2 + "\"");
			}
			RESULT.append(") . ");//范围条件
		}

		if (EXISTarg != null && EXIST != null) {
			RESULT.append("FILTER regex (" + EXISTarg + ", \"" + EXIST + "\")");//必须包含某一字符串的条件
		}

		RESULT.append("} ");

		if (ORDERBY != null) {
			RESULT.append("ORDER BY " + ORDERBY + " ");//以什么为关键字排序
		}

		if (LIMIT != null) {
			RESULT.append("LIMIT " + LIMIT + " ");//最多输出多少条
		}

//		return RESULT.toString();
		return repoConn.prepareTupleQuery(QueryLanguage.SPARQL, RESULT.toString()).evaluate();
	}

	
	
//	public String query() {
//		
//	}
}
