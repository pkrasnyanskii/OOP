#!/bin/bash
set -e

rm -rf out docs
mkdir out docs

# Компиляция
javac -d out src/main/java/ru/nsu/krasnyanski/HeapSort.java src/main/java/ru/nsu/krasnyanski/HeapSortDemo.java

# Документация
javadoc -d docs -sourcepath src/main/java -subpackages ru.nsu.krasnyanski

# Запуск демо
java -cp out ru.nsu.krasnyanski.HeapSortDemo