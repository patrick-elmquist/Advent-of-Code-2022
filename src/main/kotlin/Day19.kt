import day.Input
import day.day
import util.log

// answer #1: 1528
// answer #2: 16926

fun main() {
    day(n = 19) {
        part1(expected = 1528) { input ->
            val parsed = input.parse()

            (1 .. 32).map {
                it to getMaxGeode().take(it).reduce(Int::plus)
            }.toMap(maxGeode)
//            maxGeode[0] = 0
            maxGeode.log()

            parsed.map { blueprint ->
                max = 0
                solve(
                    State(
                        blueprint = blueprint,
                        minute = 1,
                        stop = 24,
                        oreRobots = 1,
                        maxClay = blueprint.all.maxOf { it.clay },
                        maxOre = blueprint.all.maxOf { it.ore },
                        maxObsidian = blueprint.all.maxOf { it.obsidian },
                    )
                ).log()
            }
                .withIndex()
                .sumOf { (i, v) -> (i + 1) * v }
        }
        part1 test 1 expect 33

        part2(expected = 16926) { input ->
            val parsed = input.parse()

            maxGeode.clear()
            (1 .. 32).map {
                it to getMaxGeode().take(it).reduce(Int::plus)
            }.toMap(maxGeode)
            parsed.take(3).map { blueprint ->
                max = 0
                solve(
                    State(
                        blueprint = blueprint,
                        minute = 1,
                        stop = 32,
                        oreRobots = 1,
                        maxClay = blueprint.all.maxOf { it.clay },
                        maxOre = blueprint.all.maxOf { it.ore },
                        maxObsidian = blueprint.all.maxOf { it.obsidian },
                    )
                ).log()
            }.reduce(Int::times) // this is not to be used as a real answer, multiply together instead
        }
        part2 test 1 expect (56 * 62)
    }
}

private fun getMaxGeode() = generateSequence(1) { index ->
    index + 1
}
private val maxGeode = mutableMapOf<Int, Int>()
private var max = 0
private var cache = mutableMapOf<State, Int>()
private fun solve(state: State): Int {
    val blueprint = state.blueprint

    if (state.minute > state.stop) return state.quality.also { max = kotlin.math.max(it, max) }
//    if (state.minute == state.stop && state.geodeRobots == 0) return state.quality
//    if (state.minute == state.stop - 5 && state.clayRobots == 0) return state.quality
//    if (state.minute == state.stop - 4 && state.obsidianRobots == 0) return state.quality

    val timeLeft = state.stop - state.minute
    if (state in cache) {
        return cache.getValue(state)
    }

    val get = maxGeode.get(timeLeft)
    if (get != null && state.geode + state.geodeRobots * (timeLeft + 1) + get < max) {
//        "max:$max utopia:${get + state.geode + state.geodeRobots * timeLeft} geodes:${state.geode} robots:${state.geodeRobots} left:$timeLeft".log()
        return 0
    }

    val qualityCreateGeode =
        if (state.ore >= blueprint.geodeRobot.ore && state.obsidian >= blueprint.geodeRobot.obsidian) {
            return solveAndCache(
                state.copy(
                    ore = state.ore - blueprint.geodeRobot.ore,
                    obsidian = state.obsidian - blueprint.geodeRobot.obsidian,
                    geodeRobots = state.geodeRobots + 1
                ), state
            )
        } else {
            0
        }

    val stopOreEarly = (timeLeft * state.oreRobots + state.ore > timeLeft * state.maxOre)
    val qualityCreateOre =
        if (state.ore >= blueprint.oreRobot.ore && !stopOreEarly) {
            solveAndCache(
                state.copy(
                    ore = state.ore - blueprint.oreRobot.ore,
                    oreRobots = state.oreRobots + 1
                ), state
            )
        } else {
            0
        }

    val stopClayEarly = (timeLeft * state.clayRobots + state.clay > timeLeft * state.maxClay)
    val qualityCreateClay =
        if (state.ore >= blueprint.clayRobot.ore && !stopClayEarly) {
            solveAndCache(
                state.copy(
                    ore = state.ore - blueprint.clayRobot.ore,
                    clayRobots = state.clayRobots + 1
                ), state
            )
        } else {
            0
        }

    val stopObsidianEarly = (timeLeft * state.obsidianRobots + state.obsidian > timeLeft * state.maxObsidian)
    val qualityCreateObsidian =
        if (state.ore >= blueprint.obsidianRobot.ore && state.clay >= blueprint.obsidianRobot.clay && !stopObsidianEarly) {
            solveAndCache(
                state.copy(
                    ore = state.ore - blueprint.obsidianRobot.ore,
                    clay = state.clay - blueprint.obsidianRobot.clay,
                    obsidianRobots = state.obsidianRobots + 1
                ), state
            )
        } else {
            0
        }

    val doNothing = solveAndCache(state, state)

    return listOf(
        doNothing,
        qualityCreateOre,
        qualityCreateClay,
        qualityCreateObsidian,
        qualityCreateGeode
    ).max()
}

private fun solveAndCache(
    next: State,
    state: State,
) = if (next in cache) {
    cache.getValue(next)
} else {
    val next1 = next.copy(
        minute = next.minute + 1,
        ore = next.ore + state.oreRobots,
        clay = next.clay + state.clayRobots,
        obsidian = next.obsidian + state.obsidianRobots,
        geode = next.geode + state.geodeRobots,
    )
    solve(next1).also { cache[next1] = it }
}

private data class Blueprint(
    val oreRobot: Cost,
    val clayRobot: Cost,
    val obsidianRobot: Cost,
    val geodeRobot: Cost
) {
    val all = listOf(oreRobot, clayRobot, obsidianRobot, geodeRobot)
}

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

    override fun toString(): String {
        return buildString {
            if (ore > 0) {
                append("$ore ore")
                if (clay > 0 || obsidian > 0)
                    append(" and ")
            }
            if (clay > 0) {
                append("$clay clay")
                if (obsidian > 0)
                    append(" and ")
            }
            if (obsidian > 0) {
                append("$obsidian obsidian")
            }
        }
    }
}

private data class State(
    val minute: Int,
    val stop: Int,
    val oreRobots: Int = 0,
    val clayRobots: Int = 0,
    val obsidianRobots: Int = 0,
    val geodeRobots: Int = 0,
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geode: Int = 0,
    val blueprint: Blueprint,
    val maxOre: Int,
    val maxObsidian: Int,
    val maxClay: Int,
) {
    val quality = geode
}

private fun Input.parse() =
    lines.map { line ->
        line.dropWhile { it != 'E' }
            .split(". ")
            .let {
                Blueprint(
                    oreRobot = Cost(ore = it[0].split(" ")[4]),
                    clayRobot = Cost(ore = it[1].split(" ")[4]),
                    obsidianRobot = it[2].split(" ").let { Cost(ore = it[4], clay = it[7]) },
                    geodeRobot = it[3].split(" ").let { Cost(ore = it[4], obsidian = it[7]) },
                )
            }
    }
