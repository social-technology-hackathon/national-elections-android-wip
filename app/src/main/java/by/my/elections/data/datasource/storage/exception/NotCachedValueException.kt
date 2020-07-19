package by.my.elections.data.datasource.storage.exception

class NotCachedValueException(key: String) : Exception("Key '$key' isn't cached")