
# BO Universe Manager

`BoUniverseManager` is a CLI tool designed to manage SAP BO Universes. It allows users to generate Excel representations of universes, compare local and server universes, and apply changes to the SAP BO server.

## Usage

Run the application using the following syntax:

```bash
java -jar yourapp.jar <command> [options]
```

### Commands and Options

#### `get`
Generates an Excel file representing a SAP BO universe.

- **Arguments**:
  - `Universe Name`: *(Required)* The name of the SAP BO universe to export.
  - `-o, --output`: *(Optional)* Target path to save the Excel file. Defaults to the current directory if not specified.

#### `plan`
Compares local and server SAP BO universes using the specified file.

- **Arguments**:
  - `Input file`: *(Required)* Path to the input file for comparison.

#### `apply`
Applies changes to the SAP BO server from the specified file.

- **Arguments**:
  - `Input file`: *(Required)* Path to the input file containing changes.

### Examples

#### Export a SAP BO universe to Excel
```bash
java -jar yourapp.jar get "UniverseName" -o /path/to/output
```

#### Compare local and server universes
```bash
java -jar yourapp.jar plan /path/to/input/file
```

#### Apply changes to the SAP BO server
```bash
java -jar yourapp.jar apply /path/to/input/file
```

### Error Handling

- If no command is provided:
  ```bash
  Error: No command provided. Use 'get', 'plan', or 'apply'.
  ```
- If required arguments are missing:
  ```bash
  Error: Required argument missing for command: <command>
  ```
- If invalid options or arguments are used:
  ```bash
  Error parsing command-line arguments: <error_message>
  ```

### Help
To view the help message:
```bash
java -jar yourapp.jar --help
```

This will display detailed usage instructions and available options.
