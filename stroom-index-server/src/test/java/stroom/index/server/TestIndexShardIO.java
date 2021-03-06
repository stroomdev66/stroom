/*
 * Copyright 2017 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stroom.index.server;

import org.apache.lucene.document.Document;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import stroom.index.shared.Index;
import stroom.index.shared.IndexField;
import stroom.index.shared.IndexFields;
import stroom.index.shared.IndexFieldsMap;
import stroom.index.shared.IndexShard;
import stroom.node.shared.Volume;
import stroom.search.server.shard.IndexShardSearcher;
import stroom.search.server.shard.IndexShardSearcherImpl;
import stroom.streamstore.server.fs.FileSystemUtil;
import stroom.util.test.StroomJUnit4ClassRunner;
import stroom.util.test.StroomUnitTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;

@RunWith(StroomJUnit4ClassRunner.class)
public class TestIndexShardIO extends StroomUnitTest {

    //    private static final IndexShardService INDEX_SHARD_SERVICE = new MockIndexShardService();
    private static final IndexFields INDEX_FIELDS = IndexFields.createStreamIndexFields();
    //    private static final IndexShardWriterCache INDEX_SHARD_WRITER_CACHE = new MockIndexShardWriterCache();
//    private static final IndexShardManager INDEX_SHARD_MANAGER = new MockIndexShardManager();
    private static final IndexConfig INDEX_CONFIG;

    static {
        INDEX_FIELDS.add(IndexField.createField("Id"));
        INDEX_FIELDS.add(IndexField.createField("Test"));
        INDEX_FIELDS.add(IndexField.createField("Id2"));

        final Index index = new Index();
        index.setName("Test");
        INDEX_CONFIG = new IndexConfig(index, INDEX_FIELDS, new IndexFieldsMap(INDEX_FIELDS));
    }

    public static void main(final String[] args) {
        for (final Object s : System.getProperties().keySet()) {
            System.out.println(s + "=" + System.getProperty((String) s));
        }
    }

    private Document buildDocument(final int id) {
        final Document d = new Document();
        d.add(FieldFactory.create(IndexField.createIdField("Id"), id));
        d.add(FieldFactory.create(IndexField.createField("Test"), "Test"));
        d.add(FieldFactory.create(IndexField.createIdField("Id2"), id));

        return d;
    }

    @Test
    public void testOpenCloseManyWrite() throws IOException {
        final Volume volume = new Volume();
        volume.setPath(getCurrentTestDir().getAbsolutePath());
        final Index index = new Index();
        index.setName("Test");

        final IndexShard idx1 = new IndexShard();
        idx1.setId(1L);
        idx1.setIndex(index);
        idx1.setPartition("all");
        idx1.setVolume(volume);
        idx1.setIndexVersion(LuceneVersionUtil.getCurrentVersion());

        // Clean up from previous tests.
        final Path dir = IndexShardUtil.getIndexPath(idx1);
        FileSystemUtil.deleteDirectory(dir);

        for (int i = 1; i <= 10; i++) {
            final IndexShardWriter writer = new IndexShardWriterImpl(null, INDEX_CONFIG, idx1);
            writer.flush();
            writer.addDocument(buildDocument(i));
            writer.flush();
            Assert.assertEquals(i, writer.getDocumentCount());
            writer.destroy();
        }
    }

    @Test
    public void testOpenCloseManyReadWrite() throws IOException {
        final Index index = new Index();
        index.setName("Test");

        final Volume volume = new Volume();
        volume.setPath(getCurrentTestDir().getAbsolutePath());
        final IndexShard idx1 = new IndexShard();
        idx1.setIndex(index);
        idx1.setPartition("all");
        idx1.setId(1L);
        idx1.setVolume(volume);
        idx1.setIndexVersion(LuceneVersionUtil.getCurrentVersion());

        // Clean up from previous tests.
        final Path dir = IndexShardUtil.getIndexPath(idx1);
        FileSystemUtil.deleteDirectory(dir);

        for (int i = 1; i <= 10; i++) {
            final IndexShardWriter writer = new IndexShardWriterImpl(null, INDEX_CONFIG, idx1);
            writer.addDocument(buildDocument(i));
            writer.destroy();
            Assert.assertEquals(i, writer.getDocumentCount());

            final IndexShardSearcher searcher = new IndexShardSearcherImpl(idx1);
            Assert.assertEquals(i, searcher.getReader().maxDoc());
            searcher.destroy();
        }
    }

    @Test
    public void testFailToCloseAndReopen() throws IOException {
        final Index index = new Index();
        index.setName("Test");

        final Volume volume = new Volume();
        volume.setPath(getCurrentTestDir().getAbsolutePath());
        final IndexShard idx1 = new IndexShard();
        idx1.setIndex(index);
        idx1.setPartition("all");
        idx1.setId(1L);
        idx1.setVolume(volume);
        idx1.setIndexVersion(LuceneVersionUtil.getCurrentVersion());

        // Clean up from previous tests.
        final Path dir = IndexShardUtil.getIndexPath(idx1);
        FileSystemUtil.deleteDirectory(dir);

        final IndexShardWriter writer = new IndexShardWriterImpl(null, INDEX_CONFIG, idx1);

        for (int i = 1; i <= 10; i++) {
            writer.addDocument(buildDocument(i));
            writer.flush();
            Assert.assertEquals(i, writer.getDocumentCount());
        }

        writer.destroy();
    }

    @Test
    public void testFailToCloseFlushAndReopen() throws IOException {
        final Index index = new Index();
        index.setName("Test");

        final Volume volume = new Volume();
        volume.setPath(getCurrentTestDir().getAbsolutePath());
        final IndexShard idx1 = new IndexShard();
        idx1.setIndex(index);
        idx1.setPartition("all");
        idx1.setId(1L);
        idx1.setVolume(volume);
        idx1.setIndexVersion(LuceneVersionUtil.getCurrentVersion());

        // Clean up from previous tests.
        final Path dir = IndexShardUtil.getIndexPath(idx1);
        FileSystemUtil.deleteDirectory(dir);

        final IndexShardWriter writer = new IndexShardWriterImpl(null, INDEX_CONFIG, idx1);

        for (int i = 1; i <= 10; i++) {
            writer.addDocument(buildDocument(i));
            writer.flush();
            Assert.assertEquals("No docs flushed ", i, writer.getDocumentCount());
        }

        writer.destroy();
    }

    @Test
    public void testWriteLoadsNoFlush() throws IOException {
        final Index index = new Index();
        index.setName("Test");

        final Volume volume = new Volume();
        final File testDir = getCurrentTestDir();
        volume.setPath(testDir.getAbsolutePath());
        FileSystemUtil.deleteDirectory(testDir);
        final IndexShard idx1 = new IndexShard();
        idx1.setIndex(index);
        idx1.setPartition("all");
        idx1.setId(1L);
        idx1.setVolume(volume);
        idx1.setIndexVersion(LuceneVersionUtil.getCurrentVersion());

        // Clean up from previous tests.
        final Path dir = IndexShardUtil.getIndexPath(idx1);
        FileSystemUtil.deleteDirectory(dir);

        final IndexShardWriter writer = new IndexShardWriterImpl(null, INDEX_CONFIG, idx1);

        Long lastSize = null;

        final HashSet<Integer> flushSet = new HashSet<>();

        for (int i = 1; i <= 100; i++) {
            writer.addDocument(buildDocument(i));
//            writer.sync();
            // System.out.println(writer.getIndexWriter().ramSizeInBytes());

            final Long newSize = idx1.getFileSize();

            if (newSize != null) {
                if (lastSize != null) {
                    if (!lastSize.equals(newSize)) {
                        flushSet.add(Integer.valueOf(i));
                    }
                }
                lastSize = newSize;
            }

        }
        // TODO - TO Fix
        // Assert.assertEquals("Some flush happened before we expected it "
        // + flushSet, 0, flushSet.size());

        writer.destroy();
        Assert.assertTrue("Expected not to flush", flushSet.isEmpty());
        // Assert.assertEquals("Expected to flush every 2048 docs...","[2048,
        // 6144, 4096, 8192]",
        // flushSet.toString());
    }
}
