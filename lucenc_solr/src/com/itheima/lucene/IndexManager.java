package com.itheima.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class IndexManager {

	private IndexWriter indexWriter;
	@Before
	public void init() throws Exception {
		//1）创建一个Directory对象，指定索引库的位置
				Directory directory = FSDirectory.open(new File("F:\\lucencTest"));
		//2）创建一个IndexWriter对象，directory,IndexWriterConfig
				indexWriter = new IndexWriter(directory, new IndexWriterConfig(Version.LATEST,new IKAnalyzer()));
	}

	
	@Test
	public void testAddDocument() throws Exception{
	//3）创建一个document对象
		Document document = new Document();
		Field fieldName = new TextField("name", "测试文档01",Store.YES);
		Field fieldContent = new TextField("context01", "测试文档01的内容",Store.YES);
		Field fieldContent2 = new TextField("context02", "测试文档01的第二个内容域",Store.YES);
		Field fieldSize = new TextField("size", 1000l+"",Store.YES);
	//4）向Document对象中添加域
		document.add(fieldName);
		document.add(fieldContent);
		document.add(fieldContent2);
		document.add(fieldSize);
	//5）把文档对象写入索引库
		indexWriter.addDocument(document);
		//6）关闭IndexWriter对象
		indexWriter.close();
	}
	
	//索引库删除
	//指定查询条件删除
	@Test
	public void deleteIndexByQuery() throws Exception{
		// 1）创建一个IndexWriter对象
		// 2）创建一个Query对象，可以使用TermQuery
		Query query  = new TermQuery(new Term("name","森"));
		indexWriter.deleteDocuments(query);
		indexWriter.close();
	}
	
	//查询索引库
	@Test
	public void updateDocument() throws Exception{
		// 1）创建一个IndexWriter对象
		// 2）创建一个Document对象，并添加域
		Document document = new Document();
		document.add(new TextField("name", "更新之后的文档01",Store.YES));
		document.add(new TextField("content", "更新之后的文档01内容",Store.YES));
		document.add(new TextField("name2", "更新之后的文档01",Store.YES));
		// 3）使用IndexWriter对象的update方法更新文件。
		indexWriter.updateDocument(new Term("name","地球"), document);
		indexWriter.close();
	}
	
}
