package com.torodb.d2r;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.torodb.core.TableRef;
import com.torodb.core.TableRefFactory;
import com.torodb.core.impl.TableRefFactoryImpl;
import com.torodb.core.transaction.metainf.FieldType;
import com.torodb.core.transaction.metainf.ImmutableMetaCollection;
import com.torodb.core.transaction.metainf.ImmutableMetaDatabase;
import com.torodb.core.transaction.metainf.ImmutableMetaDocPart;
import com.torodb.core.transaction.metainf.ImmutableMetaField;
import com.torodb.core.transaction.metainf.ImmutableMetaSnapshot;

public class IdentifierFactoryImplTest {
	
	private IdentifierFactoryImpl identifierFactory;
	private TableRefFactory tableRefFactory= new TableRefFactoryImpl();

	@Before
	public void setUp() throws Exception {
	    this.identifierFactory = new IdentifierFactoryImpl(new MockIdentifierInterface());
	}
    
    @Test
    public void emptyDatabaseToIdentifierTest() {
        ImmutableMetaSnapshot metaSnapshot = new ImmutableMetaSnapshot.Builder().build();
        String identifier = identifierFactory.toDatabaseIdentifier(metaSnapshot, "");
        Assert.assertEquals("", identifier);
    }
    
    @Test
    public void unallowedDatabaseToIdentifierTest() {
        ImmutableMetaSnapshot metaSnapshot = new ImmutableMetaSnapshot.Builder().build();
        String identifier = identifierFactory.toDatabaseIdentifier(metaSnapshot, "unallowed_schema");
        Assert.assertEquals("_unallowed_schema", identifier);
    }
    
    @Test
    public void databaseToIdentifierTest() {
        ImmutableMetaSnapshot metaSnapshot = new ImmutableMetaSnapshot.Builder().build();
        String identifier = identifierFactory.toDatabaseIdentifier(metaSnapshot, "database");
        Assert.assertEquals("database", identifier);
    }
    
    @Test
    public void long128DatabaseToIdentifierTest() {
        ImmutableMetaSnapshot metaSnapshot = new ImmutableMetaSnapshot.Builder().build();
        String identifier = identifierFactory.toDatabaseIdentifier(metaSnapshot, 
                  "database_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long");
        Assert.assertEquals("database_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long", identifier);
    }
    
    @Test
    public void longForCounterDatabaseToIdentifierTest() {
        ImmutableMetaSnapshot metaSnapshot = new ImmutableMetaSnapshot.Builder().build();
        String identifier = identifierFactory.toDatabaseIdentifier(metaSnapshot, 
                  "database_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long");
        Assert.assertEquals("database_long_long_long_long_long_long_long_long_long_long_longong_long_long_long_long_long_long_long_long_long_long_long_long_1", identifier);
    }
    
    @Test
    public void longForCounterWithCollisionCharacterDatabaseToIdentifierTest() {
        ImmutableMetaSnapshot metaSnapshot = new ImmutableMetaSnapshot.Builder()
                .add(new ImmutableMetaDatabase.Builder("database_collider", 
                        "database_long_long_long_long_long_long_long_long_long_long_longong_long_long_long_long_long_long_long_long_long_long_long_long_1"))
                .build();
        String identifier = identifierFactory.toDatabaseIdentifier(metaSnapshot, 
                "database_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long");
        Assert.assertEquals("database_long_long_long_long_long_long_long_long_long_long_longong_long_long_long_long_long_long_long_long_long_long_long_long_2", identifier);
    }
    
    @Test
    public void emptyCollectionDocPartRootToIdentifierTest() {
        ImmutableMetaDatabase metaDatabase = new ImmutableMetaDatabase.Builder("database", "database")
                .build();
        String identifier = identifierFactory.toDocPartIdentifier(metaDatabase, "", createTableRef());
        Assert.assertEquals("", identifier);
    }
    
