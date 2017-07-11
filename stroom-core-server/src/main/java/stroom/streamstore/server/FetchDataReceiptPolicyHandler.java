/*
 * Copyright 2017 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stroom.streamstore.server;

import org.springframework.context.annotation.Scope;
import stroom.streamstore.shared.DataReceiptPolicy;
import stroom.streamstore.shared.FetchDataReceiptPolicyAction;
import stroom.task.server.AbstractTaskHandler;
import stroom.task.server.TaskHandlerBean;
import stroom.util.spring.StroomScope;

import javax.inject.Inject;

@TaskHandlerBean(task = FetchDataReceiptPolicyAction.class)
@Scope(StroomScope.TASK)
public class FetchDataReceiptPolicyHandler extends AbstractTaskHandler<FetchDataReceiptPolicyAction, DataReceiptPolicy> {
    private final DataReceiptService dataReceiptService;


    @Inject
    FetchDataReceiptPolicyHandler(final DataReceiptService dataReceiptService) {
        this.dataReceiptService = dataReceiptService;
    }

    @Override
    public DataReceiptPolicy exec(final FetchDataReceiptPolicyAction task) {
        return dataReceiptService.load();
    }
}
