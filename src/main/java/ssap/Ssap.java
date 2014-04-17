package ssap;

public class Ssap {
	
    private final long id;
    private final String content;
    private final String test;

    public Ssap(long id, String content, String testing) {
        this.id = id;
        this.content = content;
        this.test = testing;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
    
    public String getTest() {
        return test;
    }
}