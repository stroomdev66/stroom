/*
 * Copyright 2017 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stroom.resources.query.v2;

import com.codahale.metrics.annotation.Timed;
import com.codahale.metrics.health.HealthCheck;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stroom.datasource.api.v2.DataSource;
import stroom.query.api.v2.DocRef;
import stroom.query.api.v2.QueryKey;
import stroom.query.api.v2.SearchRequest;
import stroom.query.api.v2.SearchResponse;
import stroom.resources.ResourcePaths;
import stroom.statistics.server.sql.StatisticsQueryService;
import stroom.util.json.JsonUtil;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(
        value = "sqlstatistics query - " + ResourcePaths.V2,
        description = "Stroom SQL Statistics Query API")
@Path(ResourcePaths.SQL_STATISTICS + ResourcePaths.V2)
@Produces(MediaType.APPLICATION_JSON)
public class SqlStatisticsQueryResource implements QueryResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlStatisticsQueryResource.class);

    private StatisticsQueryService statisticsQueryService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(QueryResource.DATA_SOURCE_ENDPOINT)
    @Timed
    @ApiOperation(
            value = "Submit a request for a data source definition, supplying the DocRef for the data source",
            response = DataSource.class)
    public DataSource getDataSource(@ApiParam("DocRef") final DocRef docRef) {

        if (LOGGER.isDebugEnabled()) {
            String json = JsonUtil.writeValueAsString(docRef);
            LOGGER.debug("/dataSource called with docRef:\n{}", json);
        }
        return statisticsQueryService.getDataSource(docRef);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(QueryResource.SEARCH_ENDPOINT)
    @Timed
    @ApiOperation(
            value = "Submit a search request",
            response = SearchResponse.class)
    public SearchResponse search(@ApiParam("SearchRequest") final SearchRequest request) {

        if (LOGGER.isDebugEnabled()) {
            String json = JsonUtil.writeValueAsString(request);
            LOGGER.debug("/search called with searchRequest:\n{}", json);
        }

        SearchResponse response = statisticsQueryService.search(request);
        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(QueryResource.DESTROY_ENDPOINT)
    @Timed
    @ApiOperation(
            value = "Destroy a running query",
            response = Boolean.class)
    public Boolean destroy(@ApiParam("QueryKey") final QueryKey queryKey) {
        if (LOGGER.isDebugEnabled()) {
            String json = JsonUtil.writeValueAsString(queryKey);
            LOGGER.debug("/destroy called with queryKey:\n{}", json);
        }
        return statisticsQueryService.destroy(queryKey);
    }

    public void setStatisticsQueryService(final StatisticsQueryService statisticsQueryService) {
        this.statisticsQueryService = statisticsQueryService;
    }

    public HealthCheck.Result getHealth() {
        if (statisticsQueryService == null) {
            StringBuilder errorMessageBuilder = new StringBuilder();
            errorMessageBuilder.append("Dependency error!");
            String statisticsQueryServiceMessage = " 'statisticsQueryService' has not been set!";
            errorMessageBuilder.append(statisticsQueryServiceMessage);
            return HealthCheck.Result.unhealthy(errorMessageBuilder.toString());
        } else {
            return HealthCheck.Result.healthy();
        }
    }

}