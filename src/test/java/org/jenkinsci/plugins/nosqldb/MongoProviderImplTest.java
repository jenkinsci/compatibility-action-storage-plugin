package org.jenkinsci.plugins.nosqldb;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.DownloadConfigBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.runtime.Network;

/**
 *
 * @author Mads
 */
public class MongoProviderImplTest {
    
    private static final int PORT = 12345; 
    private static final String HOST = "localhost";
    private static final String COLLECTION = "test_collection";
    private static final String DBNAME = "test";
    private static MongodStarter starter;
    
    private MongodExecutable _mongodExe;
    private MongodProcess _mongod;
    private MongoClient _mongo;

 
    @Test
    public void basicInsertionWork() throws Exception {
        MongoProviderImpl testCollection = new MongoProviderImpl(HOST, PORT, DBNAME, COLLECTION, null, null);
        SimpleData data = testCollection.create(new SimpleData("test1", "test2"));        
        SimpleData extracted = testCollection.read(data.getId(), SimpleData.class);
        Assert.assertEquals(data, extracted);          
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @After
    public  void shutdown() {
        _mongod.stop();
        _mongodExe.stop();
    }
    
    @Before
    public void prepare() throws Exception {
        int port = 12345;
        try {
            Command command = Command.MongoD;

            IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaults(command)
                .artifactStore(new ArtifactStoreBuilder()
                    .defaults(command)
                    .download(new DownloadConfigBuilder()
                    .defaultsForCommand(command))
                    .executableNaming(new UserTempNaming()))
                .build();
            
            IMongodConfig mongodConfig;
            mongodConfig = new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(port, Network.localhostIsIPv6()))
                    
                    .build();
 
            starter = MongodStarter.getInstance(runtimeConfig);
            
            _mongodExe = starter.prepare(mongodConfig);
            _mongod = _mongodExe.start();
            _mongo = new MongoClient(HOST, PORT);
            DB db = _mongo.getDB(DBNAME);
            DBCollection col = db.createCollection(COLLECTION, new BasicDBObject());

        } catch (Exception ex) {
            throw ex;
        }
    }
}
