package org.xpen.hello.search.lucene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DoubleValuesSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Shape;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Lucene spatial演示
 * 出处：官方junit代码
 */
public class SpatialExample {


    @Test
    public void test() throws Exception {
        init();
        indexPoints();
        search();
    }

    /** Spatial4j上下文 */
    private SpatialContext ctx;// "ctx" is the conventional variable name

    /** 提供索引和查询模型的策略接口 */
    private SpatialStrategy strategy;

    /** 索引目录 */
    private Directory directory;

    protected void init() {
        // SpatialContext也可以通过SpatialContextFactory工厂类来构建
        this.ctx = SpatialContext.GEO;

        //网格最大11层, geohash的精度
        int maxLevels = 11;
        
        //Spatial Tiers
        SpatialPrefixTree grid = new GeohashPrefixTree(ctx, maxLevels);

        this.strategy = new RecursivePrefixTreeStrategy(grid, "myGeoField");

        this.directory = new RAMDirectory();
    }

    private void indexPoints() throws Exception {
        IndexWriterConfig iwConfig = new IndexWriterConfig(new SmartChineseAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, iwConfig);
        
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        ObjectMapper mapper = new ObjectMapper(jsonFactory);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(
                SpatialExample.class.getClassLoader().getResourceAsStream("search/lucene/shop.json")));
        
        String line = null;  
        while ((line = br.readLine()) != null) {
            ShopBean shopBean = mapper.readValue(line, ShopBean.class);
            //这里的x,y即经纬度，x为Longitude(经度),y为Latitude(纬度)  
            Document document = newSampleDocument(shopBean.getId(), shopBean.getName(), ctx.makePoint(shopBean.getLongitude(), shopBean.getLatitude()));
            indexWriter.addDocument(document);
        }  

        indexWriter.close();
    }

    private Document newSampleDocument(int id, String title, Shape... shapes) {
        Document doc = new Document();
        doc.add(new StoredField("id", id));
        doc.add(new NumericDocValuesField("id", id));
        doc.add(new TextField("name", title, Store.YES));
        
        // Potentially more than one shape in this field is supported by some
        // strategies; see the javadocs of the SpatialStrategy impl to see.
        for (Shape shape : shapes) {
            for (Field f : strategy.createIndexableFields(shape)) {
                doc.add(f);
            }
            // store it too; the format is up to you
            // (assume point in this example)
            Point pt = (Point) shape;
            doc.add(new StoredField(strategy.getFieldName(), pt.getX() + " " + pt.getY()));
        }

        return doc;
    }
    
    private void search() throws Exception {
        searchInner("密室");
        System.out.println("----------------");
        searchInner("咖啡");
    }

    private void searchInner(String keyword) throws Exception {
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        // --Filter by circle (<= distance from a point)
        // Search with circle
        // note: SpatialArgs can be parsed from a string
        Point pt = ctx.makePoint(121.41791, 31.21867);
        // the distance in km
        DoubleValuesSource valueSource = strategy.makeDistanceValueSource(pt, DistanceUtils.DEG_TO_KM);
        //按距离由近及远排序
        Sort distSort = new Sort(valueSource.getSortField(false)).rewrite(indexSearcher); // false=asc                                                                                         dist
                                                                                          
        SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects,
                ctx.makeCircle(pt, DistanceUtils.dist2Degrees(3.0, DistanceUtils.EARTH_MEAN_RADIUS_KM)));
        Query query = strategy.makeQuery(args);
        
        BooleanQuery.Builder bqb = new BooleanQuery.Builder();
        bqb.add(query, BooleanClause.Occur.MUST);
        bqb.add(new TermQuery(new Term("name", keyword)), BooleanClause.Occur.MUST);
        
        TopDocs docs = indexSearcher.search(bqb.build(), 20, distSort);
        printDocs(indexSearcher, docs, args);

        indexReader.close();
    }

    private void printDocs(IndexSearcher indexSearcher, TopDocs docs, SpatialArgs args) throws IOException {
        for (int i = 0; i < docs.totalHits; i++) {
            Document doc = indexSearcher.doc(docs.scoreDocs[i].doc);
            System.out.print(doc.getField("id").numericValue().intValue());
            System.out.print(":" + doc.getField("name").stringValue());
            
            //计算距离
            String docStr = doc.getField(strategy.getFieldName()).stringValue();
            // assume docStr is "x y" as written in newSampleDocument()
            int spaceIdx = docStr.indexOf(' ');
            double x = Double.parseDouble(docStr.substring(0, spaceIdx));
            double y = Double.parseDouble(docStr.substring(spaceIdx + 1));
            double docDistDEG = ctx.calcDistance(args.getShape().getCenter(), x, y);
            
            System.out.print("(" + DistanceUtils.degrees2Dist(docDistDEG, DistanceUtils.EARTH_MEAN_RADIUS_KM) + "km)");
            
            System.out.println();
        }
    }

}
