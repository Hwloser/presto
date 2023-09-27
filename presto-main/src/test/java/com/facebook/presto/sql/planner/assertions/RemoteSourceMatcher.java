/*
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

package com.facebook.presto.sql.planner.assertions;

import com.facebook.presto.Session;
import com.facebook.presto.cost.StatsProvider;
import com.facebook.presto.metadata.Metadata;
import com.facebook.presto.spi.plan.PlanNode;
import com.facebook.presto.sql.planner.plan.PlanFragmentId;
import com.facebook.presto.sql.planner.plan.RemoteSourceNode;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import static com.facebook.presto.sql.analyzer.ExpressionTreeUtils.createSymbolReference;
import static com.facebook.presto.sql.planner.assertions.MatchResult.NO_MATCH;
import static com.facebook.presto.sql.planner.assertions.MatchResult.match;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

public class RemoteSourceMatcher
        implements Matcher
{
    private List<PlanFragmentId> sourceFragmentIds;
    private final Map<String, Integer> outputSymbolAliases;

    public RemoteSourceMatcher(List<PlanFragmentId> sourceFragmentIds, Map<String, Integer> outputSymbolAliases)
    {
        this.sourceFragmentIds = requireNonNull(sourceFragmentIds, "sourceFragmentIds is null");
        this.outputSymbolAliases = requireNonNull(outputSymbolAliases, "outputSymbolAliases is null");
    }

    @Override
    public boolean shapeMatches(PlanNode node)
    {
        return node instanceof RemoteSourceNode;
    }

    @Override
    public MatchResult detailMatches(PlanNode node, StatsProvider stats, Session session, Metadata metadata, SymbolAliases symbolAliases)
    {
        checkState(shapeMatches(node), "Plan testing framework error: shapeMatches returned false in detailMatches in %s", this.getClass().getName());
        RemoteSourceNode remoteSourceNode = (RemoteSourceNode) node;
        if (remoteSourceNode.getSourceFragmentIds().equals(sourceFragmentIds)) {
            return match(SymbolAliases.builder()
                    .putAll(Maps.transformValues(outputSymbolAliases, index -> createSymbolReference(remoteSourceNode.getOutputVariables().get(index))))
                    .build());
        }
        return NO_MATCH;
    }
}
