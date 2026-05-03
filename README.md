# Logo Language Server
JetBrains Internship Application Project

---

## Description
Language Server made for Logo programming language.

Server capabilities:
* Text highlighting (Semantic Tokens)
* Go-to-declaration for functions and variables
* Word completion

---

## How to start
### 1. Make sure you have LSP4IJ plugin installed
* If not you can find it in `Settings` -> `Plugins...`.
### 2. Create a project JAR file
* In project console use `./gradlew clean jar` command.
### 3. Create a new language server using the plugin
* In the bottom left corner of Intellij IDEA you can find `Language Servers` tab
* When said tab is open in its top right corner there is a `New Language Server` option
* Set a server name of your choice (e.g. logo-ls)
* In the Command box enter `java -jar /path/to/your/jar/file`
* Mappings tab:
  * Add a file name pattern: pattern = `*.lgo`, language id = `logo`
  * Add file type: file type = `Logo File` or `Plain Text`, language id = `logo` (if Logo file option does not exist, you can create it in `Settings` -> `Editor` -> `File Types` and assign it a file name pattern = `*.lgo`)
### 4. Open a `.lgo` file in Intellij IDEA and test it out!

---

## Licence
* This project is exclusively made for a JetBrains Internship Application. It is not licenced.