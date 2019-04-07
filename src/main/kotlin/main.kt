import kotlin.browser.*

fun main() {
	document.title = "kotlin-webframework"
	val element = document.createElement("h1")
	element.appendChild(document.createTextNode("hello"))
	document.body?.appendChild(element)
}
