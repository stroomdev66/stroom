package stroom.statistics.server.sql.internal;

import com.google.common.base.Preconditions;
import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import stroom.node.server.StroomPropertyService;
import stroom.query.api.v2.DocRef;
import stroom.statistics.internal.InternalStatisticEvent;
import stroom.statistics.internal.InternalStatisticsService;
import stroom.statistics.server.sql.StatisticEvent;
import stroom.statistics.server.sql.StatisticTag;
import stroom.statistics.server.sql.Statistics;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Component
class SQLInternalStatisticsService implements InternalStatisticsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLInternalStatisticsService.class);

    private static final String PROP_KEY_DOC_REF_TYPE = "stroom.services.sqlStatistics.docRefType";

    private final StroomPropertyService stroomPropertyService;
    private final Statistics statisticsService;
    private final String docRefType;

    @Inject
    SQLInternalStatisticsService(final StroomPropertyService stroomPropertyService,
                                 final Statistics statisticsService) {
        this.stroomPropertyService = stroomPropertyService;
        this.statisticsService = statisticsService;
        this.docRefType = stroomPropertyService.getProperty(PROP_KEY_DOC_REF_TYPE);
    }

    @Override
    public void putEvents(final Map<DocRef, List<InternalStatisticEvent>> eventsMap) {

        List<StatisticEvent> statisticEvents = Preconditions.checkNotNull(eventsMap).entrySet().stream()
                .flatMap(entry ->
                        entry.getValue().stream()
                                .map(event -> new Tuple2<>(entry.getKey(), event)))
                .map(tuple2 -> internalEventMapper(tuple2._1(), tuple2._2()))
                .collect(Collectors.toList());

        statisticsService.putEvents(statisticEvents);
    }

    private StatisticEvent internalEventMapper(final DocRef docRef,
                                               final InternalStatisticEvent internalStatisticEvent) {

        Preconditions.checkNotNull(internalStatisticEvent);
        switch (internalStatisticEvent.getType()) {
            case COUNT:
                return mapCountEvent(docRef, internalStatisticEvent);
            case VALUE:
                return mapValueEvent(docRef, internalStatisticEvent);
            default:
                throw new IllegalArgumentException("Unknown type: " + internalStatisticEvent.getType());
        }
    }

    private StatisticEvent mapCountEvent(final DocRef docRef,
                                         final InternalStatisticEvent internalStatisticEvent) {
        return StatisticEvent.createCount(
                internalStatisticEvent.getTimeMs(),
                docRef.getName(),
                internalStatisticEvent.getTags().entrySet().stream()
                        .map(entry -> new StatisticTag(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList()),
                internalStatisticEvent.getValueAsLong());
    }

    private StatisticEvent mapValueEvent(final DocRef docRef,
                                         final InternalStatisticEvent internalStatisticEvent) {
        return StatisticEvent.createValue(
                internalStatisticEvent.getTimeMs(),
                docRef.getName(),
                internalStatisticEvent.getTags().entrySet().stream()
                        .map(entry -> new StatisticTag(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList()),
                internalStatisticEvent.getValueAsDouble());
    }

    @Override
    public String getDocRefType() {
        return docRefType;
    }
}
