package org.jenkinsci.plugins.externaldata;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.DownloadConfigBuilder;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.runtime.Network;
import java.lang.reflect.Method;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 *
 * @author Mads
 */
public class MongoProviderImplTest {
    
    private static final int PORT = 12345; 
    private static final String HOST = "localhost";
    private static final String COLLECTION = "test_collection";
    private static final String DBNAME = "test";
    private static final MongodStarter starter;
    
    @Rule
    public TestName currentTestName = new TestName();
    
    
    static {
        starter = MongodStarter.getInstance(new RuntimeConfigBuilder()
                .defaults(Command.MongoD)
                .artifactStore(new ArtifactStoreBuilder()
                    .defaults(Command.MongoD)
                    .download(new DownloadConfigBuilder()
                    .defaultsForCommand(Command.MongoD))
                    .executableNaming(new UserTempNaming()))
                .build());
    }
    
    private MongodExecutable _mongodExe;
    private MongodProcess _mongod;
    private MongoClient _mongo;

 
    @Test
    public void createTestSuccess() throws Exception {
        MongoProviderImpl testCollection = new MongoProviderImpl(HOST, PORT, DBNAME, COLLECTION, null, null);
        SimpleData data = new SimpleData("test1", "test2");
        SimpleData dataExtracted = testCollection.create(data);        
        assertNotNull(dataExtracted.getId());
    }
    
    @Test
    public void readTestSuccess() throws Exception {
        MongoProviderImpl testCollection = new MongoProviderImpl(HOST, PORT, DBNAME, COLLECTION, null, null);
        SimpleData data = testCollection.create(new SimpleData("test1", "test2"));        
        SimpleData extracted = testCollection.read(data.getId(), SimpleData.class);
        assertThat(data, is(extracted));
    }
    
    @Test
    public void readManyTestSuccess() throws Exception {
        MongoProviderImpl testCollection = new MongoProviderImpl(HOST, PORT, DBNAME, COLLECTION, null, null);
        SimpleData data = testCollection.create(new SimpleData("test1", "test2"));        
        SimpleData data2 = testCollection.create(new SimpleData("test1", "moreData"));
        List<SimpleData> allData = testCollection.readMany(new BasicDBObject("moreData", "test1"), SimpleData.class);
        assertEquals(2, allData.size());
        assertTrue(allData.contains(data));
        assertTrue(allData.contains(data2));
    }
    
    @Test(expected = ExternalDataException.class)
    @SkipSetup
    public void testConnectionFail() throws Exception {
        MongoProviderImpl testCollection = new MongoProviderImpl(HOST, PORT, DBNAME, COLLECTION, null, null);
        SimpleData data = testCollection.create(new SimpleData("test1", "test2"));          
    }
    
    @Test
    public void countTestSuccess() throws Exception {
        MongoProviderImpl testCollection = new MongoProviderImpl(HOST, PORT, DBNAME, COLLECTION, null, null);
        SimpleData data = testCollection.create(new SimpleData("test1", "test2"));        
        SimpleData data2 = testCollection.create(new SimpleData("test1", "moreData"));
        int count = testCollection.count(new BasicDBObject("moreData", "test1"));
        assertEquals(2, count);
    }
    
    @Test
    public void listAndSort() throws Exception {
        MongoProviderImpl testCollection = new MongoProviderImpl(HOST, PORT, DBNAME, COLLECTION, null, null);
        SimpleData dataA = testCollection.create(new SimpleData("A", "A2"));        
        SimpleData dataC = testCollection.create(new SimpleData("C", "C2"));        
        SimpleData dataB = testCollection.create(new SimpleData("B", "B2"));
        SimpleData dataD = testCollection.create(new SimpleData("D", "D2"));
        
        DBObject q = QueryBuilder.start().or(
                new BasicDBObject("moreData", "A"), 
                new BasicDBObject("moreData", "B"),
                new BasicDBObject("moreData", "C"),
                new BasicDBObject("moreData", "D")).get();
        
        System.out.println("QUERY: "+q);
        
        List<DBObject> obj = testCollection.listAndSort(q, new BasicDBObject("moreData", 1));
        assertTrue(!obj.isEmpty());
        
        assertEquals("A", obj.get(0).get("moreData"));
        assertEquals("B", obj.get(1).get("moreData"));
        assertEquals("C", obj.get(2).get("moreData"));
        assertEquals("D", obj.get(3).get("moreData"));
        
        List<DBObject> obj2 = testCollection.listAndSort(q, new BasicDBObject("moreData", -1));
        assertTrue(!obj2.isEmpty());
        
        assertEquals("D", obj2.get(0).get("moreData"));
        assertEquals("C", obj2.get(1).get("moreData"));
        assertEquals("B", obj2.get(2).get("moreData"));
        assertEquals("A", obj2.get(3).get("moreData"));
        
    }
    
    
    @After
    public void tearDown() throws Exception { 
        Method m = MongoProviderImplTest.class.getMethod(currentTestName.getMethodName(), (Class<?>[]) null);            
        if(m.getAnnotation(SkipSetup.class) == null) {
            _mongod.stop();   
            _mongodExe.stop();
        }
    }
    
    @Before
    public void setUp() throws Exception {
        int port = 12345;
        try {
            
            Method m = MongoProviderImplTest.class.getMethod(currentTestName.getMethodName(), (Class<?>[]) null);
            
            if(m.getAnnotation(SkipSetup.class) == null) {            
                IMongodConfig mongodConfig;
                mongodConfig = new MongodConfigBuilder()
                        .version(Version.Main.PRODUCTION)
                        .net(new Net(port, Network.localhostIsIPv6()))                    
                        .build();
                _mongodExe = starter.prepare(mongodConfig);

                _mongod = _mongodExe.start();
                _mongo = new MongoClient(HOST, PORT);

                DB db = _mongo.getDB(DBNAME);
                DBCollection col = db.createCollection(COLLECTION, new BasicDBObject());          
            }

        } catch (Exception ex) {
            throw ex;
        }
    }
}
