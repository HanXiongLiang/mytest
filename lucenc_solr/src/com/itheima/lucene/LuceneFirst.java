package com.itheima.lucene;

import java.io.File;


import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneFirst {

	@Test
	public void createIndex() throws Exception {
		// 1、创建一个Directory对象，指定索引库保存的位置。
		//保存在内存中
		//Directory directory = new RAMDirectory();
		// 位置可以是内存也可以是磁盘。一般就是保存在磁盘。
		Directory directory = FSDirectory.open(new File("F:\\lucencTest"));
		// 2、创建一个IndexWriter对象。:创建索引
		Analyzer analyzer = new IKAnalyzer();//创建分析器对象,
			//IndexWriterConfig:创建索引配置,  参数一:lucene版本,参数二:是IndexWriter的对象
		IndexWriterConfig cofig = new IndexWriterConfig(Version.LATEST,analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, cofig);
		//3.原始文档路路径(能获取多个)
		File file = new File("F:\\CloudMusic");
		File[] files = file.listFiles();
		//f代表F:\\CloudMusic文件下每一个的文件
		for (File f : files) {
			//获取文件名
			String fileName = f.getName();
			//获取文件内容
			String fileContext = FileUtils.readFileToString(f);
			//获取文件路径
			String filePath = f.getPath();
			//获取文件大小
			long fileSize  = FileUtils.sizeOf(f);
			
			//对应每个属性创建域
			//参数一: 域的名称
			//参数二: 域的值
			//参数三: 是否储存
			Field fieldName = new TextField("name", fileName, Store.YES);
			Field fieldContext = new TextField("context", fileContext, Store.YES);
			Field fieldPath = new TextField("path", filePath, Store.YES);
			Field fieldsize = new TextField("size", fileSize+"", Store.YES);
			// 4、向文档对象中添加域，文件的每个属性都对应一个域。
			Document document = new Document();
			document.add(fieldName);
			document.add(fieldContext);
			document.add(fieldPath);
			document.add(fieldsize);
			
			// 5、把文档对象写入索引库
			indexWriter.addDocument(document);
		}
		indexWriter.close();
	}
	
	//查询索引
	@Test
	public void testSearchIndex() throws Exception {
		//1.创建一个Directory对象，指定索引库保存的位置。
		Directory directory = FSDirectory.open(new File("F:\\lucencTest"));
		//2.创建一个IndexReader对象，以读的方式打开索引库
		IndexReader indexReader = DirectoryReader.open(directory);
		//3.创建一个IndexSearcher对象，构造参数IndexReader对象。
		IndexSearcher indexSercher = new IndexSearcher(indexReader); 
		//4.创建一个Query对象，指定要搜索的域及要搜索的关键词。
			//参数1：要查询的域 参数2：要查询的关键词
		Query query = new TermQuery(new Term("name", "新"));
		//5.执行查询，得到一个文档的id列表
				//参数1：查询对象 参数2：返回结果的最大数量
		TopDocs topDocs = indexSercher.search(query, 10);
		//取查询结果的总记录数
		System.out.println("总条数是:"+topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc s : scoreDocs) {
			//取文档的id
			int doc = s.doc;
			//6.根据id取文档对象
			Document document = indexSercher.doc(doc);
			//7.从文档对象中取域的内容。
			System.out.println(document.get("name"));
			System.out.println(document.get("context"));
			System.out.println(document.get("path"));
			System.out.println(document.get("size"));
			
		}
		
		//关闭indexReader
		indexReader.close();
	}
	
	//中文分析器   ⦁	分析器的分词效果

	@Test
	public void testTokenStream() throws Exception {
		//1.创建一个分析器对象
		Analyzer analyzer = new IKAnalyzer();
		// 2）调用分析器的对象的tokenStream方法获得一个TokenStream对象，参数就是要分析的的内容。
				//参数1：域的名称，可以是null
		TokenStream tokenStream = analyzer.tokenStream(null, 
				"2015年11月29日 - Lucene是传智播客apache软件基金会4 jakarta项目组的一个法轮功子项目,是一个开放源代码的全文检索引擎工具包");
		// 3）调用tokenStream对象的reset方法
		tokenStream.reset();
		// 4）给指针设置一个引用  添加一个引用，可以获得每个关键词
		CharTermAttribute addAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		// 5）遍历单词列表  就是你输入的信息
		while (tokenStream.incrementToken()) {
			// 6）取引用的内容
			System.out.println(addAttribute.toString());
		}
		// 7）关闭tokenStream对象
		tokenStream.close();
	}
	
}