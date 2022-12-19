import day.Input
import day.day
import util.log
import kotlin.math.max

// answer #1: 1528
// answer #2:

fun main() {
    day(n = 19) {
        part1(expected = 1528) { input ->
            val parsed = input.parse()

            parsed.map { blueprint ->
                solve(State(blueprint = blueprint, minute = 1, oreRobots = 1)).also {
                }.log()
            }
                .withIndex()
                .sumOf { (i, v) -> (i + 1) * v }
        }
        part1 test 1 expect 33

        part2 { input ->

        }
        part2 test 1 expect Unit
    }
}

var i = 0
private var cache = mutableMapOf<State, Int>()
private fun solve(state: State): Int {
    val blueprint = state.blueprint

    if (state.minute == 25) return state.quality

    if (state in cache) {
        i++
        return cache.getValue(state)
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
            Int.MIN_VALUE
        }

    val maxOreNeeded = listOf(
        blueprint.oreRobot.ore,
        blueprint.clayRobot.ore,
        blueprint.obsidianRobot.ore,
        blueprint.geodeRobot.ore
    ).max()
    val qualityCreateOre =
        if (state.ore >= blueprint.oreRobot.ore && state.oreRobots < maxOreNeeded) {
            solveAndCache(
                state.copy(
                    ore = state.ore - blueprint.oreRobot.ore,
                    oreRobots = state.oreRobots + 1
                ), state
            )
        } else {
            Int.MIN_VALUE
        }
    val qualityCreateClay =
        if (state.ore >= blueprint.clayRobot.ore) {
            solveAndCache(
                state.copy(
                    ore = state.ore - blueprint.clayRobot.ore,
                    clayRobots = state.clayRobots + 1
                ), state
            )
        } else {
            Int.MIN_VALUE
        }
    val qualityCreateObsidian =
        if (state.ore >= blueprint.obsidianRobot.ore && state.clay >= blueprint.obsidianRobot.clay) {
            solveAndCache(
                state.copy(
                    ore = state.ore - blueprint.obsidianRobot.ore,
                    clay = state.clay - blueprint.obsidianRobot.clay,
                    obsidianRobots = state.obsidianRobots + 1
                ), state
            )
        } else {
            Int.MIN_VALUE
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
    state: State
) =
    if (next in cache) {
        cache.getValue(next)
    } else {
        val next1 = next.copy(
            minute = next.minute + 1,
            ore = next.ore + state.oreRobots,
            clay = next.clay + state.clayRobots,
            obsidian = next.obsidian + state.obsidianRobots,
            geode = next.geode + state.geodeRobots
        )
        solve(next1).also { cache[next1] = it }
    }

private data class Blueprint(
    val oreRobot: Cost,
    val clayRobot: Cost,
    val obsidianRobot: Cost,
    val geodeRobot: Cost
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
    val oreRobots: Int = 0,
    val clayRobots: Int = 0,
    val obsidianRobots: Int = 0,
    val geodeRobots: Int = 0,
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geode: Int = 0,
    val blueprint: Blueprint
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
