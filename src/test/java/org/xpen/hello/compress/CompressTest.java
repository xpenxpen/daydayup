package org.xpen.hello.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.Test;

public class CompressTest {

    //测试压缩
    @Test
    public void testCompress() throws Exception {
        /* Create Output Stream that will have final zip files */
        OutputStream zip_output = new FileOutputStream(new File("target/zip_output_test.zip"));
        /* Create Archive Output Stream that attaches File Output Stream / and specifies type of compression */
        ArchiveOutputStream logical_zip = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, zip_output);
        /* Create Archieve entry - write header information*/
        logical_zip.putArchiveEntry(new ZipArchiveEntry("discussionForumHome.xml"));
        /* Copy input file */
        IOUtils.copy(new FileInputStream(new File("src/test/resources/xml/xslt/1/discussionForumHome.xml")), logical_zip);
        /* Close Archieve entry, write trailer information */
        logical_zip.closeArchiveEntry();
        /* Repeat steps for file - 2 */
        logical_zip.putArchiveEntry(new ZipArchiveEntry("discussionForumHome.xsl"));
        IOUtils.copy(new FileInputStream(new File("src/test/resources/xml/xslt/1/discussionForumHome.xsl")), logical_zip);
        logical_zip.closeArchiveEntry();
        /* Finish addition of entries to the file */
        logical_zip.finish(); 
        /* Close output stream, our files are zipped */
        zip_output.close();
    }

}
