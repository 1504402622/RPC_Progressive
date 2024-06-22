package cn.glfs.filter;

import java.util.ArrayList;
import java.util.List;


public class FilterLoader {
    private List<Filter> filters = new ArrayList<>();
    public void addFilter(Filter filter){
        filters.add(filter);
    }
    public void addFilter(List<Filter> filters){
        for (Object filter : filters) {
            addFilter((Filter) filter);
        }
    }
    public FilterResponse doFilter(FilterData data){
        for (Filter filter : filters) {
            final FilterResponse filterResponse = filter.doFilter(data);
            // 如果出现异常返回
            if(!filterResponse.getResult()){
                return filterResponse;
            }
        }
        return new FilterResponse(true,null);
    }
}
