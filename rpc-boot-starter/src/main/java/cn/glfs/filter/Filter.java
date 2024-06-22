package cn.glfs.filter;


public interface Filter<T> {

    FilterResponse doFilter(FilterData<T> filterData);

}
