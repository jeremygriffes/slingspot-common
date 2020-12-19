package net.slingspot.lang

public inline fun <reified T> arrayOfNotNull(vararg elements: T?): Array<T> = listOfNotNull(*elements).toTypedArray()