    @Test
    public void unallowedCollectionDocPartRootToIdentifierTest() {
        ImmutableMetaDatabase metaDatabase = new ImmutableMetaDatabase.Builder("database", "database")
                .build();
        String identifier = identifierFactory.toDocPartIdentifier(metaDatabase, "unallowed_table", createTableRef());
        Assert.assertEquals("_unallowed_table", identifier);
    }
    
    @Test
    public void docPartRootToIdentifierTest() {
        ImmutableMetaDatabase metaDatabase = new ImmutableMetaDatabase.Builder("database", "database")
                .build();
        String identifier = identifierFactory.toDocPartIdentifier(metaDatabase, "collecti", createTableRef());
        Assert.assertEquals("collecti", identifier);
    }
    
    @Test
    public void docPartObjectChildToIdentifierTest() {
        ImmutableMetaDatabase metaDatabase = new ImmutableMetaDatabase.Builder("database", "database")
                .build();
        String identifier = identifierFactory.toDocPartIdentifier(metaDatabase, "collecti", createTableRef("object"));
        Assert.assertEquals("collecti_object", identifier);
    }
    
    @Test
    public void docPartArrayChildToIdentifierTest() {
        ImmutableMetaDatabase metaDatabase = new ImmutableMetaDatabase.Builder("database", "database")
                .build();
        String identifier = identifierFactory.toDocPartIdentifier(metaDatabase, "collecti", createTableRef("array"));
        Assert.assertEquals("collecti_array", identifier);
    }
    
    @Test
    public void docPartArrayInArrayChildToIdentifierTest() {
        ImmutableMetaDatabase metaDatabase = new ImmutableMetaDatabase.Builder("database", "database")
                .build();
        String identifier = identifierFactory.toDocPartIdentifier(metaDatabase, "collecti", createTableRef("array", "2"));
        Assert.assertEquals("collecti_array$2", identifier);
    }
    
    @Test
    public void docPartObjectArrayInArrayObjectToIdentifierTest() {
        ImmutableMetaDatabase metaDatabase = new ImmutableMetaDatabase.Builder("database", "database")
                .build();
        String identifier = identifierFactory.toDocPartIdentifier(metaDatabase, "collecti", createTableRef("object", "array", "2", "object"));
        Assert.assertEquals("collecti_object_array$2_object", identifier);
    }
    
    @Test
    public void emptyDocPartToIdentifierTest() {
        ImmutableMetaDatabase metaDatabase = new ImmutableMetaDatabase.Builder("database", "database")
                .build();
        String identifier = identifierFactory.toDocPartIdentifier(metaDatabase, "collecti", createTableRef(""));
        Assert.assertEquals("collecti_", identifier);
    }
    
    @Test
    public void long128DocPartToIdentifierTest() {
        ImmutableMetaDatabase metaDatabase = new ImmutableMetaDatabase.Builder("database", "database")
                .build();
        String identifier = identifierFactory.toDocPartIdentifier(metaDatabase, 
                "collecti", 
                createTableRef("long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long"));
        Assert.assertEquals("collecti_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long", identifier);
    }
    
    @Test
    public void longForCounterDocPartToIdentifierTest() {
        ImmutableMetaDatabase metaDatabase = new ImmutableMetaDatabase.Builder("database", "database")
                .build();
        String identifier = identifierFactory.toDocPartIdentifier(metaDatabase, 
                "collecti", 
                createTableRef("long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long"));
        Assert.assertEquals("collecti_long_long_long_long_long_long_long_long_long_long_longong_long_long_long_long_long_long_long_long_long_long_long_long_1", identifier);
    }
    
