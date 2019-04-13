import kotlin.browser.*
import org.w3c.dom.*

interface Observable<T> {
	val value: T
	fun addObserver(observer: (T) -> Unit)
}

class Model<T>(value: T): Observable<T> {
	private val observers = ArrayList<(T) -> Unit>()
	private var cachedValue = value
	override var value
		get() = cachedValue
		set(newValue) {
			if (newValue != cachedValue) {
				cachedValue = newValue
				for (observer in observers) {
					observer(newValue)
				}
			}
		}
	override fun addObserver(observer: (T) -> Unit) {
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

	fun input(block: MyInputElement.() -> Unit) {
		val element = document.createElement("input") as HTMLInputElement
		MyInputElement(element).block()
		root.appendChild(element)
	}

	fun text(text: String) {
		root.appendChild(document.createTextNode(text))
	}
	fun text(text: Observable<String>) {
		val textNode = document.createTextNode(text.value)
		text.addObserver {
			textNode.data = text.value
		}
		root.appendChild(textNode)
	}
	fun classes(vararg classes: String) {
		root.classList.add(*classes)
	}
	fun click(handler: () -> Unit) {
		root.addEventListener("click", { handler() })
	}

	fun div_if(condition: Observable<Boolean>, block: MyElement.() -> Unit) {
		var element = document.createElement("div")
		if (condition.value) {
			MyElement(element).block()
		}
		condition.addObserver {
			val oldElement = element
			element = element.cloneNode(false) as Element
			if (condition.value) {
				MyElement(element).block()
			}
			root.replaceChild(element, oldElement)
		}
		root.appendChild(element)
	}
	fun <T, L: List<T>> div_foreach(list: Observable<L>, block: MyElement.(T) -> Unit) {
		var element = document.createElement("div")
		for (t in list.value) {
			MyElement(element).block(t)
		}
		list.addObserver {
			val oldElement = element
			element = element.cloneNode(false) as Element
			for (t in list.value) {
				MyElement(element).block(t)
			}
			root.replaceChild(element, oldElement)
		}
		root.appendChild(element)
	}
}

class MyInputElement(val element: HTMLInputElement) {
	fun type(type: String) {
		element.type = type
	}
	fun value(value: String) {
		element.value = value
	}
	fun value(value: Model<String>) {
		element.value = value.value
		value.addObserver {
			element.value = value.value
		}
		element.addEventListener("input", {
			value.value = element.value
		})
	}
	fun onClick(handler: () -> Unit) {
		element.addEventListener("click", { handler() })
	}
}

fun run(block: MyElement.() -> Unit) {
	MyElement(document.body!!).block()
}

fun main() = run {
	title("kotlin-webframework")
	h1 {
		text("todo list")
	}
	val items = Model<List<String>>(listOf())
	val currentItem = Model<String>("")
	input {
		value(currentItem)
	}
	input {
		type("button")
		value("add")
		onClick {
			items.value = items.value + currentItem.value
			currentItem.value = ""
		}
	}
	div_foreach(items) { item ->
		p {
			text(item)
		}
	}
}
