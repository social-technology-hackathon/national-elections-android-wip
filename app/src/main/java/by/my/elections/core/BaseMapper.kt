package by.my.elections.core

interface BaseMapper<in A : Any, out B : Any> {
    fun map(from: A): B
}