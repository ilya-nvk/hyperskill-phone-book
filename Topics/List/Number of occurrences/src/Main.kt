fun solution(strings: List<String>, str: String): Int {
    var counter = 0
    for (el in strings) {
        if (str == el) counter++
    }
    return counter
}