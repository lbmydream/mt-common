package com.mt.common.domain.model.idempotent;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Getter;

import java.util.*;

@Getter
public class ChangeRecordQuery extends QueryCriteria {
    private String entityType;
    private Set<String> changeIds;
    private final ChangeRecordSort changeRecordSort;

    public ChangeRecordQuery(String queryParam) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        changeRecordSort = ChangeRecordSort.instance;
    }

    public ChangeRecordQuery(String queryParam, String entityType) {
        updateQueryParam(queryParam);
        this.entityType = entityType;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        changeRecordSort = ChangeRecordSort.instance;
    }

    public ChangeRecordQuery(String queryParam, String pageConfig, String queryConfig) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.limited(pageConfig, 100));
        setQueryConfig(new QueryConfig(queryConfig));
        changeRecordSort = ChangeRecordSort.instance;
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam);
        entityType = stringStringMap.get("entityType");
        Optional.ofNullable(stringStringMap.get("changeId")).ifPresent(e -> {
            changeIds = new HashSet<>(List.of(e.split("\\.")));
        });
    }

    @Getter
    public static class ChangeRecordSort {
        private final boolean byId = true;
        private final boolean isAsc = true;
        private static final ChangeRecordSort instance = new ChangeRecordSort();

        private ChangeRecordSort() {
        }
    }
}
