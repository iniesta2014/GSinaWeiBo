
package weibo4android.org.json;

public class JSONException extends Exception {
    private Throwable mCause;

    /**
     * Constructs a JSONException with an explanatory message.
     * 
     * @param message Detail about the reason for the exception.
     */
    public JSONException(String message) {
        super(message);
    }

    public JSONException(Throwable t) {
        super(t.getMessage());
        this.mCause = t;
    }

    public Throwable getCause() {
        return this.mCause;
    }
}
