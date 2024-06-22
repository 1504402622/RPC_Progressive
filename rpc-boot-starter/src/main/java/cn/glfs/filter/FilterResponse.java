package cn.glfs.filter;


public class FilterResponse {

    private final Boolean result;

    private final Exception exception;

    public FilterResponse(Boolean result, Exception exception) {
        this.result = result;
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public Boolean getResult() {
        return result;
    }
}
