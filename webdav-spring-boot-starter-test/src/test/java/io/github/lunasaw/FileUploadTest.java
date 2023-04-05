package io.github.lunasaw;

import com.alibaba.fastjson2.JSON;
import com.luna.common.file.FileTools;
import com.luna.common.text.StringTools;
import com.luna.common.utils.Assert;
import io.github.lunasaw.webdav.WebDavSupport;
import io.github.lunasaw.webdav.entity.MultiStatusResult;
import io.github.lunasaw.webdav.request.WebDavBaseUtils;
import io.github.lunasaw.webdav.request.WebDavJackrabbitUtils;
import io.github.lunasaw.webdav.request.WebDavUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * @author chenzhangyue
 * 2023/4/2
 */
@Slf4j
public class FileUploadTest extends BaseTest {

    @Autowired
    private WebDavUtils           webDavUtils;

    @Autowired
    private WebDavSupport         webDavSupport;

    @Autowired
    private WebDavJackrabbitUtils webDavJackrabbitUtils;

    @Test
    public void upload_test() {
        boolean test =
            webDavUtils.upload("/images/buy_logo22.jpeg", "/Users/weidian/compose/images/buy_logo.jpeg");
        Assert.isTrue(test);
        boolean exist = webDavUtils.exist("http://localhost:8080/webdav/project/test/images/buy_logo.jpeg");
        Assert.isTrue(exist);
    }

    @Test
    public void download_test() {
        String ROUND_FILE_PATH = "/Users/weidian/compose/images/buy_logo_{}.jpeg";
        ROUND_FILE_PATH = StringTools.format(ROUND_FILE_PATH, RandomUtils.nextInt());
        webDavUtils.download("test", "/images/buy_logo.jpeg", ROUND_FILE_PATH);
        Assert.isTrue(FileTools.isExists(ROUND_FILE_PATH), ROUND_FILE_PATH + "文件下载错误");
    }

    @Test
    public void copy_test() throws IOException {
        webDavJackrabbitUtils.copy("http://localhost:8080/webdav/project/test/", "http://localhost:8080/webdav/project/test2/", true, false);
    }

    @Test
    public void exist_test() throws IOException {
        webDavJackrabbitUtils.exist("http://localhost:8080/webdav/project/");
    }

    @Test
    public void exist_list()  {
        String filePath = webDavSupport.getBasePath();
        MultiStatusResult list = webDavUtils.list(filePath + "test/images/");
        System.out.println(JSON.toJSONString(list.getMultistatus().getResponse()));
    }

    @Test
    public void unlock_test() {

        String token = "opaquelocktoken:32e098d5-ad9d-4509-9106-d05445472562";
        boolean lock = webDavUtils.unLock(webDavSupport.getBasePath() + "test/images/", token);
        System.out.println(lock);

    }

    @Test
    public void lock_first_test() {
        String filePath = webDavSupport.getBasePath();
        String url = String.format(filePath + "test/images/", UUID.randomUUID());
        String token = webDavUtils.lockExclusive(url);
        assertTrue(token.startsWith("opaquelocktoken:"));
        boolean delete = webDavUtils.delete(url);
        assertTrue(delete);
    }

    @Test
    public void create_test() {
        String filePath = webDavSupport.getBasePath();
        String url = String.format(filePath + "test/images/hhh/%s", UUID.randomUUID());
        webDavUtils.upload(url, new byte[0], true);
        assertTrue(webDavUtils.exist(url));
    }

    @Test
    public void lock_second_test() {
        String filePath = webDavSupport.getBasePath();
        String url = String.format(filePath + "test/%s", UUID.randomUUID());
        webDavUtils.upload(url, new byte[0], true);
        String token = webDavUtils.lockExclusive(url, 5000);
        String result = webDavUtils.refreshLock(url, 5000 * 20, token);
        assertTrue(token.startsWith("opaquelocktoken:"));
        assertTrue(token.equals(result));
    }

    @Test
    public void test() throws MalformedURLException {
        URL url = new URL("http://www.cnblogs.com/index.html?language=cn#j2se");
        // # URL：http://www.cnblogs.com/index.html?language=cn#j2se
        System.out.println("URL = " + url.toString());
        // # protocol：http
        System.out.println("protocol = " + url.getProtocol());
        // # authority：www.cnblogs.com
        System.out.println("authority = " + url.getAuthority());
        // # filename：/index.html?language=cn
        System.out.println("filename = " + url.getFile());
        // # host：www.cnblogs.com
        System.out.println("host = " + url.getHost());
        // # path：/index.html
        System.out.println("path = " + url.getPath());
        // # port：-1
        System.out.println("port = " + url.getPort());
        // # default port：80
        System.out.println("default port = " + url.getDefaultPort());
        // # query：language=cn
        System.out.println("query = " + url.getQuery());
        // # ref：j2se
        System.out.println("ref = " + url.getRef());
    }
}