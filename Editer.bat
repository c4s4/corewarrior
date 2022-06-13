rem si vous utilisez le JRE, remplacer 
rem java -classpath %CLASSPATH%;bin.jar;lib.jar
rem par
rem jre -cp bin.jar;lib.jar

java -classpath %CLASSPATH%;bin.jar;lib.jar casa.corewarrior.editeur.Editeur %1
