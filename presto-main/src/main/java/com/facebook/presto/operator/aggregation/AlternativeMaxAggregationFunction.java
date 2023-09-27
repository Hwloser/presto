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
package com.facebook.presto.operator.aggregation;

import com.facebook.presto.bytecode.DynamicClassLoader;
import com.facebook.presto.common.type.Type;
import com.facebook.presto.operator.aggregation.state.AlternativeNullableLongState;
import com.facebook.presto.operator.aggregation.state.StateCompiler;
import com.facebook.presto.spi.function.AccumulatorState;
import com.facebook.presto.spi.function.AccumulatorStateSerializer;

public class AlternativeMaxAggregationFunction
        extends MaxAggregationFunction
{
    public static final AlternativeMaxAggregationFunction ALTERNATIVE_MAX = new AlternativeMaxAggregationFunction();

    public AlternativeMaxAggregationFunction()
    {
        super();
    }

    protected AccumulatorStateSerializer<?> getStateSerializer(Class<? extends AccumulatorState> stateInterface, DynamicClassLoader classLoader)
    {
        return StateCompiler.generateStateSerializer(AlternativeNullableLongState.class, classLoader);
    }

    @Override
    protected Type overrideIntermediateType(Type inputType, Type defaultIntermediateType)
    {
        return inputType;
    }
}
