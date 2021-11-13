package android.example.boggle

import java.io.File
import kotlin.random.Random

typealias Coordinate = Pair<Int, Int>
typealias BoggleBoard = ArrayList<Array<String>>

val words = File(
    "C:\\Users\\Ryan\\AndroidStudioProjects\\" +
            "Boggle\\app\\src\\main\\java\\android\\example\\boggle\\sowpods.txt"
).readLines()

val boggleDictionary = HashMap<Char, MutableList<String>>()
val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
var validWords = mutableListOf<String>()

val started = arrayListOf<BooleanArray>()
val ogChecked = arrayListOf<BooleanArray>()
val board = arrayListOf<Array<String>>()

fun makeBoard(sizeOfBoard: Int) {
    board.clear()
    started.clear()
    ogChecked.clear()
    for (y in 0 until sizeOfBoard) {
        val column = Array<String>(sizeOfBoard) {
            alphabet[Random.nextInt(alphabet.length)].toString()
        }
        val checkColumn = BooleanArray(sizeOfBoard) { false }

        started.add(checkColumn)
        ogChecked.add(checkColumn.copyOf())
        board.add(column)

    }

}

fun validNextMoves(y: Int, x: Int): List<Coordinate> {
    val listOfX = mutableListOf<Int>()
    val listOfY = mutableListOf<Int>()

    listOfY.addAll(
        setOf(
            (y - 1)
                .coerceAtLeast(0), y, (y + 1).coerceAtMost(board.lastIndex)
        )
    )
    listOfX.addAll(
        setOf(
            (x - 1)
                .coerceAtLeast(0), x, (x + 1).coerceAtMost(board.lastIndex)
        )
    )

    val listOfPairs = mutableListOf<Pair<Int, Int>>()

    listOfY.forEach { yIndex ->
        listOfX.forEach { xIndex ->
            listOfPairs.add(yIndex to xIndex)
        }
    }
    listOfPairs.remove(Coordinate(y, x))
    return listOfPairs
}

fun dfsRecursive(
    arrayOfNodes: BoggleBoard,
    yAndX: Coordinate,
    result: String,
    checked: List<BooleanArray>
) {

    if (result.isEmpty()) {
        started[yAndX.first][yAndX.second] = true
    }

    val copyOfChecked = checked.map { it.clone() }
    var newWords = result

    if (!copyOfChecked[yAndX.first][yAndX.second]) {
        newWords += arrayOfNodes[yAndX.first][yAndX.second]
        copyOfChecked[yAndX.first][yAndX.second] = true
    }

    val key = newWords.first()
    if (newWords.length > 2 && boggleDictionary[key]
            ?.contains(newWords) == true && !validWords.contains(newWords)
    ) {
        validWords.add(newWords)
    }

    if (boggleDictionary[key]?.any { it.startsWith(newWords) } == true) {
        val possibleNextMoves = validNextMoves(yAndX.first, yAndX.second)
        possibleNextMoves.forEach { (y, x) ->
            if (!copyOfChecked[y][x]) {
                dfsRecursive(arrayOfNodes, Coordinate(y, x), newWords, copyOfChecked)
            }
        }
    } else {
        started.forEachIndexed { outerIndex, innerArray ->
            val index = innerArray.indexOfFirst { !it }
            if (index >= 0) {
                dfsRecursive(arrayOfNodes, Coordinate(outerIndex, index), "", ogChecked)
            }
        }
    }
}

fun main() {
    makeBoard(9)
    words.forEach { word ->
        if (word.length > 2) {
            when {
                !boggleDictionary.containsKey(word.first()) ->
                    boggleDictionary[word.first()] = mutableListOf(word)
                else ->
                    boggleDictionary[word.first()]?.add(word)
            }
        }
    }
    dfsRecursive(board, Coordinate(0, 0), "", ogChecked)
    validWords.forEach { println(it) }
}