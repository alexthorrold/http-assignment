import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class HttpServerRequestTest
{
    /*
     * given a well-formed HTTP request for foobar.com/foo/bar.txt which
     * generates the following HTTP request:
     * GET /foo/bar.txt HTTP/1.1
     * Host: foobar.com
     *
     * getHost should return foobar.com and
     * getFile should return foo/bar.txt
     */
    @Test
    @DisplayName("Test well-formed HTTP request for foobar.com/foo/bar.txt")
    void wellFormedReq() {
	HttpServerRequest hsr = new HttpServerRequest();
	hsr.process("GET /foo/bar.txt HTTP/1.1");
	hsr.process("Host: foobar.com");
	hsr.process("");

	Assertions.assertEquals(hsr.isDone(), true);
	Assertions.assertEquals("foo/bar.txt", hsr.getFile(), "getFile");
	Assertions.assertEquals("foobar.com", hsr.getHost(), "getHost");
    }

    /*
     * given a well-formed HTTP request for foobar.com/foo/ which
     * generates the following HTTP request:
     * GET /foo/ HTTP/1.1
     * Host: foobar.com
     *
     * getHost should return foobar.com and
     * getFile should return foo/index.html
     */
    @Test
    @DisplayName("Test well-formed HTTP request for foobar.com/foo/")
    void wellFormedReqIndex() {
	HttpServerRequest hsr = new HttpServerRequest();
	hsr.process("GET /foo/ HTTP/1.1");
	hsr.process("Host: foobar.com");
	hsr.process("");

	assertTrue(hsr.isDone());
	Assertions.assertEquals("foo/index.html", hsr.getFile(), "getFile");
	Assertions.assertEquals("foobar.com", hsr.getHost(), "getHost");
    }

    /*
     * given a well-formed HTTP request for foobar.com/foo which
     * generates the following HTTP request:
     * GET /foo HTTP/1.1
     * Host: foobar.com
     *
     * @@@ complete this comment and write the test.
     */
    @Test
    @DisplayName("Test well-formed HTTP request for /foo")
    void wellFormedFoo() {
        HttpServerRequest hsr = new HttpServerRequest();
        hsr.process("GET /foo HTTP/1.1");
        hsr.process("Host: foobar.com");
        hsr.process("");

        assertTrue(hsr.isDone());
        Assertions.assertEquals("foo", hsr.getFile(), "getFile");
        Assertions.assertEquals("foobar.com", hsr.getHost(), "getHost");
    }

    /*
     * write a test for a GET request with fewer than three
     * components, such as:
     * GET /
     * that getFile returns null
     */
    @Test
    @DisplayName("Test GET with fewer than three components")
    void badGet1() {
        HttpServerRequest hsr = new HttpServerRequest();
        hsr.process("GET /");

        Assertions.assertNull(hsr.getFile(), "getFile");
    }

    /*
     * write a test for a GET request for "": -- i.e.
     * GET  HTTP/1.1
     * that getFile returns null
     */
    @Test
    @DisplayName("Test GET with an empty filename")
    void badGet2() {
        HttpServerRequest hsr = new HttpServerRequest();
        hsr.process("GET  HTTP/1.1");

        Assertions.assertNull(hsr.getFile(), "getFile");
    }

    /*
     * write a test for an HTTP request that does not include a Host:
     * header.  getHost should return null
     */
    @Test
    @DisplayName("Test HTTP request without a Host: header")
    void noHost() {
        HttpServerRequest hsr = new HttpServerRequest();
        hsr.process("GET /foo HTTP/1.1");
        hsr.process("");

        Assertions.assertNull(hsr.getHost(), "getHost");
    }

    /*
     * write a test for an HTTP request where the Host: header is empty,
     * as in "Host: "
     * getHost should return an empty string ""
     */
    @Test
    @DisplayName("Test parsing of 'Host: '")
    void emptyHost() {
        HttpServerRequest hsr = new HttpServerRequest();
        hsr.process("GET /foo HTTP/1.1");
        hsr.process("Host: ");
        hsr.process("");

        Assertions.assertEquals("", hsr.getHost(), "getHost");
    }

    /*
     * write a test for an empty HTTP request, i.e., the client sends
     * an empty line ("").  isDone should return true, and
     * getFile/getHost should both return null
     */
    @Test
    @DisplayName("Test parsing of empty first line")
    void emptyFirst() {
        HttpServerRequest hsr = new HttpServerRequest();
        hsr.process("");

        Assertions.assertNull(hsr.getFile(), "getFile");
        Assertions.assertNull(hsr.getHost(), "getHost");
    }

    /*
     * write a test for when the client disconnects without sending anything
     * i.e., we get a null string returned from readLine.
     * isDone should return true, and getFile/getHost should
     * both return null
     */
    @Test
    @DisplayName("Test parsing of null first line")
    void nullFirst() {
        HttpServerRequest hsr = new HttpServerRequest();
        hsr.process(null);

        Assertions.assertTrue(hsr.isDone(), "isDone");
        Assertions.assertNull(hsr.getFile(), "getFile");
        Assertions.assertNull(hsr.getHost(), "getHost");
    }

    /*
     * write a test that ensures isDone() returns false until the client
     * sends an empty line, and isDone() returns true. use a well-formed
     * HTTP request such as:
     * GET /foo HTTP/1.1
     * Host: foobar.com
     *
     */
    @Test
    @DisplayName("Test checking if we are done")
    void checkDone() {
        HttpServerRequest hsr = new HttpServerRequest();
        hsr.process("GET /foo/bar.txt HTTP/1.1");
        hsr.process("Host: foobar.com");
        boolean middleCheck = hsr.isDone();
        hsr.process("");

        Assertions.assertFalse(middleCheck, "isDone before empty line");
        Assertions.assertTrue(hsr.isDone(), "isDone after empty line");
    }

    /*
     * add any additional unit tests that you think are useful.  For
     * example, if the client sends:
     * Host: www.foobar.com
     * GET /foo HTTP/1.1
     *
     * then getHost should return null and getFile should return null
     * because the request is not well-formed.  isDone returns true
     * if you send an empty string after the GET.
     */
    @Test
    @DisplayName("Test if incorrect order returns null")
    void incorrectOrderReturnsNull() {
        HttpServerRequest hsr = new HttpServerRequest();
        hsr.process("Host: www.foobar.com");
        hsr.process("GET /foo/bar.txt HTTP /1.1");
        hsr.process("");

        Assertions.assertTrue(hsr.isDone(), "isDone");
        Assertions.assertNull(hsr.getFile(), "getFile");
        Assertions.assertNull(hsr.getHost(), "getHost");
    }
}
