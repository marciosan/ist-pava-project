# IST PAva Project no.1

Advanced Programming project at [Técnico Lisboa](https://tecnico.ulisboa.pt/pt/)

Goal: Implementation of keyword parameters in Java using reflection and Javassist


# Run instructions

1. Compile source files and create jar:

	$ ant

2. Compile all tests in tests folder (with dependency for keyConstructors.jar):

	$ compile

3. Run (assuming test class files are in directory bin/tests):

	$ java -cp 'keyConstructors.jar:bin/tests' ist.meic.pa.KeyConstructors Test


# Objectivos

1. Translator: parsing da class e dos valores da anotação
	Assegurar que atributos existem.
	Assegurar que valores condizem com class.

2. Guardar atributos e valores

3. Parsing dos argumentos do constructor
	"width", 100 -> width = 100;
	new Widget("width", 100, "nPatas", 5) -> erro, widget nao tem patas

	Assegurar que atributos existem.
	Assegurar que valores condizem com class.

4. Inicializar
