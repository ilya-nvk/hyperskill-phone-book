package phonebook

import java.io.File
import java.lang.Exception
import java.lang.Integer.min
import kotlin.math.floor
import kotlin.math.sqrt

data class Contact(val number: Int, val name: String)

fun String.toContact(): Contact {
    val args = this.split(" ")
    val number = args[0].toInt()
    val name = args.subList(1, args.size).joinToString(" ").trim()
    return Contact(number, name)
}

fun readContacts(contactsList: List<String>): MutableList<Contact> {
    val contacts = mutableListOf<Contact>()
    for (contact in contactsList)
        contacts += contact.toContact()
    return contacts
}

fun linearSearch(name: String, contacts: List<Contact>): Int {
    for (contact in contacts) {
        if (contact.name == name) return contact.number
    }
    return -1
}

fun linearSearch(names: List<String>, contacts: List<Contact>): Int {
    var found = 0
    for (name in names) {
        if (linearSearch(name, contacts) != -1) found++
    }
    return found
}

fun bubbleSort(
    contacts: MutableList<Contact>, timeLimit: Long,
    start: Long = System.currentTimeMillis()
): MutableList<Contact> {
    System.currentTimeMillis()
    val sortedContacts = contacts.toMutableList()
    for (i in 0 until contacts.lastIndex) {
        for (j in 0 until contacts.lastIndex) {
            if (contacts[j].name < contacts[j + 1].name) {
                contacts[j] = contacts[j + 1].also { contacts[j + 1] = contacts[j] }
            }
            if (System.currentTimeMillis() - start > timeLimit * 10) {
                throw Exception("Sorting takes too long")
            }
        }
    }
    return sortedContacts
}

fun jumpSearch(name: String, contacts: List<Contact>): Int {
    if (contacts.isEmpty())
        return -1

    var curr = 0
    var prev = 0
    val last = contacts.lastIndex
    val step = floor(sqrt(contacts.size.toDouble())).toInt()

    while (contacts[curr].name < name) {
        if (curr == last)
            return -1
        prev = curr
        curr = min(curr + step, last)
    }

    while (contacts[curr].name > name) {
        if (curr == prev)
            return -1
        curr--
    }

    if (contacts[curr].name == name)
        return contacts[curr].number

    return -1
}

fun jumpSearch(names: List<String>, contacts: List<Contact>): Int {
    var found = 0
    for (name in names) {
        if (jumpSearch(name, contacts) != -1) found++
    }
    return found
}

fun quickSort(
    contacts: MutableList<Contact>,
    timeLimit: Long,
    start: Long = System.currentTimeMillis()
): MutableList<Contact> {
    if (System.currentTimeMillis() - start > timeLimit * 10)
        throw Exception("Sorting takes too long")

    if (contacts.size == 0 || contacts.size == 1)
        return contacts

    val pivot = contacts.last()
    val left = mutableListOf<Contact>()
    val middle = mutableListOf<Contact>()
    val right = mutableListOf<Contact>()

    for (contact in contacts) {
        if (contact.name < pivot.name) {
            left.add(contact)
        } else if (contact.name > pivot.name) {
            right.add(contact)
        } else {
            middle.add(contact)
        }
    }

    return (quickSort(left, timeLimit, start) + middle + quickSort(right, timeLimit, start)).toMutableList()
}

fun binarySearch(name: String, contacts: List<Contact>): Int {
    var left = 0
    var right = contacts.lastIndex
    while (left <= right) {
        val middle = (left + right) // 2
        if (contacts[middle].name == name) {
            return middle
        } else if (contacts[middle].name > name) {
            right = middle - 1
        } else {
            left = middle + 1
        }
    }
    return -1
}

fun binarySearch(names: List<String>, contacts: List<Contact>): Int {
    var found = 0
    for (name in names) {
        if (binarySearch(name, contacts) != -1) found++
    }
    return found
}

fun createHashMap(contacts: List<Contact>): HashMap<String, Int> {
    val hashMap = HashMap<String, Int>()
    for (contact in contacts) {
        hashMap[contact.name] = contact.number
    }
    return hashMap
}

fun hashMapSearch(name: String, contacts: HashMap<String, Int>): Int {
    return contacts[name] ?: -1
}

fun hashMapSearch(names: List<String>, contacts: HashMap<String, Int>): Int {
    var found = 0
    for (name in names) {
        if (hashMapSearch(name, contacts) != -1) found++
    }
    return found
}

