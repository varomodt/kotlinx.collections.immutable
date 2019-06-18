/*
 * Copyright 2016-2019 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package generators.immutableList

import generators.BenchmarkSourceGenerator
import generators.immutableList.impl.ListImplementation
import generators.immutableList.impl.listNewElement
import java.io.PrintWriter

class ListSetBenchmarkGenerator(private val impl: ListImplementation) : BenchmarkSourceGenerator() {
    override val outputFileName: String get() = "Set"

    override fun getPackage(): String {
        return super.getPackage() + ".immutableList." + impl.packageName
    }

    override fun generateBenchmark(out: PrintWriter) {
        out.println("""
open class Set {
    @Param("10000", "100000")
    var size: Int = 0

    private var persistentList = ${impl.empty()}
    private var randomIndices = listOf<Int>()

    @Setup(Level.Trial)
    fun prepare() {
        persistentList = persistentListAdd(size)
        randomIndices = List(size) { it }.shuffled()
    }

    @Benchmark
    fun setByIndex(): ${impl.type()} {
        repeat(times = size) { index ->
            persistentList = ${impl.setOperation("persistentList", "index", listNewElement)}
        }
        return persistentList
    }

    @Benchmark
    fun setByRandomIndex(): ${impl.type()} {
        repeat(times = size) { index ->
            persistentList = ${impl.setOperation("persistentList", "randomIndices[index]", listNewElement)}
        }
        return persistentList
    }
}
        """.trimIndent()
        )
    }
}