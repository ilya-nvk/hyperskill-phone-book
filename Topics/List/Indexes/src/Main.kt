fun solution(products: List<String>, product: String) {
    val positions = mutableListOf<Int>()
    for (i in products.indices)
        if (product == products[i]) positions.add(i)
    println(positions.joinToString(" "))
}