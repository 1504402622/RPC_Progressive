package cn.glfs.filter;


import java.util.ArrayList;
import java.util.List;

public class FilterFactory {
    private static List<Filter> clientBeforeFilters = new ArrayList<>();
    private static List<Filter> clientAfterFilters = new ArrayList<>();
    private static List<Filter> serverBeforeFilters = new ArrayList<>();
    private static List<Filter> serverAfterFilters = new ArrayList<>();
    public static void registerClientBeforeFilter(Filter filter) {
        clientBeforeFilters.add(filter);
    }

    public static void registerClientAfterFilter(Filter filter) {
        clientBeforeFilters.add(filter);
    }

    public static void registerServerBeforeFilter(Filter filter) {
        serverBeforeFilters.add(filter);
    }

    public static void registerServerAfterFilter(Filter filter) {
        serverAfterFilters.add(filter);
    }

    public static List<Filter> getClientBeforeFilters() {
        return clientBeforeFilters;
    }

    public static List<Filter> getClientAfterFilters() {
        return clientAfterFilters;
    }

    public static List<Filter> getServerBeforeFilters() {
        return serverBeforeFilters;
    }

    public static List<Filter> getServerAfterFilters() {
        return serverAfterFilters;
    }
}

