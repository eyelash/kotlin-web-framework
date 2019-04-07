import kotlin.browser.*
import org.w3c.dom.*

class Stream<T>(value: T) {
	private val observers = ArrayList<(T) -> Unit>()
	private var cachedValue = value
	var value
		get() = cachedValue
		set(newValue) {
			if (newValue != cachedValue) {
				cachedValue = newValue
				for (observer in observers) {
					observer(newValue)
				}
			}
		}
	fun addObserver(observer: (T) -> Unit) {
		observers.add(observer)
	}
}

class MyElement(val root: Element) {
	fun title(title: String) {
		document.title = title
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

	fun text(text: String) {
		root.appendChild(document.createTextNode(text))
	}
	fun text(stream: Stream<String>) {
		val textNode = document.createTextNode(stream.value)
		stream.addObserver { text ->
			textNode.data = text
		}
		root.appendChild(textNode)
	}
	fun classes(vararg classes: String) {
		root.classList.add(*classes)
	}
	fun click(handler: () -> Unit) {
		root.addEventListener("click", { handler() })
	}
}

fun run(block: MyElement.() -> Unit) {
	MyElement(document.body!!).block()
}

val myText = Stream<String>("Hello")

fun main() = run {
	title("kotlin-webframework")
	h1 {
		text(myText)
		text("!")
		click {
			myText.value = myText.value + "1"
		}
	}
	p {
		text("Welcome to kotlin-webframework!")
	}
}
