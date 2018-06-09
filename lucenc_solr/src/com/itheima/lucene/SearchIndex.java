package com.itheima.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class SearchIndex {

	private IndexSearcher indexSearcher;
	@Before
	public void init() throws Exception {
		// 1、创建一个IndexReader对象，以读的方式打开索引库。
		IndexReader indexReader = DirectoryReader.open(FSDirectory.open(new File("F:\\lucencTest"))); 
		// 2、创建一个IndexSearcher对象。
		indexSearcher = new IndexSearcher(indexReader);
	}
	
	private void searchResult(Query query) throws Exception {
		// 4、执行查询
		TopDocs topDocs = indexSearcher.search(query, 10);
		// 5、取查询结果的总记录数
		System.out.println("查询结果的总记录数：" + topDocs.totalHits);
		// 6、打印结果
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			//根据文档的id取文档对象
			Document document = indexSearcher.doc(scoreDoc.doc);
			System.out.println(document.get("name"));
			System.out.println(document.get("content"));
			System.out.println(document.get("path"));
			System.out.println(document.get("size"));
		}
		// 7、关闭IndexReader对象。
		indexSearcher.getIndexReader().close();
	}

	@Test
	public void testMatchAllDocsQuery() throws Exception {
		
		// 3、创建一个Query对象，创建一个MatchAllDocsQuery
		Query query = new MatchAllDocsQuery();
		System.out.println(query);
		searchResult(query);
	}
	
	@Test
	public void testNumericRangeQuery() throws Exception {
		//创建一个Query对象
		//参数1：要查询的域 
		//参数2：范围的最小值 
		//参数3：范围的最大值
		// 参数4：是否包含最小值
		// 参数5：是否包含最大值
		Query query = NumericRangeQuery.newLongRange("size", 0l, 100000l, true, false);
		System.out.println(query.toString());
		//执行查询
		searchResult(query);
	}
	
	//组合条件查询
	@Test
	public void testBooleanQuery() throws Exception {
		//创建一个Query对象
		BooleanQuery query = new BooleanQuery();
		
		//条件一:
		Query query1 = new TermQuery(new Term("name","文档"));
		//条件二:
		Query query2 = new TermQuery(new Term("context","杀"));
		//组合条件
		query.add(query1,Occur.SHOULD);
		query.add(query2,Occur.MUST_NOT);
		System.out.println(query);
		//执行查询
		searchResult(query);
	}
	//可以形成像  搜索框一样的查询   用到QueryParser(单一搜索)
	@Test
	public void testQueryParser() throws Exception {
		// 使用方法：
		// 1）添加QueryParser的jar包。
		// 2）创建一个QueryParser对象，两个参数。参数1：默认搜索域，参数2：分析器对象
		QueryParser qp = new QueryParser("context", new IKAnalyzer());
		// 3）使用QueryParser对象的parse方法创建一个Query对象，参数要搜索的内容，可以是一句话。
		//Query query = qp.parse("lucene是一个基于java开发的全文检索工具包");
		//Query query = qp.parse("name:文档");
		Query query = qp.parse("name:文档  -context:杀");
		// 4）执行查询
		System.out.println(query.toString());
		searchResult(query);
	}
	
	//指定多个搜索域(重点)
	//MultiFieldQueryParser(指定多个默认搜索域)
	@Test
	public void testMultiFieldQueryParser() throws Exception{
		//创建MultiFieldQueryParser查询对象
		//参数一:表示能设置多个字符串类型的搜索域
		String[] fields = {"name","context"};
		//参数二:表示分析器对象
		MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, new IKAnalyzer());
		//Query query = queryParser.parse("lucene是一个基于java开发的全文检索工具包");
		Query query = queryParser.parse("name:文档");
		
		System.out.println(query.toString());
		//执行查询
		searchResult(query);
	}
	
	//相关度排序(匹配度最高的排序最高)
	/*
	 * Lucene中有两个指标影响相关度
	 * tf: 关键词在文档中出现的频率,tf越高越重要
	 * df: 关键词的多个文档中出现的频率,df越高越不重要
	 * 可以在添加文档时设置域的boost值(默认是1.0)
	 */
}