    @Test
    public void longForCounterWithCollisionCharacterDocPartToIdentifierTest() {
        ImmutableMetaDatabase metaDatabase = new ImmutableMetaDatabase.Builder("database", "database")
                .add(new ImmutableMetaCollection.Builder("collecti", "collecti")
                        .add(new ImmutableMetaDocPart.Builder(createTableRef(), 
                                "collecti_long_long_long_long_long_long_long_long_long_long_longong_long_long_long_long_long_long_long_long_long_long_long_long_1")
                                .build())
                        .build())
                .build();
        String identifier = identifierFactory.toDocPartIdentifier(metaDatabase, 
                "collecti", 
                createTableRef("long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long"));
        Assert.assertEquals("collecti_long_long_long_long_long_long_long_long_long_long_longong_long_long_long_long_long_long_long_long_long_long_long_long_2", identifier);
    }
    
    @Test
    public void emptyFieldToIdentifierTest() {
        ImmutableMetaDocPart metaDocPart = new ImmutableMetaDocPart.Builder(createTableRef(), 
                "docpart")
                .build();
        String identifier = identifierFactory.toFieldIdentifier(metaDocPart, FieldType.STRING, "");
        Assert.assertEquals("_s", identifier);
    }
    
    @Test
    public void unallowedFieldToIdentifierTest() {
        ImmutableMetaDocPart metaDocPart = new ImmutableMetaDocPart.Builder(createTableRef(), 
                "docpart")
                .build();
        String identifier = identifierFactory.toFieldIdentifier(metaDocPart, FieldType.STRING, "unallowed_column");
        Assert.assertEquals("_unallowed_column_s", identifier);
    }
    
    @Test
    public void fieldToIdentifierTest() {
        ImmutableMetaDocPart metaDocPart = new ImmutableMetaDocPart.Builder(createTableRef(), 
                "docpart")
                .build();
        String identifier = identifierFactory.toFieldIdentifier(metaDocPart, FieldType.STRING, "field");
        Assert.assertEquals("field_s", identifier);
    }
    
    @Test
    public void long128FieldToIdentifierTest() {
        ImmutableMetaDocPart metaDocPart = new ImmutableMetaDocPart.Builder(createTableRef(), 
                "docpart")
                .build();
        String identifier = identifierFactory.toFieldIdentifier(metaDocPart, FieldType.STRING, 
                "field__long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long");
        Assert.assertEquals("field__long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_s", identifier);
    }
    
    @Test
    public void longForCounterFieldToIdentifierTest() {
        ImmutableMetaDocPart metaDocPart = new ImmutableMetaDocPart.Builder(createTableRef(), 
                "docpart")
                .build();
        String identifier = identifierFactory.toFieldIdentifier(metaDocPart, FieldType.STRING, 
                "field____long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long");
        Assert.assertEquals("field____long_long_long_long_long_long_long_long_long_long_lonng_long_long_long_long_long_long_long_long_long_long_long_long_1_s", identifier);
    }
    
    @Test
    public void longForCounterWithCollisionCharacterFieldToIdentifierTest() {
        ImmutableMetaDocPart metaDocPart = new ImmutableMetaDocPart.Builder(createTableRef(), 
                "docpart")
                .add(new ImmutableMetaField("field_collider", 
                        "field____long_long_long_long_long_long_long_long_long_long_lonng_long_long_long_long_long_long_long_long_long_long_long_long_1_s", 
                        FieldType.STRING))
                .build();
        String identifier = identifierFactory.toFieldIdentifier(metaDocPart, FieldType.STRING, 
                "field____long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long_long");
        Assert.assertEquals("field____long_long_long_long_long_long_long_long_long_long_lonng_long_long_long_long_long_long_long_long_long_long_long_long_2_s", identifier);
    }
    
    private TableRef createTableRef(String...names) {
        TableRef tableRef = tableRefFactory.createRoot();
        
        for (String name : names) {
            try {
                int index = Integer.parseInt(name);
                tableRef = tableRefFactory.createChild(tableRef, index);
            } catch(NumberFormatException ex) {
                tableRef = tableRefFactory.createChild(tableRef, name);
            }
        }
        
        return tableRef;
    }
}
