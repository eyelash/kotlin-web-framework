import kotlin.browser.*
import org.w3c.dom.*

class MyElement(val root: Element) {
	var title: String = ""
		set(value) {
			field = value
			document.title = value
		}
	var text: String = ""
		set(value) {
			field = value
			root.appendChild(document.createTextNode(value))
		}

	private fun appendElement(tagName: String, block: MyElement.() -> Unit) {
		val element = document.createElement(tagName)
		MyElement(element).block()
		root.appendChild(element)
	}

	fun h1(block: MyElement.() -> Unit) = appendElement("h1", block)
	fun h2(block: MyElement.() -> Unit) = appendElement("h2", block)
	fun h3(block: MyElement.() -> Unit) = appendElement("h3", block)
	fun p(block: MyElement.() -> Unit) = appendElement("p", block)
	fun div(block: MyElement.() -> Unit) = appendElement("div", block)
}

fun run(block: MyElement.() -> Unit) {
	MyElement(document.body!!).block()
}

fun main() = run {
	title = "kotlin-webframework"
	h1 {
		text = "Hello"
	}
	p {
		text = "Welcome to kotlin-webframework!"
	}
}