fun longToTime(time: Long): String {
    val minutes = time / 1000 / 60
    val seconds = time / 1000 % 60
    val milliseconds = time % 1000
    return "$minutes min. $seconds sec. $milliseconds ms."
}

fun print(found: Int, goal: Int, searchingTime: Long) {
    println("Found $found / $goal entries. Time taken: ${longToTime(searchingTime)}")
}

fun print(
    found: Int, goal: Int, searchingTime: Long, sortingTime: Long, sortingSucceeded: Boolean
) {
    println(
        "Found $found / $goal entries. " +
                "Time taken: ${longToTime(searchingTime + sortingTime)}"
    )
    println(
        "Sorting time: ${longToTime(sortingTime)}" +
                if (!sortingSucceeded) " - STOPPED, moved to linear search" else ""
    )
    println("Searching time: ${longToTime(searchingTime)}")
}

fun print(
    found: Int, goal: Int, searchingTime: Long, creatingHashMapTime: Long
) {
    println(
        "Found $found / $goal entries. " +
                "Time taken: ${longToTime(searchingTime + creatingHashMapTime)}"
    )
    println("Creating time: ${longToTime(creatingHashMapTime)}")
    println("Searching time: ${longToTime(searchingTime)}")
}

fun main() {
    val findPath = "find.txt"
    val directoryPath = "directory.txt"


    // reading files
    val names = File(findPath).readLines()
    var contacts = readContacts(File(directoryPath).readLines())


    // linear search
    println("Start searching (linear search)...")
    var startSearchingTime = System.currentTimeMillis()
    var found = linearSearch(names, contacts)
    var endSearchingTime = System.currentTimeMillis()
    val linearSearchTime = endSearchingTime - startSearchingTime
    print(found, names.size, linearSearchTime)
    println()


    // bubble sort + jump search
    println("Start searching (bubble sort + jump search)...")

    // sort
    var startSortingTime = System.currentTimeMillis()

    var sortingSucceeded = true
    var sortedContacts = try {
        bubbleSort(contacts, linearSearchTime)
    } catch (e: Exception) {
        sortingSucceeded = false
        contacts
    }
    var endSortingTime = System.currentTimeMillis()
    var totalSortingTime = endSortingTime - startSortingTime

    var search: (List<String>, List<Contact>) -> Int =
        if (sortingSucceeded)
            ::jumpSearch
        else
            ::linearSearch

    // search
    startSearchingTime = System.currentTimeMillis()
    found = search(names, sortedContacts)
    endSearchingTime = System.currentTimeMillis()
    var totalSearchingTime = endSearchingTime - startSearchingTime

    print(found, names.size, totalSearchingTime, totalSortingTime, sortingSucceeded)
    println()


    // quick sort + binary search
    contacts = readContacts(File(directoryPath).readLines())
    println("Start searching (quick sort + binary search)...")

    // quick sort
    startSortingTime = System.currentTimeMillis()

    sortingSucceeded = true
    sortedContacts = try {
        quickSort(contacts, linearSearchTime)
    } catch (e: Exception) {
        sortingSucceeded = false
        contacts
    }
    endSortingTime = System.currentTimeMillis()
    totalSortingTime = endSortingTime - startSortingTime
    search =
        if (sortingSucceeded)
            ::binarySearch
        else
            ::linearSearch

    // binary search
    startSearchingTime = System.currentTimeMillis()
    found = search(names, sortedContacts)
    endSearchingTime = System.currentTimeMillis()
    totalSearchingTime = endSearchingTime - startSearchingTime

    print(found, names.size, totalSearchingTime, totalSortingTime, sortingSucceeded)
    println()


    // hash table
    println("Start searching (hash table)...")

    // hash map creation
    val creatingStartTime = System.currentTimeMillis()
    val hashMap = createHashMap(contacts)
    val creatingEndTime = System.currentTimeMillis()
    val totalCreatingTime = creatingEndTime - creatingStartTime

    // searching
    startSearchingTime = System.currentTimeMillis()
    found = hashMapSearch(names, hashMap)
    endSearchingTime = System.currentTimeMillis()
    totalSearchingTime = endSearchingTime - startSearchingTime

    print(found, names.size, totalSearchingTime, totalCreatingTime)
}