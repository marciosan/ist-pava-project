# IST PAva Project no.1

Advanced Programming project at [Técnico Lisboa](https://tecnico.ulisboa.pt/pt/)

Goal: Implementation of keyword parameters in Java using reflection and Javassist


# Run instructions

1. Compile source files and create jar:

	$ ant

2. Compile all tests in tests folder (with dependency for keyConstructors.jar):

	$ compileTests

3. Run (assuming test class files are in directory bin/tests):

	$ java -cp 'keyConstructors.jar:bin/tests' ist.meic.pa.KeyConstructors Test


