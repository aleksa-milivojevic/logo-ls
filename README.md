# Logo Language Server
JetBrains Internship Application Project

---

## Description
Language Server made for Logo programming language.

Server capabilities:
* **Full text synchronisation**
* **Text highlighting** (Semantic Tokens)
* **Go-to-declaration** for functions and variables
* **Word completion**

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

## Project architecture

### Communication & Initialization
The server communicates with a client using JSON-RPC. During the handshake:
*   `LogoLauncher` creates the server instance and starts listening on `System.in`.
*   `LogoLanguageServer` reports its capabilities to the client, specifically enabling:
    *   **Full Text Synchronization**.
    *   **Completion Provider** with resolve capabilities.
    *   **Declaration Provider** for navigating to definitions.
    *   **Semantic Tokens Provider** for syntax highlighting.

### Text Synchronization & State
The server keeps track of the code state in `LogoTextDocumentService` using in-memory collections:
*   **`openDocs`**: Stores the URIs and current text content of all open documents.
*   **`declarations`**: A map linking variable or function names to their specific coordinates (`LocationLink`) in the source code.
*   **`declarationNames`**: A list of all user-defined symbols found during code scanning.

### Language Logic Implementation
The server processes Logo code through several key mechanisms:

*   **Tokenization**: It uses regular expressions in `semanticTokensFull` to identify keywords, numbers, and identifiers.
*   **Symbol Discovery**: The `findDeclarations` method parses the code for patterns like `to`, `define`, or `make` to index user-defined functions and variables.
*   **Intellisense**: The `completion` method suggests items by combining both language (stored in `LogoTokens` class) and user declared functions and variables.
*   **Semantic Highlighting**: Tokens are categorized by types such as `keyword`, `variable`, `function`, or `number` and returned to the client as a legend-mapped integer array.

---

## Licence
* This project is exclusively made for a JetBrains Internship Application. It is not licenced.