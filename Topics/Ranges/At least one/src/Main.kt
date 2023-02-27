import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)
    val l1 = scanner.nextInt()
    val r1 = scanner.nextInt()
    val l2 = scanner.nextInt()
    val r2 = scanner.nextInt()
    val n = scanner.nextInt()
    println(n in l1..r1 || n in l2..r2)
}