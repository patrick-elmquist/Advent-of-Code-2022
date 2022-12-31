package day19

import common.Input
import common.day
import kotlin.math.max

// answer #1: 1528
// answer #2: 16926

fun main() {
    day(n = 19) {
        part1(expected = 1528) { input ->
            val parsed = input.parse()

            parsed.map { blueprint ->
                bestScenario = 0
                solve(
                    blueprint,
                    State(
                        minute = 1,
                        lastMinute = 24,
                        oreRobots = 1,
                        clayMaxNeeded = blueprint.all.maxOf { it.clay },
                        oreMaxNeeded = blueprint.all.maxOf { it.ore },
                        obsidianMaxNeeded = blueprint.all.maxOf { it.obsidian },
                    )
                )
            }
                .withIndex()
                .sumOf { (i, v) -> (i + 1) * v }
        }
        part1 test 1 expect 33

        part2(expected = 16926) { input ->
            val parsed = input.parse()

            parsed.take(3).map { blueprint ->
                bestScenario = 0
                solve(
                    blueprint,
                    State(
                        minute = 1,
                        lastMinute = 32,
                        oreRobots = 1,
                        clayMaxNeeded = blueprint.all.maxOf { it.clay },
                        oreMaxNeeded = blueprint.all.maxOf { it.ore },
                        obsidianMaxNeeded = blueprint.all.maxOf { it.obsidian },
                    )
                )
            }.reduce(Int::times) // this is not to be used as a real answer, multiply together instead
        }
        part2 test 1 expect (56 * 62)
    }
}

private var bestScenario = 0
private fun resourceSequence() = generateSequence(1) { index -> index + 1 }
private val maxGeode = (1..32).associateWith {
    resourceSequence().take(it).reduce(Int::plus)
}

private fun solve(
    blueprint: Blueprint,
    state: State,
    cache: MutableMap<State, Int> = mutableMapOf()
): Int {
    if (state.minute > state.lastMinute) {
        bestScenario = max(state.geode, bestScenario)
        return state.geode
    }

    val timeLeft = state.lastMinute - state.minute

    val get = maxGeode[timeLeft]
    if (get != null && state.geode + state.geodeRobots * (timeLeft + 1) + get < bestScenario) {
        return 0
    }

    val createGeode =
        if (state.ore >= blueprint.geodeRobot.ore && state.obsidian >= blueprint.geodeRobot.obsidian) {
            solveWithCache(
                state.copy(
                    ore = state.ore - blueprint.geodeRobot.ore,
                    obsidian = state.obsidian - blueprint.geodeRobot.obsidian,
                    geodeRobots = state.geodeRobots + 1
                ), state, blueprint, cache
            )
        } else 0

    val stopOreEarly = timeLeft * state.oreRobots + state.ore > timeLeft * state.oreMaxNeeded
    val createOre =
        if (state.ore >= blueprint.oreRobot.ore && !stopOreEarly) {
            solveWithCache(
                state.copy(
                    ore = state.ore - blueprint.oreRobot.ore,
                    oreRobots = state.oreRobots + 1
                ), state, blueprint, cache
            )
        } else 0

    val stopClayEarly = timeLeft * state.clayRobots + state.clay > timeLeft * state.clayMaxNeeded
    val createClay =
        if (state.ore >= blueprint.clayRobot.ore && !stopClayEarly) {
            solveWithCache(
                state.copy(
                    ore = state.ore - blueprint.clayRobot.ore,
                    clayRobots = state.clayRobots + 1
                ), state, blueprint, cache
            )
        } else 0

    val stopObsidianEarly = timeLeft * state.obsidianRobots + state.obsidian > timeLeft * state.obsidianMaxNeeded
    val createObsidian =
        if (state.ore >= blueprint.obsidianRobot.ore && state.clay >= blueprint.obsidianRobot.clay && !stopObsidianEarly) {
            solveWithCache(
                state.copy(
                    ore = state.ore - blueprint.obsidianRobot.ore,
                    clay = state.clay - blueprint.obsidianRobot.clay,
                    obsidianRobots = state.obsidianRobots + 1
                ), state, blueprint, cache
            )
        } else 0

    val doNothing = solveWithCache(state, state, blueprint, cache)

    return listOf(doNothing, createOre, createClay, createObsidian, createGeode).max()
}

private fun solveWithCache(
    next: State,
    state: State,
    blueprint: Blueprint,
    cache: MutableMap<State, Int>
) = when (next) {
    in cache -> cache.getValue(next)
    else -> {
        val nextAfterCollect = next.copy(
            minute = next.minute + 1,
            ore = next.ore + state.oreRobots,
            clay = next.clay + state.clayRobots,
            obsidian = next.obsidian + state.obsidianRobots,
            geode = next.geode + state.geodeRobots,
        )
        cache.getOrPut(nextAfterCollect) { solve(blueprint, nextAfterCollect, cache) }
    }
}

private data class Blueprint(
    val oreRobot: Cost,
    val clayRobot: Cost,
    val obsidianRobot: Cost,
    val geodeRobot: Cost
) {
    val all = listOf(oreRobot, clayRobot, obsidianRobot, geodeRobot)
}

private data class State(
    val minute: Int,
    val lastMinute: Int,
    val ore: Int = 0,
    val oreRobots: Int = 0,
    val oreMaxNeeded: Int,
    val clay: Int = 0,
    val clayRobots: Int = 0,
    val clayMaxNeeded: Int,
    val obsidian: Int = 0,
    val obsidianRobots: Int = 0,
    val obsidianMaxNeeded: Int,
    val geode: Int = 0,
    val geodeRobots: Int = 0
)

private data class Cost(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0
) {
    constructor(
        ore: String? = null,
        clay: String? = null,
        obsidian: String? = null
    ) : this(ore?.toIntOrNull() ?: 0, clay?.toIntOrNull() ?: 0, obsidian?.toIntOrNull() ?: 0)
}

private fun Input.parse() =
    lines.map { line ->
        line.dropWhile { it != 'E' }
            .split(". ")
            .let {
                Blueprint(
                    oreRobot = Cost(ore = it[0].split(" ")[4]),
                    clayRobot = Cost(ore = it[1].split(" ")[4]),
                    obsidianRobot = it[2].split(" ").let { split -> Cost(ore = split[4], clay = split[7]) },
                    geodeRobot = it[3].split(" ").let { split -> Cost(ore = split[4], obsidian = split[7]) },
                )
            }
    }
