package com.amdocs.aia.il.common.model.configuration.tables;//package com.amdocs.aia.il.common.model.configuration.tables;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//
//@ExtendWith(MockitoExtension.class)
//public class IndexConfigurationTest {
//
//    @Test
//    void test_pojo_structure() {
//        IndexConfiguration indexConfiguration = getIndexConfiguration();
//        Assertions.assertEquals("indexName", indexConfiguration.getIndexName());
//        Assertions.assertEquals(3, indexConfiguration.getColumnNames().size());
//        Assertions.assertTrue(indexConfiguration.getIsUnique());
//        indexConfiguration = new IndexConfiguration("indexNameNew", Arrays.asList("col1", "col2"), Boolean.FALSE);
//        Assertions.assertEquals("indexNameNew", indexConfiguration.getIndexName());
//        Assertions.assertEquals(2, indexConfiguration.getColumnNames().size());
//        Assertions.assertFalse(indexConfiguration.getIsUnique());
//    }
//
//}
