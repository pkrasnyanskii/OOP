#!/bin/bash
set -e

rm -rf out docs
mkdir out docs

# Компиляция
javac -d out src/main/java/ru/nsu/krasnyanski/*.java

# Документация
javadoc -d docs -sourcepath src/main/java -subpackages ru.nsu.krasnyanski

# Создание JAR
jar cfe out/HeapSortDemo.jar ru.nsu.krasnyanski.HeapSortDemo -C out .

# Запуск JAR
java -jar out/HeapSortDemo.jar