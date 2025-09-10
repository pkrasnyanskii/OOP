#!/bin/bash
# exit при любой ошибке
set -e

# исходники
SRC_DIR=./src/main/java
# папка для скомпилированных классов
BIN_DIR=./out
# папка для документации
DOC_DIR=./docs
# имя jar
JAR_FILE=HeapSort.jar

# создаём папки
mkdir -p $BIN_DIR
mkdir -p $DOC_DIR

# компиляция всех Java-файлов
echo "Compiling sources"
javac -d $BIN_DIR $(find $SRC_DIR -name "*.java")

# генерация Javadoc
echo "Creating Javadoc"
javadoc -d $DOC_DIR $(find $SRC_DIR -name "*.java")

# упаковка в jar
echo "Packing JAR"
cd $BIN_DIR
jar cfe $JAR_FILE ru.nsu.krasnyanski.HeapSort ru/nsu/krasnyanski/*.class
cd ..

# запуск приложения
echo "Launching HeapSort"
java -cp $BIN_DIR/$JAR_FILE ru.nsu.krasnyanski.HeapSort
echo "Done!"